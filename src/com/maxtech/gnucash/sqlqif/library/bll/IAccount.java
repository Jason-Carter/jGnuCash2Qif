package com.maxtech.gnucash.sqlqif.library.bll;

import sun.rmi.server.InactiveGroupException;

import java.util.List;

public interface IAccount {
    String Guid = null;
    String Name = null;
    String Description = null;
    String AccountType = null;
    String Hierarchy = null;
    Integer HierarchyLevel = null;
    Iterable<ITransaction> Transactions() ;

    public String getGuid();
    public void setGuid(String guid);
    public String getName();
    public void setName(String name);
    public String getDescription();
    public void setDescription(String description);
    public String getAccountType();
    public void setAccountType(String accountType);
    public String getHierarchy();
    public void setHierarchy(String hierarchy);
    public Integer getHierarchyLevel();
    public void setHierarchyLevel(Integer hierarchyLevel);
}
