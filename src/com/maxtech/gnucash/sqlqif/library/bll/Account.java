package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.ArrayList;

public class Account implements IAccount {
    public Account ()
    {
        Transactions = new ArrayList<ITransaction>();
    }

    public String Guid;
    public String Name;
    public String Description;
    public String AccountType;
    public String Hierarchy;
    public int HierarchyLevel;
    public Iterable<ITransaction> Transactions;

    @Override
    public Iterable<ITransaction> Transactions() {
        return Transactions;
    }

    @Override
    public String getGuid() {
        return Guid;
    }

    @Override
    public void setGuid(String guid) {
        Guid = guid;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public void setName(String name) {
        Name = name;
    }

    @Override
    public String getDescription() {
        return Description;
    }

    @Override
    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public String getAccountType() {
        return AccountType;
    }

    @Override
    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    @Override
    public String getHierarchy() {
        return Hierarchy;
    }

    @Override
    public void setHierarchy(String hierarchy) {
        Hierarchy = hierarchy;
    }

    @Override
    public Integer getHierarchyLevel() {
        return HierarchyLevel;
    }

    @Override
    public void setHierarchyLevel(Integer hierarchyLevel) {
        HierarchyLevel = hierarchyLevel;
    }
}
