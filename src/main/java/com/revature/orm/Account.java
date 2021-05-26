package com.revature.orm;


import com.revature.orm.annotations.ColumnType;
import com.revature.orm.annotations.MyColumn;
import com.revature.orm.annotations.MyEntity;
import com.revature.p1.entities.Transaction;

import java.util.ArrayList;
import java.util.List;


@MyEntity(name = "account")
public class Account extends MySavable
{
    @MyColumn(name = "balance", nullable = false, unique = false, type = ColumnType.DECIMAL,
              length = 0, pk = false, fk = false, reference = "", delete = "cascade")
    private double balance;

    @MyColumn(  name = "number",nullable = false,unique = true,type = ColumnType.SERIAL,
            length = 10,pk = true, fk = false,reference = "",delete = "cascade")
    private int number;

    @MyColumn(  name = "type",nullable = false,unique = false,type = ColumnType.VARCHAR,
            length = 10,pk = false,fk = false,reference = "",delete = "cascade")
    private String type;

    @MyColumn(name = "customer_ssn", type = ColumnType.VARCHAR, length = 9, nullable = false,unique = false,
              pk = false, fk = true, reference = "project1.customer(ssn)", delete = "cascade")
    private String customerSsn;

    protected List<Transaction> transactions;

    public Account()
    {
    }

    public Account(String ssn)
    {
        this.number = 0;
        this.balance = 0;
        this.customerSsn = ssn;
        this.type = "";
        this.transactions = new ArrayList<>();
    }
    protected Account(int number, double balance)
    {
        this.number = number;
        this.balance = balance;
        this.customerSsn = "";
        this.type = "";
        this.transactions = new ArrayList<>();
    }

    protected Account(int number, double balance, List<Transaction> transactions)
    {
        this.balance = balance;
        this.number = number;
        this.customerSsn = "";
        this.type = "";
        this.transactions.addAll(transactions);
    }

    protected Account(double balance, int number, String type, String customerSsn)
    {
        this.balance = balance;
        this.number = number;
        this.type = type;
        this.customerSsn = customerSsn;
        this.transactions = new ArrayList<>();
    }

    public Account(MySavable savable)
    {
        this.balance = ((Account) savable).balance;
        this.customerSsn = ((Account) savable).customerSsn;
        this.number = ((Account) savable).number;
        this.transactions = new ArrayList<>();
        this.transactions.addAll(((Account) savable).transactions);
        this.type = ((Account) savable).type;
    }

    public void setTransactions(List<Transaction> transactions)
    {
        this.transactions.addAll(transactions);
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public String getCustomerSsn()
    {
        return customerSsn;
    }

    public void setCustomerSsn(String customerSsn)
    {
        this.customerSsn = customerSsn;
    }

    public double getBalance()
    {
        return balance;
    }

    public int getNumber()
    {
        return number;
    }

    public List<Transaction> getTransactions()
    {
        return transactions;
    }

    public void updateBalance()
    {
        if (!transactions.isEmpty())
        {
            balance = transactions.get(transactions.size()- 1).getBalance();
        }
    }
    public double deposit(double amount){return 0;};
    public double withdraw(double amount){return 0;};
}
