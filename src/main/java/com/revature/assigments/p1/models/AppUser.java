package com.revature.assigments.p1.models;

import com.revature.assigments.p1.annotations.Column;
import com.revature.assigments.p1.annotations.Entity;
import com.revature.assigments.p1.annotations.Id;
import com.revature.assigments.p1.annotations.Table;

import java.util.ArrayList;

@Entity ()
@Table(name = "app_user")
public class AppUser {

    @Id(name = "user_id")
    @Column(name = "user_id", dataType = "int")
    private int id;
    @Column(name ="username", dataType = "varchar(256)", unique = "unique", notNull = "not null" )
    private  String username;
    @Column(name ="password", dataType = "varchar(256)", unique = "unique", notNull = "not null" )
    private String password;
    @Column(name ="first_name", dataType = "varchar(256)", unique = "unique", notNull = "not null" )
    private String firstName;
    @Column(name ="last_name", dataType = "varchar(256)", unique = "unique", notNull = "not null" )
    private String lastName;
    @Column(name ="email", dataType = "varchar(256)", unique = "unique", notNull = "not null" )
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
