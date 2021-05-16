package com.revature.assigments.p1.models;

import java.util.ArrayList;

public class AppUser {

    private int id;
    private  String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<AppAccount> accounts = new ArrayList<AppAccount>();


    public AppUser(){
        super();
    }

    public AppUser(String username, String password, String firstName, String lastName, String email){
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<AppAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<AppAccount> accounts) {
        this.accounts = accounts;
    }

    public void addAccountToUser(AppAccount account){
        this.accounts.add(account);
    }


}
