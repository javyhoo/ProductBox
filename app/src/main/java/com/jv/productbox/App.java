package com.jv.productbox;

import android.app.Application;

public class App extends Application {

    public static User user;
    public static Product product;

    @Override
    public void onCreate() {
        super.onCreate();

        user = new User();

        //todo fake data
        product = new Product();
    }

}
