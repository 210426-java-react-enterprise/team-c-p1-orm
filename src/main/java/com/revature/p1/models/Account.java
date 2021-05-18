package com.revature.p1.models;

import com.revature.p1.utils.annotations.Column;
import com.revature.p1.utils.annotations.Entity;
import com.revature.p1.utils.annotations.Key;

@Entity(name = "accounts")
public class Account {

    public Account(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Key
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    private double balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
