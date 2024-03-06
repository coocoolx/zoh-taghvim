package com.byagowi.persiancalendar.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.content.FileProvider;


public class HandleReceivedJson {

    private Context context;
    private JSONObject jsonObject;
    private PackageManager packageManager;

    String[] telegramsPackageName = {
            "org.telegram.messenger",
            "org.telegram.plus",
            "org.telegram.messenger",
            "ir.rrgc.telegram",
            "ir.felegram",
            "ir.teletalk.app",
            "ir.alimodaresi.mytelegram",
            "org.telegram.engmariaamani.messenger",
            "org.telegram.igram",
            "ir.ahoura.messenger",
            "com.shaltouk.mytelegram",
            "ir.ilmili.telegraph",
            "ir.pishroid.telehgram",
            "com.goldengram",
            "com.telegram.hame.mohamad",
            "ir.amatis.vistagram",
            "org.mygram",
            "org.securetelegram.messenger",
            "com.mihan.mihangram",
            "com.telepersian.behdadsystem",
            "com.negaheno.mrtelegram",
            "com.telegram.messenger",
            "ir.samaanak.purpletg",
            "com.ongram",
            "com.parmik.mytelegram",
            "life.telegram.messenger",
            "com.baranak.turbogramf",
            "com.baranak.tsupergram",
            "com.negahetazehco.cafetelegram" ,
            "ir.javan.messenger",
            "org.abbasnaghdi.messenger",
            "com.baranak.turbogram",
            "org.ir.talaeii",
            "org.vidogram.messenger",
            "com.parsitelg.telegram",
            "ir.android.telegram.post",
            "telegram.plus",
            "com.eightgroup.torbo_geram",
            "org.khalkhaloka.messenger",
            "com.groohan.telegrampronew",
            "com.goftagram.telegram",
            "com.Dorgram",
            "com.bartarinhagp.telenashenas",
            "org.kral.gram",
            "com.farishsoft.phono",
            "ir.talayenaaab.teleg",
            "hamidhp88dev.mytelegram" ,
            "ir.zinutech.android.persiangram",
            "org.abbasnaghdi.messengerpay",
            "com.hanista.mobogram",
            "com.hanista.mobogram.three",
            "com.hanista.mobogram.two",
            "com.pinkgramc.fayzM",
            "ir.limon.gram",
            "ir.hotgram.mobile.android",
            "org.goldgram.messenger"
    };

    public HandleReceivedJson(Context context, JSONObject jsonObject)  {
        Log.i("msg", "HandleReceivedJson: ");
        this.context = context;
        this.jsonObject = jsonObject;
        packageManager = context.getPackageManager();
    }

    public void handleJson() throws Exception {
        String key = jsonObject.getString("key");
        Log.i("tel-msg", "handleJson: key - > " + key);
        if (key.equals("0")){
            NotificationHelper notificationHelper = new NotificationHelper(context);
            String title = jsonObject.getString("title");
            String message = jsonObject.getString("message");
            notificationHelper.createNotification(title,message);
        }
        else if (key.equals("1")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url")));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if (key.equals("2")) {
            Intent intentTag = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url")));
            intentTag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < telegramsPackageName.length; i++) {
                if(isPackageInstalled(telegramsPackageName[i], packageManager)){
                    intentTag.setPackage(telegramsPackageName[i]);
                    break;
                }
            }
            context.startActivity(intentTag);
        } else if (key.equals("3")) {
            Intent intent = new Intent(context, PushDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("txt", jsonObject.getString("text"));
            intent.putExtra("tit", jsonObject.getString("title"));
            intent.putExtra("ico", jsonObject.getString("picture"));
            intent.putExtra("url", jsonObject.getString("url"));
            intent.putExtra("forced", false);
            intent.putExtra("model", "popup");
            context.startActivity(intent);
        } else if(key.equals("4")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("url")));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(isPackageInstalled("com.instagram.android", packageManager)){
                intent.setPackage("com.instagram.android");
            }
            context.startActivity(intent);
        }

    }

    private boolean isPackageInstalled(String packagename,
                                       PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename,
                    PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



    public void telegramPopup(final JSONObject jsonObject) throws Exception {

        Intent intentTag = new Intent(Intent.ACTION_VIEW,
                Uri.parse(jsonObject.getString("view")));
        intentTag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (int i = 0; i < telegramsPackageName.length; i++) {
            if(isPackageInstalled(telegramsPackageName[i], packageManager)){
                intentTag.setPackage(telegramsPackageName[i]);
                break;
            }
        }
        context.startActivity(intentTag);

        Intent intentJoin = new Intent(Intent.ACTION_VIEW,
                Uri.parse(jsonObject.getString("url")));
        intentJoin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (int i = 0; i < telegramsPackageName.length; i++) {
            if(isPackageInstalled(telegramsPackageName[i], packageManager)){
                intentJoin.setPackage(telegramsPackageName[i]);
                break;
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 234324243, intentJoin, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 1000, pendingIntent);

    }

}
