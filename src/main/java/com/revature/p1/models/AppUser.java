package com.revature.p1.models;

import com.revature.p1.utils.Column;
import com.revature.p1.utils.Entity;
import com.revature.p1.utils.Key;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User
 * <p>
 * POJO to represent Users within the banking application
 */
@Entity(name = "app_users")
public class AppUser {
    @Key
    @Column(name = "user_id")
    private int userID;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private LocalDateTime birthday; //TODO calculate age from birthday
    @Column
    private LocalDateTime joinedDate;
    @Column
    private int age;

    public AppUser() {

    }

    public AppUser(int userID, String userName, String password, String email, String firstName, String lastName, LocalDateTime birthday,
                int age) {
        this.userID = userID;
        this.setUserName(userName);
        this.setPassword(password);
        this.setEmail(email);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setBirthday(birthday);
        this.setJoinedDate(LocalDateTime.now());
        this.setAge(age);
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getBirthdayFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00");
        return formatter.format(birthday);
    }

    public String getJoinedDayFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00");
        return formatter.format(joinedDate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("userID=").append(userID);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", birthday=").append(birthday);
        sb.append(", joinedDate=").append(joinedDate);
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}