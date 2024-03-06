package com.byagowi.persiancalendar.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryListener;
import com.byagowi.persiancalendar.BuildConfig;
import com.byagowi.persiancalendar.Constants;
import com.byagowi.persiancalendar.R;
import com.byagowi.persiancalendar.adapter.DrawerAdapter;
import com.byagowi.persiancalendar.util.TypeFaceUtil;
import com.byagowi.persiancalendar.util.UpdateUtils;
import com.byagowi.persiancalendar.util.Utils;
import com.byagowi.persiancalendar.view.fragment.AboutFragment;
import com.byagowi.persiancalendar.view.fragment.ApplicationPreferenceFragment;
import com.byagowi.persiancalendar.view.fragment.CalendarFragment;
import com.byagowi.persiancalendar.view.fragment.CompassFragment;
import com.byagowi.persiancalendar.view.fragment.ConverterFragment;
import com.example.forceupdate.ForceUpdate;
import com.pushpole.sdk.PushPole;

/*import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;*/

import calendar.CivilDate;


import static com.byagowi.persiancalendar.Constants.DEFAULT_APP_LANGUAGE;
import static com.byagowi.persiancalendar.Constants.LANG_EN_US;
import static com.byagowi.persiancalendar.Constants.LANG_UR;
import static com.byagowi.persiancalendar.Constants.PREF_APP_LANGUAGE;
import static com.byagowi.persiancalendar.Constants.PREF_PERSIAN_DIGITS;
import static com.byagowi.persiancalendar.Constants.PREF_SHOW_DEVICE_CALENDAR_EVENTS;
import static com.byagowi.persiancalendar.Constants.PREF_THEME;

//import co.ronash.pushe.Pushe;

/**
 * Program activity for android
 *
 * @author ebraminio
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int PREFERENCE = 4;
    private static final int CALENDAR = 1;
    private static final int CONVERTER = 2;
    private static final int COMPASS = 3;
    private static final int ABOUT = 5;
    private static final int EXIT = 5;
    // Default selected fragment
    private static final int DEFAULT = CALENDAR;
    private static CivilDate creationDate;
    private final String TAG = MainActivity.class.getName();
   // AdView adView;
   // InterstitialAd mInterstitialAd;
    SharedPreferences pref;
    Handler handler;
    boolean settingHasChanged = false;
    String isCommnetedBefore = "isCommnetedBefore";
    long last_clicked_time = 0;
    long max_dif_millis = 2000;
    private DrawerLayout drawerLayout;
    private DrawerAdapter adapter;
    private Class<?>[] fragments = {
            null,
            CalendarFragment.class,
            ConverterFragment.class,
            CompassFragment.class,
            ApplicationPreferenceFragment.class,
               AboutFragment.class
    };
    private int menuPosition = 0; // it should be zero otherwise #selectItem won't be called

    // https://stackoverflow.com/a/3410200
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void oneTimeClockDisablingForAndroid5LE() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            String key = "oneTimeClockDisablingForAndroid5LE";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!prefs.getBoolean(key, false)) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(Constants.PREF_WIDGET_CLOCK, false);
                edit.putBoolean(key, true);
                edit.apply();
            }
        }
    }

//    private void requestAd() {
//        TapsellPlus.requestInterstitial(
//                MainActivity.this,
//                "5e524e4cbeb0ea0001f219d2",
//                new AdRequestCallback() {
//                    @Override
//                    public void response() {
//                        //ad is ready to show
//                    }
//
//                    @Override
//                    public void error(String message) {
//                    }
//
//                });
//    }

//    private void showAd() {
//        TapsellPlus.showAd(MainActivity.this, "5e524e4cbeb0ea0001f219d2");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setTheme(this);
        Utils.changeAppLanguage(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Adivery.configure(getApplication(), "70eeffdd-8508-49ca-8255-046027896fcd");
        PushPole.initialize(this,true);
        new AppRater(this).show();

        //   MobileAds.initialize(this, "ca-app-pub-6184372097955619~6196275203");

//        TapsellPlus.initialize(this, "ddgjgcpceohiectibahtknjiqjaqhsnojllgpmlmodhtormqaghkrmascmlieebmnclmsq");
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/NotoNaskhArabic-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        // Doesn't matter apparently
        // oneTimeClockDisablingForAndroid5LE();
        UpdateUtils.update(getApplicationContext(), false);
        setContentView(R.layout.activity_main);
        Utils.startEitherServiceOrWorker(this);
        Utils.initUtils(this);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        }

        RecyclerView navigation = findViewById(R.id.navigation_view);
        navigation.setHasFixedSize(true);
        adapter = new DrawerAdapter(this);
        navigation.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        navigation.setLayoutManager(layoutManager);

        drawerLayout = findViewById(R.id.drawer);
        final View appMainView = findViewById(R.id.app_main_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            int slidingDirection = +1;

            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (Utils.isRTL(getApplicationContext()))
                        slidingDirection = -1;
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                slidingAnimation(drawerView, slideOffset);
            }


            private void slidingAnimation(View drawerView, float slideOffset) {
                appMainView.setTranslationX(slideOffset * drawerView.getWidth() * slidingDirection);
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        String action = getIntent() != null ? getIntent().getAction() : null;
        if ("COMPASS_SHORTCUT".equals(action)) {
            selectItem(COMPASS);
        } else if ("PREFERENCE_SHORTCUT".equals(action)) {
            selectItem(PREFERENCE);
        } else if ("CONVERTER_SHORTCUT".equals(action)) {
            selectItem(CONVERTER);
        } else if ("ABOUT_SHORTCUT".equals(action)) {
            selectItem(ABOUT);
        } else {
            selectItem(DEFAULT);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        if (Utils.isShowDeviceCalendarEvents()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                Utils.askForCalendarPermission(this);
            }
        }

        creationDate = Utils.getGregorianToday();
        Utils.changeAppLanguage(this);


        SharedPreferences preferences = getSharedPreferences("Sh", Context.MODE_PRIVATE);
        //ads();
        if (preferences.getBoolean("first", true))
        {
            preferences.edit().putBoolean("first", false).commit();

        } else{
        }
        Adivery.prepareInterstitialAd(MainActivity.this, "d70c9c57-ff7b-419c-b9c6-30e05846531a");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Adivery.showAd("d70c9c57-ff7b-419c-b9c6-30e05846531a");
                            }
        }, 60000);


        Adivery.addGlobalListener(new AdiveryListener() {

            @Override
            public void onAppOpenAdLoaded(String placementId){
                // تبلیغ اجرای اپلیکیشن بارگذاری شده است.
                Log.d("myTag", "اجرای");
//                Adivery.showAd("38b301f2-5e0c-4776-b671-c6b04a612311");
            }

            @Override
            public void onInterstitialAdLoaded(String placementId) {
                // تبلیغ میان‌صفحه‌ای بارگذاری شده
                Log.d("myTag", "میانصفحهای");
            }

            @Override
            public void onRewardedAdLoaded(String placementId) {
                // تبلیغ جایزه‌ای بارگذاری شده
                Log.d("myTag", "جایزهای");
            }

            @Override
            public void onRewardedAdClosed(String placementId, boolean isRewarded) {
                // بررسی کنید که آیا کاربر جایزه دریافت می‌کند یا خیر
                Log.d("myTag", "This is my message");
            }

            @Override
            public void log(String placementId, String log) {
                // پیغام را چاپ کنید
                Log.d("myTag", "This is my message");
            }
        });

        ForceUpdate.get
//                .setUrl("https://applex.ir/updater/taghvimirlexgoog.json",BuildConfig.VERSION_CODE)
                .setUrl("https://applex.ir/updater/taghvimzho.json", 3)
                .syn();

    }

//    private void ads(){
//
//        ViewGroup bannerContainer = findViewById(R.id.standardBanner);
//        TapsellPlus.showBannerAd(
//                MainActivity.this,
//                bannerContainer,
//                "5e516315cb31f00001dde316",
//                TapsellPlusBannerType.BANNER_320x50,
//                new AdRequestCallback() {
//                    @Override
//                    public void response() {
//                    }
//
//                    @Override
//                    public void error(String message) {
//                    }
//                });
//     //   adView = (AdView) findViewById(R.id.adView);
//
//      //  AdRequest adRequest = new AdRequest.Builder().build();
//        //  AdRequest adRequest = new AdRequest.Builder().addTestDevice("91FD617519604C3CFF6F6A1AD3D393B3").build();
//      //  adView.loadAd(adRequest);
//      //  adView.setVisibility(View.VISIBLE);
//        bannerContainer.setVisibility(View.VISIBLE);
//
//
///*
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//
//                bannerContaibbner.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                bannerContainer.setVisibility(View.VISIBLE);
//
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the user is about to return
//                // to the app after tapping on an ad.
//                bannerContainer.setVisibility(View.VISIBLE);
//
//            }
//        });
//*/
//
//
//        /*mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-6184372097955619/5722660159");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/
//        requestAd();
//        ///  mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("91FD617519604C3CFF6F6A1AD3D393B3").build());
//        handler = new Handler();
//
//        final Runnable r = () -> {
//          //  if (mInterstitialAd.isLoaded()) {
//        /*        mInterstitialAd.show();
//                Log.d("Ads", "showAd");
//
//            } else {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                //  mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("91FD617519604C3CFF6F6A1AD3D393B3").build());
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }
//                showAd();
//                //     AdadFullscreenBannerAd.show(MainActivity.this);
//
//                Log.e("TAG", "The interstitial wasn't loaded yet.");
//                //  Toast.makeText(MainActivity.this,"The interstitial wasn't loaded yet.",Toast.LENGTH_LONG).show();
//            }
//            ///     handler.postDelayed(this, 5000);*/
//            showAd();
//        };
//        handler.postDelayed(r, 5000);
//    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        settingHasChanged = true;
        if (key.equals(PREF_APP_LANGUAGE)) {
            boolean persianDigits;
            switch (sharedPreferences.getString(PREF_APP_LANGUAGE, DEFAULT_APP_LANGUAGE)) {
                case LANG_EN_US:
                    persianDigits = false;
                    break;
                case LANG_UR:
                    persianDigits = false;
                    break;
                default:
                    persianDigits = true;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_PERSIAN_DIGITS, persianDigits);
            editor.apply();
        }

        if (key.equals(PREF_SHOW_DEVICE_CALENDAR_EVENTS)) {
            if (sharedPreferences.getBoolean(PREF_SHOW_DEVICE_CALENDAR_EVENTS, true)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Utils.askForCalendarPermission(this);
                }
            }
        }

        if (key.equals(PREF_APP_LANGUAGE) || key.equals(PREF_THEME)) {
            restartActivity(PREFERENCE);
        }

        UpdateUtils.update(getApplicationContext(), true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CALENDAR_READ_PERMISSION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                Utils.initUtils(this);
                if (menuPosition == CALENDAR) {
                    restartActivity(menuPosition);

                }
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(PREF_SHOW_DEVICE_CALENDAR_EVENTS, false);
                edit.apply();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Utils.initUtils(this);
        View v = findViewById(R.id.drawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            v.setLayoutDirection(Utils.isRTL(this) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.changeAppLanguage(this);
        UpdateUtils.update(getApplicationContext(), false);
        if (!creationDate.equals(Utils.getGregorianToday())) {
            restartActivity(menuPosition);
        }
        ForceUpdate.get.setActivity(this);

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else if (menuPosition != DEFAULT) {
            selectItem(DEFAULT);
        } else {
            CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager()
                    .findFragmentByTag(CalendarFragment.class.getName());

            if (calendarFragment != null) {
                if (calendarFragment.closeSearch()) {
                    return;
                }
            }
        }


        if (isDoubleTap()) {
//            if (!getSharedPreferences(isCommnetedBefore,
//                    Context.MODE_PRIVATE).getBoolean(isCommnetedBefore, false)) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                dialog.setMessage("برای حمایت از ما لطفا نظر خود را ثبت کنید \uD83D\uDE0D");
//                dialog.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences sharedPreferences = getSharedPreferences(isCommnetedBefore,
//                                Context.MODE_PRIVATE);
//                        sharedPreferences.edit().putBoolean(isCommnetedBefore, true).apply();
//
////Myket
//                     /*   try {
//                            String url = "myket://comment?id=" + getPackageName();
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse(url));
//                            startActivity(intent);
//                        } catch (Exception e) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
//                                    "https://myket.ir/app/" + getPackageName()));
//                            startActivity(browserIntent);
//                        }*/
/////Bazar
//                  /*      try {
//                            Intent intent = new Intent(Intent.ACTION_EDIT);
//                            intent.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
//                            intent.setPackage("com.farsitel.bazaar");
//                            startActivity(intent);
//
//                        } catch (Exception e) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
//                                    "https://cafebazaar.ir/app/" + getPackageName() + "/?l=fa"));
//                            startActivity(browserIntent);
//                        }*/
////GOOGLE
//                       Uri uri = Uri.parse("market://details?id=" + getPackageName());
//                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                        try {
//                            startActivity(goToMarket);
//                        } catch (ActivityNotFoundException e) {
//                            startActivity(new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                        }
//                    }
//                });
//                dialog.setNegativeButton("بعدا..", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        MainActivity.super.onBackPressed();
//                    }
//                });
//                dialog.show();
//
//            } else {
                super.onBackPressed();
//            }
//
//        } else {
        }
        Toast.makeText(getApplicationContext(), "برای خروج یکبار دیگر کلیک کنید", Toast.LENGTH_SHORT).show();
    }

    private boolean isDoubleTap() {
        long current_time = System.currentTimeMillis();
        long dif = current_time - last_clicked_time;
        last_clicked_time = current_time;
        if (dif < max_dif_millis)
            return true;
        else
            return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Checking for the "menu" key
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void restartActivity(int item) {
        Intent intent = getIntent();
        if (item == CONVERTER)
            intent.setAction("CONVERTER_SHORTCUT");
        else if (item == COMPASS)
            intent.setAction("COMPASS_SHORTCUT");
        else if (item == PREFERENCE)
            intent.setAction("PREFERENCE_SHORTCUT");
        else if (item == ABOUT)
            intent.setAction("ABOUT_SHORTCUT");

        finish();
        startActivity(intent);
    }

    public void selectItem(int item) {
        if (item == EXIT) {
            finish();
            return;
        }

        if (menuPosition != item) {
            if (settingHasChanged && menuPosition == PREFERENCE) { // update on returning from preferences
                Utils.initUtils(this);
                UpdateUtils.update(getApplicationContext(), true);
            }

            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_holder,
                                (Fragment) fragments[item].newInstance(),
                                fragments[item].getName()
                        ).commit();
                menuPosition = item;
            } catch (Exception e) {
                Log.e(TAG, item + " is selected as an index", e);
            }
        }

        adapter.setSelectedItem(menuPosition);

        drawerLayout.closeDrawers();
    }
}
