package com.maxtech.gnucash.sqlqif.library.bll;

import java.math.BigDecimal;

public interface IAccountSplit {
    IAccount getAccount();
    void setAccount(IAccount account);
    String getReconciled();
    void setReconciled(String reconciled);
    BigDecimal getTrxValue();
    void setTrxValue(BigDecimal trxValue);
}
