//package com.byagowi.persiancalendar.push;
//
///**
// */
//
//import android.content.Intent;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import org.json.JSONObject;
//
//import java.util.Map;
//
//import static java.sql.DriverManager.println;
//
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//
//    @Override
//    public void onMessageReceived(final RemoteMessage remoteMessage) {
//        if (PushPole.getFcmHandler(this).onMessageReceived(remoteMessage)) {
//            if (remoteMessage.getData().size() > 0 ){
//                Map<String, String> params = remoteMessage.getData();
//                JSONObject object = new JSONObject(params);
//                try {
//                    new HandleReceivedJsonPushPol(getApplicationContext(),object).handleJson();
//                    Log.i("Message Recivied : ", object.getString("custom_content"));
//                }catch (Exception e){
//                }
//            }else{
//                super.onMessageReceived(remoteMessage);
//            }
//            return;
//        }
//      if (remoteMessage.getData().size() > 0 ){
//          Map<String, String> params = remoteMessage.getData();
//        JSONObject object = new JSONObject(params);
//        try {
//             if (object.getString("key").equals("1")) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("url")));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                 getApplicationContext().startActivity(intent);
//            }
//          new HandleReceivedJson(getApplicationContext(),object).handleJson();
//            Log.e("Message Recivied : ", object.getString("key"));
//        }catch (Exception e){
//        }
//      }else{
//        super.onMessageReceived(remoteMessage);
//      }
//    }
//}