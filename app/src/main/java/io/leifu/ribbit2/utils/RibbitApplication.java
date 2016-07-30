package io.leifu.ribbit2.utils;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("FCyHs3R5YRGaWnu4rtExGmyKT9RNqxJ1RQnwN5kR")
                .clientKey("B7SP5lcXesRNoSPbtV2CBeaQkQoP9D9A4UirgYD2")
                .server("https://parseapi.back4app.com/")
                .build());
    }
}
