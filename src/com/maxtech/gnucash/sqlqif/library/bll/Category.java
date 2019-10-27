package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.ArrayList;

public class Category implements IAccount {

    private String Guid;
    private String Name;
    private String Description;
    private String AccountType;
    private String Hierarchy;
    private int HierarchyLevel;
    private ArrayList<ITransaction> Transactions;

    public Category() {
        Transactions = new ArrayList<>();
    }

    @Override
    public ArrayList<ITransaction> Transactions() {
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
