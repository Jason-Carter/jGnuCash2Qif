package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.Date;

public interface ITransaction {
    String getTransactionGuid();
    void setTransactionGuid(String guid);
    Date getDatePosted();
    void setDatePosted(Date datePosted);
    String getRef();
    void setRef(String ref);
    String getDescription();
    void setDescription(String description);
    String getMemo();
    void setMemo(String memo);

    Iterable<IAccountSplit> AccountSplits();
    Iterable<IAccount> ParentAccounts();
}
