package com.revature.p1.main;

import com.revature.p1.models.AppUser;
import com.revature.p1.utils.Session;

public class Driver {


    public static void main(String[] args) {
        Session session = new Session();

        try {
            AppUser user = (AppUser) session.getEntityManager().get(AppUser.class, 3);
            System.out.println(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

