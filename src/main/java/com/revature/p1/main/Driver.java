package com.revature.p1.main;

import com.revature.p1.models.Account;
import com.revature.p1.models.AppUser;
import com.revature.p1.models.User;
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

        Session session = new Session();
        try {
            AppUser user = (AppUser) session.get(AppUser.class, 4);
            System.out.println(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

