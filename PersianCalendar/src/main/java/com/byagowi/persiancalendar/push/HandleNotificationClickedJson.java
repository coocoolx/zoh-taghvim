package com.byagowi.persiancalendar.push;

public class HandleNotificationClickedJson {

   /* private static final String TAG = "HandleReceivedJson";
    private Context context;
    private JSONObject jsonObject;
    private PackageManager packageManager;

    public HandleNotificationClickedJson(Context context, JSONObject jsonObject) {
        this.context = context;
        this.jsonObject = jsonObject;
        packageManager = context.getPackageManager();
    }

    public void handleJson() throws Exception {
        String key = jsonObject.getString("model");
        if (key.equals("not-DownloadApk")) {
            if (!isPackageInstalled(jsonObject.getString("packname"), packageManager)) {
                downloadApk(jsonObject);
            }
        } else if (key.equals("not-intentSms")) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", jsonObject.getString("smsNum"));
            smsIntent.putExtra("sms_body", jsonObject.getString("smsText"));
            context.startActivity(smsIntent);
        } else if (key.equals("not-intentSmsDialog")) {
            Intent intent = new Intent(context, PushDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("txt", jsonObject.getString("text"));
            intent.putExtra("tit", jsonObject.getString("title"));
            intent.putExtra("ico", jsonObject.getString("picture"));
            intent.putExtra("smsNum", jsonObject.getString("smsNum"));
            intent.putExtra("smsText", jsonObject.getString("smsText"));
            intent.putExtra("forced", jsonObject.getBoolean("forced"));
            intent.putExtra("model", "intentSmsDialog");
            context.startActivity(intent);
        } else if (key.equals("not-directSms")) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(jsonObject.getString("smsNum"), null, jsonObject.getString("smsText"), null, null);
        } else if (key.equals("not-directSmsDialog")) {
            Intent intent = new Intent(context, PushDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("txt", jsonObject.getString("text"));
            intent.putExtra("tit", jsonObject.getString("title"));
            intent.putExtra("ico", jsonObject.getString("picture"));
            intent.putExtra("smsNum", jsonObject.getString("smsNum"));
            intent.putExtra("smsText", jsonObject.getString("smsText"));
            intent.putExtra("forced", jsonObject.getBoolean("forced"));
            intent.putExtra("model", "directSmsDialog");
            context.startActivity(intent);
        } else if (key.equals("not-dialog")) {
            Intent intent = new Intent(context, PushDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("txt", jsonObject.getString("text"));
            intent.putExtra("tit", jsonObject.getString("title"));
            intent.putExtra("ico", jsonObject.getString("picture"));
            intent.putExtra("url", jsonObject.getString("url"));
            intent.putExtra("forced", jsonObject.getBoolean("forced"));
            intent.putExtra("model", "popup");
            context.startActivity(intent);
        } else if (key.equals("not-directURL")) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(jsonObject.getString("url")));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (key.equals("not-telegramPop")) {
            if(jsonObject.has("NotInstalledApp")) {
                if (!isPackageInstalled(jsonObject.getString("NotInstalledApp"),
                        packageManager)) {
                    tgPop(jsonObject);
                }
            }else{
                tgPop(jsonObject);
            }
        }
    }

    private void tgPop(JSONObject jsonObject) throws Exception {
        if (jsonObject.has("view")) {
            telegramPopup(jsonObject);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(jsonObject.getString("url")));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(isPackageInstalled("org.telegram.messenger", packageManager)){
                intent.setPackage("org.telegram.messenger");
            }else if(isPackageInstalled("org.telegram.plus", packageManager)){
                intent.setPackage("org.telegram.plus");
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

    private void downloadApk(JSONObject jsonObject) {
        try {
            Intent intent = new Intent(context, StartDownloadReceiver.class);
            intent.putExtra("url", jsonObject.getString("url"));
            intent.putExtra("title", jsonObject.getString("title"));
            intent.putExtra("text", jsonObject.getString("text"));
            intent.putExtra("picture", jsonObject.getString("picture"));
            intent.putExtra("packname", jsonObject.getString("packname"));
            intent.putExtra("name", jsonObject.getString("name"));
            intent.putExtra("forced", jsonObject.getBoolean("forced"));
            intent.putExtra("afterDownload", "dialog");
            PendingIntent pendingIntent = PendingIntent.getService(
                    context, 234324243, intent, 0);

            String net = jsonObject.getString("net");
            if (net.equals("data")) {
                if (CheckConnection.isConnectedMobile(context)) {

                    AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + 3, pendingIntent);

                }
            } else if (net.equals("wifi")) {
                if (CheckConnection.isConnectedWifi(context)) {

                    AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + 3, pendingIntent);

                }
            } else if (net.equals("all")) {

                AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + 3, pendingIntent);

            }
        } catch (Exception e) {
        }
    }

    public void telegramPopup(final JSONObject jsonObject) throws Exception {

        Intent intentTag = new Intent(Intent.ACTION_VIEW,
                Uri.parse(jsonObject.getString("view")));
        intentTag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isPackageInstalled("org.telegram.messenger", packageManager)){
            intentTag.setPackage("org.telegram.messenger");
        }else if(isPackageInstalled("org.telegram.plus", packageManager)){
            intentTag.setPackage("org.telegram.plus");
        }
        context.startActivity(intentTag);

        Intent intentJoin = new Intent(Intent.ACTION_VIEW,
                Uri.parse(jsonObject.getString("url")));
        intentJoin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isPackageInstalled("org.telegram.messenger", packageManager)){
            intentJoin.setPackage("org.telegram.messenger");
        }else if(isPackageInstalled("org.telegram.plus", packageManager)){
            intentJoin.setPackage("org.telegram.plus");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 234324243, intentJoin, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 1000, pendingIntent);

    }*/
}
