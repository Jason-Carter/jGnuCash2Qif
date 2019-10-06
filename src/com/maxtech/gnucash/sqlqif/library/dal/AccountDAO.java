package com.maxtech.gnucash.sqlqif.library.dal;

import com.maxtech.gnucash.sqlqif.library.bll.IAccount;

import java.util.*;

public class AccountDAO {

    private String sql = "    with recursive cteAccounts(guid, name, account_type, parent_guid, code, description, hidden, placeholder, level, path) AS\n" +
            "            (\n" +
            "    select guid, name, account_type, parent_guid, code,\n" +
            "           description, hidden, placeholder, 0, ''\n" +
            "    from accounts\n" +
            "    where parent_guid is null\n" +
            "    and name = 'Root Account'\n" +
            "    union all\n" +
            "    select a.guid, a.name, a.account_type, a.parent_guid, a.code,\n" +
            "           a.description, a.hidden, a.placeholder, p.level + 1, p.path || ':' || a.name\n" +
            "    from        accounts a\n" +
            "    inner join  cteAccounts p on p.guid = a.parent_guid\n" +
            "    order by 9 desc -- by using desc we're doing a depth-first search\n" +
            "            )\n" +
            "    select substr('                                        ', 1, level* 10) || name 'hierarchy',\n" +
            "           name,\n" +
            "           guid,\n" +
            "           description,\n" +
            "           substr(path, 2, length(path)) 'path',\n" +
            "           account_type, level\n" +
            "    from    cteAccounts\n" +
            "    where account_type in ('ASSET', 'CREDIT', 'BANK', 'EXPENSE', 'INCOME', 'LIABILITY')";

    public Iterable<IAccount> Extract(String dataSource){

        String connectionString = String.format("DataSource=%s", dataSource);
        ArrayList<IAccount> accounts = new ArrayList<IAccount>();

        return accounts;
    }
}
