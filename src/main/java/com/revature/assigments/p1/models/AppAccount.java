package com.revature.assigments.p1.models;

import com.revature.assigments.p1.annotations.Column;
import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.annotations.Id;
import com.revature.assigments.p1.annotations.Table;

@Entity
@Table(name="app_account")
public class AppAccount {
    @Id
    @Column(name="acct_id", dataType="int")
    private int id;
    @Column(name="acct_type", dataType="int")
    private String accountType;
    @Column(name = "curr", dataType="int")
    private String currency;
    @Column(name="acct_balance", dataType="numeric")
    private double balance;

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

}
