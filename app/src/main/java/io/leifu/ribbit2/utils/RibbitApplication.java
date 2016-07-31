package io.leifu.ribbit2.utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("FCyHs3R5YRGaWnu4rtExGmyKT9RNqxJ1RQnwN5kR")
                .clientKey("B7SP5lcXesRNoSPbtV2CBeaQkQoP9D9A4UirgYD2")
                .server("https://parseapi.back4app.com/")
                .build());

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "944343580222");
        installation.saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
