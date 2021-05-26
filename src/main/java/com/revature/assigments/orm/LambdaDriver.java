package com.revature.assigments.orm;

import com.revature.assigments.orm.models.MyCustomPrint;


public class LambdaDriver {

    public static void main(String[] args) {

        // My first anonymous class
        String str = "Hello World!!!";
        System.out.println("My regular print >> " + str);

        MyCustomPrint functionOne = new MyCustomPrint() {
            @Override
            public void print(String str) {
                System.out.println("My anonymous print>> "+str);
            }
        };

        functionOne.print(str);


        //My first Lambda
        MyCustomPrint functionTwo = (String) -> System.out.println("My Lambda print >> "+str);

        functionTwo.print(str);

    }
}





