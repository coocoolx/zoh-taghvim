package com.byagowi.persiancalendar;

import android.app.Application;

import com.byagowi.persiancalendar.util.Utils;
import com.pushpole.sdk.PushPole;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.adivery.sdk.Adivery;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.initUtils(getApplicationContext());
    }

}