package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.List;

public interface IAccount {
    public String Guid();
    public String Name();
    public String Description();
    public String AccountType();
    public String Hierarchy();
    public Integer HieararchyLevel();
    public List<ITransaction> Transactions();
}
