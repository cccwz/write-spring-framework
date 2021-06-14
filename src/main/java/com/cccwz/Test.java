package com.cccwz;

import com.cccwz.service.UserService;
import com.spring.CccwzApplicationContext;

public class Test {
    public static void main(String[] args) {
        CccwzApplicationContext applicationContext = new CccwzApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();

    }
}
