package com.cccwz;

import com.spring.CccwzApplicationContext;

public class Test {
    public static void main(String[] args) {
        CccwzApplicationContext applicationContext = new CccwzApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));


    }
}
