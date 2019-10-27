package com.maxtech.gnucash.sqlqif.library;

import com.maxtech.gnucash.sqlqif.library.bll.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class QifCashOutputter implements ICashOutputter {

    SimpleDateFormat qifDateFormat = new SimpleDateFormat("MM/d/yyyy");

    @Override
    public void Write(ArrayList<IAccount> accounts, String outputFilename) {

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {

            System.out.println("INFO: Writing category section...");
            WriteCategoryList(accounts, writer);
            System.out.println("INFO: Writing accounts section...");
            WriteAccountList(accounts, writer);
            System.out.println("INFO: Writing transactions by accounts...");
            WriteTransactionListByAccount(accounts, writer);
        }
        catch(IOException ioEx) {
            System.err.println(String.format("ERROR: The following error occurred while writing to the output file (%s): %s", outputFilename, ioEx.getMessage()));
        }

    }

    private void WriteTransactionListByAccount(ArrayList<IAccount> accounts, BufferedWriter writer) throws IOException {
        writer.write("!Option:AutoSwitch"); // Indicates start of the account list (with transactions this time)
        writer.newLine();

        // Transaction section by account
        accounts.stream().filter(n -> IsAccount(n.getAccountType()))
                .forEach(n -> {
                    try {
                        QifAccountTransactionOutput(n, writer);
                    } catch (IOException ioEx) {
                        System.err.println(String.format("ERROR: The following error occurred while writing the transaction list by account : %s", ioEx.getMessage()));
                    }
                });
    }

    private void QifAccountTransactionOutput(IAccount acc, BufferedWriter writer) throws IOException {
        QifAccountTransactionHeaderOutput(acc, writer);
        acc.Transactions().forEach(t -> {
            try {
                QifTransactionOutput(acc, t, writer);
            } catch (IOException ioEx) {
                System.err.println(String.format("ERROR: The following error occurred while writing the QIF Account Transactions : %s", ioEx.getMessage()));
            }
        });
    }

    private void QifTransactionOutput(IAccount parentAcc, ITransaction trx, BufferedWriter writer) throws IOException {

        // Use the transaction value of the parent account
        Optional<IAccountSplit> mainParentAccount = trx.AccountSplits().stream()
                                        .filter(ac -> IsAccount(ac.getAccount().getAccountType()) &&
                                                ac.getAccount().getGuid().equals(parentAcc.getGuid()))
                                        .findFirst();

        // an account split object with default values
        AccountSplit defaultAccSplit = new AccountSplit();
        defaultAccSplit.setTrxValue(BigDecimal.valueOf(0));
        defaultAccSplit.setReconciled("n");
        defaultAccSplit.setAccount(new Account());
        defaultAccSplit.getAccount().setName("");

        String accountRef = "";

        if (trx.AccountSplits().stream().anyMatch(ac -> IsCategory(ac.getAccount().getAccountType())))
        {
            // Has one or more category accounts
            // TODO: Consider supporting multiple splits, for now just grab the first one
            Optional<IAccountSplit> cat = trx.AccountSplits().stream().filter(ac -> IsCategory(ac.getAccount().getAccountType())).findFirst();
            accountRef += cat.orElse(defaultAccSplit).getAccount().getName();
            // Report the account split for future investigation
            if (trx.AccountSplits().stream().filter(ac -> IsCategory(ac.getAccount().getAccountType())).count() > 2)
            {
                System.err.println(String.format("WARNING: Account (Date: %s / Description: %s) has multiple categories. Only one is exported",
                                                ConvertToQifDateFormat(trx.getDatePosted()),
                                                trx.getDescription()));
            }
        }
        else if (trx.AccountSplits().stream().filter(ac -> IsAccount(ac.getAccount().getAccountType())).count() > 1)
        {
            // Has more than one account so is an account transfer
            //TODO: Check if there could be multiple account transfers in a split - tricky
            Optional<IAccountSplit> acc = trx.AccountSplits().stream().filter(ac -> IsAccount(ac.getAccount().getAccountType()) && ac.getAccount().getGuid() != parentAcc.getGuid()).findFirst();
            accountRef += String.format("[%s]", acc.orElse(defaultAccSplit).getAccount().getName());
        }
        else
        {
            System.err.println(String.format("WARNING: Transaction %s has no categories and only one account reference (%s)", trx.toString(), mainParentAccount.toString()));
        }

        writer.write(String.format("D%s", ConvertToQifDateFormat(trx.getDatePosted())));
        writer.newLine();
        if (trx.getRef() != null && !trx.getRef().equals(""))
        {
            writer.write(String.format("N%s", trx.getRef()));
            writer.newLine();
        }

        writer.write(String.format("U%s", mainParentAccount.orElse(defaultAccSplit).getTrxValue().toString()));
        writer.newLine();
        writer.write(String.format("T%s", mainParentAccount.orElse(defaultAccSplit).getTrxValue().toString()));
        writer.newLine();
        writer.write(String.format("P%s", trx.getDescription()));
        writer.newLine();
        writer.write(String.format("M%s", trx.getMemo()));
        writer.newLine();
        if (IsReconciled(mainParentAccount.orElse(defaultAccSplit).getReconciled()))
        {
            writer.write("C*");
            writer.newLine();
        }
        writer.write(String.format("L%s", accountRef));
        writer.newLine();
        writer.write("^");
        writer.newLine();
    }

    private String ConvertToQifDateFormat(Date nonQifDate)
    {
        return qifDateFormat.format(nonQifDate);
    }

    private void QifAccountTransactionHeaderOutput(IAccount acc, BufferedWriter writer) throws IOException {
        writer.write("!Account");
        writer.newLine();
        writer.write(String.format("N%s", acc.getName()));
        writer.newLine();
        writer.write(String.format("T%s", QifAccountType(acc.getAccountType())));
        writer.newLine();
        writer.write("^");
        writer.newLine();
        writer.write(String.format("!Type:%s", QifAccountType(acc.getAccountType())));
        writer.newLine();
    }

    private void WriteAccountList(ArrayList<IAccount> accounts, BufferedWriter writer) throws IOException {
        // Account section (asset / credit / bank / liability accounts)

        writer.write("!Option:AutoSwitch"); // Indicates start of the account list
        writer.newLine();
        writer.write("!Account");
        writer.newLine();
        accounts.stream()
                .filter(n -> IsAccount(n.getAccountType()))
                .forEach(n -> {
                    try {
                        QifAccountOutput(n, writer);
                    } catch (IOException ioEx) {
                        System.err.println(String.format("ERROR: The following error occurred while writing the account list : %s", ioEx.getMessage()));
                    }
                });

        writer.write("!Clear:AutoSwitch");  // Indicates end of the account list
        writer.newLine();
    }

    private void QifAccountOutput(IAccount acc, BufferedWriter writer) throws IOException {
        writer.write(String.format("N%s", acc.getName()));
        writer.newLine();
        writer.write(String.format("T%s", QifAccountType(acc.getAccountType())));
        writer.newLine();
        writer.write(String.format("D%s", acc.getDescription()));
        writer.newLine();
        writer.write("^");
        writer.newLine();
    }

    private void WriteCategoryList(ArrayList<IAccount> accounts, BufferedWriter writer) throws IOException {
        // Category section (expense / income accounts)
        writer.write("!Type:Cat");
        writer.newLine();
        accounts.stream()
                .filter(n -> IsCategory(n.getAccountType()))
                .forEach(n -> {
                    try {
                        QifCategoryOutput(n, writer);
                    } catch (IOException ioEx) {
                        System.err.println(String.format("ERROR: The following error occurred while writing the category list : %s", ioEx.getMessage()));
                    }
                });
    }

    private void QifCategoryOutput(IAccount cat, BufferedWriter writer) throws IOException {
        writer.write(String.format("N%s", cat.getName()));
        writer.newLine();
        writer.write(String.format("D%s", cat.getDescription()));
        writer.newLine();
        writer.write(String.format("%s", QifCategoryType(cat.getAccountType())));
        writer.newLine();
        writer.write("^");
        writer.newLine();
    }

    private boolean IsReconciled(String isReconciled) {
        // Assuming 'cleared' accounts are reconciled
        return (isReconciled.toLowerCase().equals("y") ||
                isReconciled.toLowerCase().equals("c"));
    }

    private boolean IsCategory(String catType) {
        return catType.equals("EXPENSE") ||
                catType.equals("INCOME");
    }

    private boolean IsAccount(String catType) {
        return catType.equals("ASSET") ||
                catType.equals("CREDIT") ||
                catType.equals("BANK") ||
                catType.equals("LIABILITY");
    }

    private String QifCategoryType(String catType)
    {
        String qifCatType = catType.equals("INCOME") ? "I" :
                            catType.equals("EXPENSE") ? "E" :
                            "?";

        if (qifCatType.equals("?")) {
            System.err.println(String.format("WARNING: Unknown category type: %s", catType));
        }

        return qifCatType;
    }

    private String QifAccountType(String accType) {

        String qifAccType = accType.equals("BANK") ? "Bank" :
                            accType.equals("CREDIT") ? "CCard" :
                            accType.equals("ASSET") ? "Oth A" :
                            accType.equals("LIABILITY") ? "Oth L" :
                            "?";

        if (qifAccType.equals("?")) {
            System.err.println(String.format("WARNING: Unknown account type: %s", accType));
        }

        return qifAccType;
    }
}
