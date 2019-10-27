package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.ArrayList;

public interface IAccount {

    ArrayList<ITransaction> Transactions();

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
