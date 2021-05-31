package com.revature.assigments.orm.models;

import com.revature.assigments.orm.annotations.Column;
import com.revature.assigments.orm.annotations.Entity;
import com.revature.assigments.orm.annotations.Id;
import com.revature.assigments.orm.annotations.Table;

@Entity
@Table(name="app_account")
public class AppAccount {
    @Id(name = "acct_id")
    @Column(name="acct_id", dataType="int")
    private int id;
    @Column(name="acct_type",dataType = "varchar(256)", unique = "", notNull = "not null")
    private String accountType;
    @Column(name = "curr", dataType = "varchar(256)", unique = "", notNull = "not null")
    private String currency;
    @Column(name="acct_balance", dataType="numeric")
    private double balance;
    @Column(name="holder_id", dataType = "int", unique="unique", notNull = "not null")
    private int holderId;

    public AppAccount() { super();}

    public AppAccount(String accountType, String currency, double balance) {
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
    }

    public AppAccount(int id, String accountType, String currency, double balance) {
        this.id = id;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
    }
    
    public AppAccount(int id, String accountType, String currency, double balance, int holder_id) {
        this.id = id;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.holderId = holder_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addToBalance(double amount){
        this.balance+=amount;
    }

    public void subtractToBalance(double amount){this.balance-=amount;}
    
    public int getHolderId() {
        return holderId;
    }
    
    public void setHolderId(int holderId) {
        this.holderId = holderId;
    }
}
