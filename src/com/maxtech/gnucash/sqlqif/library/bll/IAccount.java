package com.maxtech.gnucash.sqlqif.library.bll;

public interface IAccount {

    Iterable<ITransaction> Transactions();

    String getGuid();
    void setGuid(String guid);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    String getAccountType();
    void setAccountType(String accountType);
    String getHierarchy();
    void setHierarchy(String hierarchy);
    Integer getHierarchyLevel();
    void setHierarchyLevel(Integer hierarchyLevel);
}
