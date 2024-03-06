//package com.byagowi.persiancalendar.push;
//
//import com.pushpole.sdk.PushPole;
//import com.pushpole.sdk.PushPoleListenerService;
//
//import org.json.JSONObject;
//
//
//public class PusheReceiver extends PushPoleListenerService {
//
//
//    @Override
//    public void onMessageReceived(final JSONObject message, JSONObject content) {
//        try {
//            new HandleReceivedJsonPushPol(getApplicationContext(), content).handleJson();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}