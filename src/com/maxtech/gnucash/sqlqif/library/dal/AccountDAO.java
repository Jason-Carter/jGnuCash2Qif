package com.maxtech.gnucash.sqlqif.library.dal;

import com.maxtech.gnucash.sqlqif.library.bll.Account;
import com.maxtech.gnucash.sqlqif.library.bll.Category;
import com.maxtech.gnucash.sqlqif.library.bll.IAccount;

import java.sql.*;
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

        String connectionString = String.format("jdbc:sqlite:%s", dataSource);
        ArrayList<IAccount> accounts = new ArrayList<IAccount>();

        Connection connection = null;

        try
        {
            // create a database connection
            connection = DriverManager.getConnection(connectionString);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rs = statement.executeQuery(sql);
            while(rs.next())
            {
                // read the result set

                String accountType = rs.getString("account_type");

                switch (accountType)
                {
                    case "ASSET":
                    case "CREDIT":
                    case "BANK":
                        Account account = new Account();
                        account.Guid = rs.getString("guid");
                        account.Name = rs.getString("path");
                        account.Description = rs.getString("description");
                        account.AccountType = rs.getString("account_type");
                        account.Hierarchy = rs.getString("hierarchy");
                        accounts.add(account);
                        break;
                    case "EXPENSE":
                    case "INCOME":
                        Category category = new Category();
                        category.Guid = rs.getString("guid");
                        category.Name = rs.getString("path");
                        category.Description = rs.getString("description");
                        category.AccountType = rs.getString("account_type");
                        category.Hierarchy = rs.getString("hierarchy");
                        accounts.add(category);
                        break;
                }
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        return accounts;
    }
}
