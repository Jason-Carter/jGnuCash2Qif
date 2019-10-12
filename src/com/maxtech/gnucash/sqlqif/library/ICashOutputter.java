package com.maxtech.gnucash.sqlqif.library;

import com.maxtech.gnucash.sqlqif.library.bll.IAccount;

public interface ICashOutputter {
    void Write(Iterable<IAccount> accounts, String outputFilename);
}
