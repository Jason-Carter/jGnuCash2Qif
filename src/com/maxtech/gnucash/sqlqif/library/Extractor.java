package com.maxtech.gnucash.sqlqif.library;

import com.maxtech.gnucash.sqlqif.library.bll.IAccount;
import com.maxtech.gnucash.sqlqif.library.bll.ITransaction;
import com.maxtech.gnucash.sqlqif.library.dal.AccountDAO;
import com.maxtech.gnucash.sqlqif.library.dal.TransactionDAO;

import java.util.ArrayList;

public class Extractor {
    public void ExtractData(String datasource, String outputFilename) {
        System.out.println("INFO: Extracting accounts...");
        ArrayList<IAccount> accounts = (ArrayList<IAccount>) new AccountDAO().Extract(datasource);

        // debugging, just printing these out for now...
        accounts.forEach(a -> System.out.println(a.getName()));

        System.out.println("INFO: Extracting transactions...");
        try {
            ArrayList<ITransaction> transactions = (ArrayList<ITransaction>) new TransactionDAO().Extract(datasource, accounts);
            transactions.forEach(t -> System.out.println(t.getDescription()));
        }
        catch(Exception ex) {
            System.err.println(String.format("ERROR: The following error occurred while extracting transactions: %s", ex.getMessage()));
        }
    }
}
