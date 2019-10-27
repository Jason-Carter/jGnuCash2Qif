package com.maxtech.gnucash.sqlqif.library.dal;

import com.maxtech.gnucash.sqlqif.library.bll.*;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class TransactionDAO {

    public Iterable<ITransaction> Extract(String dataSource, ArrayList<IAccount> accounts) throws Exception {

        String connectionString = String.format("jdbc:sqlite:%s", dataSource);
        ArrayList<ITransaction> transactions = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        Connection connection = null;

        try
        {
            // create a database connection
            connection = DriverManager.getConnection(connectionString);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            String sql = "       with recursive cteCategories(guid, name, account_type, parent_guid, code, description, hidden, placeholder, level, path) AS\n" +
                    "            (\n" +
                    "                select  guid, name, account_type, parent_guid, code,\n" +
                    "                        description, hidden, placeholder, 0, ''\n" +
                    "                from    accounts \n" +
                    "                where   parent_guid is null\n" +
                    "                and     name = 'Root Account'\n" +
                    "                union all\n" +
                    "                select      a.guid, a.name, a.account_type, a.parent_guid, a.code,\n" +
                    "                            a.description, a.hidden, a.placeholder, p.level + 1, p.path || ':' || a.name\n" +
                    "                from        accounts a\n" +
                    "                inner join  cteCategories p on p.guid = a.parent_guid\n" +
                    "                where       a.account_type in ('EXPENSE', 'INCOME')\n" +
                    "                order by 9 desc -- by using desc we're doing a depth-first search\n" +
                    "            ),\n" +
                    "            cteAccounts(guid, name, account_type, description) as\n" +
                    "            (\n" +
                    "                select      acc.guid, acc.name, acc.account_type, acc.description\n" +
                    "                from        accounts  acc\n" +
                    "                inner join  accounts  p   on p.guid = acc.parent_guid\n" +
                    "                                          and p.parent_guid is null\n" +
                    "                                          and p.account_type = 'ROOT'\n" +
                    "                                          and p.name = 'Root Account'\n" +
                    "                where acc.account_type in ('ASSET', 'CREDIT', 'BANK', 'LIABILITY')\n" +
                    "            )\n" +
                    "            select\n" +
                    "                            t.guid              as TrxGuid,\n" +
                    "                            acc.guid            as AccGuid,\n" +
                    "                            acc.name            as AccountName,\n" +
                    "                            t.post_date         as DatePosted,\n" +
                    "                            t.Num               as Ref,\n" +
                    "                            t.Description,\n" +
                    "                            sl.string_val       as Notes,\n" +
                    "                            cat.guid            as CategoryGuid,\n" +
                    "                            cat.name            as Transfer,\n" +
                    "                            s.reconcile_state   as isReconciled,\n" +
                    "                            case acc.account_type\n" +
                    "                                when 'EQUITY' then ROUND((s.value_num / -100.0), 2)\n" +
                    "                                else ROUND((s.value_num / 100.0), 2)\n" +
                    "                            end as trxValue\n" +
                    "            from            splits        as s\n" +
                    "            inner join      transactions  as t    on t.guid = s.tx_guid\n" +
                    "            left outer join cteAccounts   as acc  on acc.guid = s.account_guid\n" +
                    "            left outer join cteCategories as cat  on cat.guid = s.account_guid\n" +
                    "            left outer join slots         as sl   on sl.obj_guid = t.guid and sl.name = 'notes'\n" +
                    "            order by        t.guid,\n" +
                    "                            t.post_date asc\n";

            ResultSet rs = statement.executeQuery(sql);
            while(rs.next())
            {
                // read the result set
                Transaction trx = AddTransaction(rs.getString("TrxGuid"), transactions);

                // Lookup the accounts or Categories and add object references
                String accountGuid = rs.getString("AccGuid");
                String categoryGuid = rs.getString("CategoryGuid");
                BigDecimal trxValue = rs.getBigDecimal("trxValue");
                String isReconciled = rs.getString("isReconciled");

                AddAccountSplit(accountGuid, trx, accounts, trxValue, isReconciled);
                AddAccountSplit(categoryGuid, trx, accounts, trxValue);

                trx.setDatePosted(formatter.parse(rs.getString("DatePosted")));
                trx.setRef(rs.getString("Ref"));
                trx.setDescription(rs.getString("Description"));
                trx.setMemo(rs.getString("Notes"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        return transactions;
    }

    private Transaction AddTransaction(String trxGuid, ArrayList<ITransaction> transactions) {

        Transaction trx;
        Optional<ITransaction> optTrx = transactions.stream().filter(t -> t.getTransactionGuid().equals(trxGuid)).findFirst();

        if (optTrx.isPresent()) {
            trx = (Transaction) optTrx.get();
        }
        else {
            trx = new Transaction();
            trx.setTransactionGuid(trxGuid);

            transactions.add(trx);
        }

        return trx;
    }

    private void AddAccountSplit(String accountGuid, Transaction trx, ArrayList<IAccount> accounts, BigDecimal trxValue) throws Exception {
        AddAccountSplit(accountGuid, trx, accounts, trxValue, "n");
    }

    private void AddAccountSplit(String accountGuid, Transaction trx, ArrayList<IAccount> accounts, BigDecimal trxValue, String isReconciled) throws Exception {

        if (accountGuid == null) {
            return;
        }

        // Transactions belong to one or more accounts, and may represent a transfer between
        // accounts, in which case it will have two account references

        Optional<IAccount> optAccount = accounts.stream().filter(a -> a.getGuid().equals(accountGuid)).findFirst();

        if (!optAccount.isPresent()) {
            // This should never happen unless there's a problem with the gnucash database
            throw new Exception("Unknown account on transaction {accountGuid}, could be due to corrupt GnuCash database");
            //TODO: If this is a problem we may well be able to simply ignore it and return from function without an error...
        }

        IAccount account = optAccount.get(); // Extract tne not null account reference from the optional wrapper

        IAccountSplit accSplit = new AccountSplit();
        accSplit.setAccount(account);
        accSplit.setReconciled(isReconciled);
        accSplit.setTrxValue(trxValue);

        if (!IsCategory(account.getAccountType())) {
            ((ArrayList<ITransaction>) account.Transactions()).add(trx);
            ((ArrayList<IAccount>) trx.ParentAccounts()).add(account);
        }

        ((ArrayList<IAccountSplit>) trx.AccountSplits()).add(accSplit);
    }

    private boolean IsCategory(String catType) {
        return catType.equals("EXPENSE") || catType.equals("INCOME");
    }
}
