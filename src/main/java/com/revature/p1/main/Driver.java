package com.revature.p1.main;

import com.revature.p1.models.Account;
import com.revature.p1.models.AppUser;
import com.revature.p1.models.User;
import com.revature.p1.repos.DataSource;
import com.revature.p1.utils.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Driver {

    public static void main(String[] args) {
       try {
           Connection connection = DataSource.getInstance().getConnection();
           Connection connection1 = DataSource.getInstance().getConnection();
           DataSource.getInstance().shutdown();
       } catch (Exception e) {
           e.printStackTrace();
       }

    }

}

