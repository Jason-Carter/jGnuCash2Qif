package com.maxtech.gnucash.sqlqif.library;

import com.maxtech.gnucash.sqlqif.library.bll.IAccount;
import com.maxtech.gnucash.sqlqif.library.dal.AccountDAO;

import java.util.ArrayList;

public class Extractor {
    public void ExtractData(String datasource, String outputFilename) {
        System.out.println("INFO: Extracting accounts...");
        ArrayList<IAccount> accounts = (ArrayList<IAccount>) (new AccountDAO()).Extract(datasource);



        System.out.println("INFO: Extracting transactions...");
    }
}
