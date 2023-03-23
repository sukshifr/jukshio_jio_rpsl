package com.jukshio.JioRPSL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {
    ImageView backIv;
    TextView messageTv, nameatbank_tv;
    Button backbtn;
    String message, name;
    boolean issuccess;
    ImageView imageview;
    int doctype;
    LinearLayout namelayout_ll;
    SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        messageTv = findViewById(R.id.message_tv);
        backbtn = findViewById(R.id.back_btn);
        backIv = findViewById(R.id.back_iv);
        imageview = findViewById(R.id.imageview);
        namelayout_ll = findViewById(R.id.namelayout_ll);
        nameatbank_tv = findViewById(R.id.nameatbank_tv);
        prefs = getSharedPreferences("JukshioPrefs", MODE_PRIVATE);
        name = prefs.getString("name_at_bank", "");
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            message = extras.getString("message");
            issuccess = extras.getBoolean("isSuccess");
            doctype = extras.getInt("doc_type");
            messageTv.setText(message);
        }
        if (issuccess) {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.suc_icon));
        } else {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.not_verified_icon));
        }
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(SuccessActivity.this, POCActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                Intent intent2 = new Intent();
                setResult(Activity.RESULT_OK, intent2);
                finish();
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(SuccessActivity.this, POCActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/

                finish();
            }
        });

    }
}