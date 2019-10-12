package com.maxtech.gnucash.sqlqif.library.bll;

import java.util.ArrayList;
import java.util.Date;

public class Transaction implements ITransaction {

    public Transaction() {
        AccountSplits = new ArrayList<>();
        ParentAccounts = new ArrayList<>();
    }

    private String TransactionGuid;
    private Date DatePosted;
    private String Ref;
    private String Description;
    private String Memo;

    private Iterable<IAccountSplit> AccountSplits;
    private Iterable<IAccount> ParentAccounts;

    @Override
    public String getTransactionGuid() {
        return TransactionGuid;
    }

    @Override
    public void setTransactionGuid(String guid) {
        TransactionGuid = guid;
    }

    @Override
    public Date getDatePosted() {
        return DatePosted;
    }

    @Override
    public void setDatePosted(Date datePosted) {
        DatePosted = datePosted;
    }

    @Override
    public String getRef() {
        return Ref;
    }

    @Override
    public void setRef(String ref) {
        Ref = ref;
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
    public String getMemo() {
        return Memo;
    }

    @Override
    public void setMemo(String memo) {
        Memo = memo;
    }

    @Override
    public Iterable<IAccountSplit> AccountSplits() {
        return AccountSplits;
    }

    @Override
    public Iterable<IAccount> ParentAccounts() {
        return ParentAccounts;
    }
}
