package com.maxtech.gnucash.sqlqif.library.bll;

import java.math.BigDecimal;

public class AccountSplit implements IAccountSplit {

    private IAccount Account;
    private String Reconciled;
    private BigDecimal TrxValue;

    @Override
    public IAccount getAccount() {
        return Account;
    }

    @Override
    public void setAccount(IAccount account) {
        Account = account;
    }

    @Override
    public String getReconciled() {
        return Reconciled;
    }

    @Override
    public void setReconciled(String reconciled) {
        Reconciled = reconciled;
    }

    @Override
    public BigDecimal getTrxValue() {
        return TrxValue;
    }

    @Override
    public void setTrxValue(BigDecimal trxValue) {
        TrxValue = trxValue;
    }
}
