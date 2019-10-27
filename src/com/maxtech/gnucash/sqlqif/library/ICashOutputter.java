package com.maxtech.gnucash.sqlqif.library;

import com.maxtech.gnucash.sqlqif.library.bll.IAccount;

import java.util.ArrayList;

public interface ICashOutputter {
    void Write(ArrayList<IAccount> accounts, String outputFilename);
}
