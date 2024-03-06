package com.byagowi.persiancalendar.push;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.byagowi.persiancalendar.R;


public class PushDialog extends Activity {

    ImageView ok;
    TextView cancel;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.push_dialog);
        Intent intent = getIntent();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int width = displaymetrics.widthPixels;

        getWindow().setLayout((int) (width * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);


        this.setFinishOnTouchOutside(false);


        String text = intent.getStringExtra("txt");
        String title = intent.getStringExtra("tit");
        String ico = intent.getStringExtra("ico");

        cancel = (TextView) findViewById(R.id.popCancelB);
        TextView txtDetail = (TextView) findViewById(R.id.detail);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        TextView txtPageTitle = (TextView) findViewById(R.id.pageTitle);
        final ImageView imgIcon = (ImageView) findViewById(R.id.icon);

        new DisplayImage(imgIcon).execute(ico);

        url = intent.getStringExtra("url");
        cancel.setText("بزن بریم!");

        if (text.length() > 3) {
            txtDetail.setVisibility(View.VISIBLE);
            txtDetail.setText(text);
        }
        if (title.length() > 3) {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(title);
        }



        imgIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                imgIcon.callOnClick();
            }
        });

        ok = (ImageView) findViewById(R.id.popOkB);
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}