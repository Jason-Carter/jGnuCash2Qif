package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.Date;
import java.util.List;

public interface ITransaction {
    public String TransactionGuid();
    public Date DatePosted();
    public String Ref();
    public String Description();
    public String Memo();
    public List<IAccountSplit> AccountSplits();
    public List<IAccount> ParentAccounts();
}
