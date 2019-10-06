package com.maxtech.gnucash.sqlqif.library.bll;

import java.math.BigDecimal;

public interface IAccountSplit {
    public IAccount Account();
    public String Reconciled();
    public BigDecimal TrxValue();
}
