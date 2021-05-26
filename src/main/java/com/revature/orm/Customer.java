package com.revature.orm;


import com.revature.orm.annotations.ColumnType;
import com.revature.orm.annotations.MyColumn;
import com.revature.orm.annotations.MyEntity;
import com.revature.p1.entities.Credential;

import java.util.ArrayList;
import java.util.List;


@MyEntity(name = "customer")
public class Customer extends MySavable
{
    @MyColumn(name = "first_name", type = ColumnType.VARCHAR, length = 20,
              nullable = false, pk = false, fk = false, reference = "", unique = false, delete = "cascade")
    private String firstName;

    @MyColumn(  name = "last_name",type = ColumnType.VARCHAR,length = 20,
                nullable = false,pk = false,fk = false,reference = "",unique = false,delete = "cascade")
    private String lastName;

    @MyColumn(  name = "ssn",type = ColumnType.VARCHAR,length = 12,
                nullable = false,pk = true,fk = false,reference = "",unique = true,delete = "cascade")
    private String ssn;

    @MyColumn(  name = "email",type = ColumnType.VARCHAR,length = 20,
                nullable = false,pk = false,fk = false,reference = "",unique = false,delete = "cascade")
    private String email;

    @MyColumn(  name = "phone",type = ColumnType.VARCHAR,length = 15,
                nullable = false,pk = false,fk = false,reference = "",unique = false,delete = "cascade")
    private String phone;


    private List<? extends MySavable> accounts;
    private Credential credential;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSsn() {
        return ssn;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public List<? extends MySavable> getAccounts()
    {
        return accounts;
    }

    public void setAccounts(List<? extends MySavable> accounts)
    {
        this.accounts = accounts;
    }

    public Credential getCredential()
    {
        return credential;
    }

    public void setCredential(Credential credential)
    {
        this.credential = credential;
    }

    public Customer(String firstName, String lastName, String ssn, String email, String phone, Credential credential)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.email = email;
        this.phone = phone;
        this.credential = credential;
        this.accounts = new ArrayList<>();
    }
    public Customer(MySavable savable)
    {
        this.email = ((Customer) savable).email;
        this.firstName = ((Customer) savable).firstName;
        this.lastName = ((Customer) savable).lastName;
        this.phone = ((Customer) savable).phone;
        this.accounts = ((Customer) savable).accounts;
        this.ssn = ((Customer) savable).ssn;
        this.credential = ((Customer) savable).credential;
    }

    public Customer(String ssn)
    {
        this.ssn = ssn;
        accounts = new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return "Customer{" +
                       "firstName='" + firstName + '\'' +
                       ", lastName='" + lastName + '\'' +
                       ", ssn='" + ssn + '\'' +
                       ", email='" + email + '\'' +
                       ", phone='" + phone + '\'' +
                       ", accounts=" + accounts +
                       ", credential=" + credential +
                       '}';
    }
}
