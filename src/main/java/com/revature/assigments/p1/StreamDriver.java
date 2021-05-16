package com.revature.assigments.p1;

import com.revature.assigments.p1.models.AppUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StreamDriver {

    public static void main(String[] args) {

        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser("jane.doe","password","Jane","Doe","jane.doe@gmail.com"));
        users.add(new AppUser("john.doe","password","John","Doe","john.doe@yahoo.com"));
        users.add(new AppUser("mattr.doe","password","Matt","Doe","matt.doe@gmail.com"));
        users.add(new AppUser("aaron.doe","password","Aaron","Doe","aaron.doe@gmail.com"));
        users.add(new AppUser("Noah.doe","password","Noah","Doe","noah.doe@outlook.com"));

        List<AppUser> outlookUsers = users.stream().filter(user -> user.getEmail().contains("gmail.com"))
                                                   .collect(Collectors.toList());


        outlookUsers.forEach(AppUser -> System.out.println(AppUser.getEmail()));

        outlookUsers = new Consumer<>()
    }

}
