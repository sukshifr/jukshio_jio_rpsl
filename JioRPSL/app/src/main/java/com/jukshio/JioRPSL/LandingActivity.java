package com.jukshio.JioRPSL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper;
import com.jukshio.JioRPSLlib.SDKinitializationCallback;


public class LandingActivity extends AppCompatActivity {


    AutoCompleteTextView storeidEdt;
    String store_id = "";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int REQUEST_CAMERA_PERMISSION = 200;
    JukshioNetworkHelper jukshioNetworkHelper;
    int env;
    String app_id = "", domain_Url = "", key="",app_type="A";
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = preferences.edit();
        final EditText userEdtTxt = findViewById(R.id.usernameEditText);
         next = findViewById(R.id.next);
        next.setVisibility(View.GONE);
        storeidEdt = findViewById(R.id.storeIdedt);
       /* ArrayAdapter autoAdpater = new ArrayAdapter(LandingActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.storeArray));
        storeidEdt.setAdapter(autoAdpater);*/
//        editor.putString("store_id", "08052021").apply();
        store_id = "0703928364";
        env = 0;
        app_id = getApplicationContext().getString(R.string.app_id_configurable);
         key = getString(R.string.secure_data);
//        domain_Url = "mops-dkyc-sb-dev.Jukshiocorp.com";
//        domain_Url = "savesolution-stag.Jukshiocorp.com";
        domain_Url = "apis-demo-stag.jukshio.com";
//        https://mops-dkyc-sb-dev.Jukshiocorp.com/
//        JukshioNetworkHelper = new JukshioNetworkHelper(this, app_id, env, key);
        jukshioNetworkHelper = new JukshioNetworkHelper(this, app_id, key, domain_Url);
        checkPermissions();
       /* storeidEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                store_id = storeidEdt.getText().toString().trim();
                editor.putString("store_id", store_id).apply();
            }
        });*/
        storeidEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeidEdt.showDropDown();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredName = userEdtTxt.getText().toString();
                String name = enteredName.replace(" ", "");
                Log.e("sukshiUsername", name);
                long time = System.currentTimeMillis();
                name += String.valueOf(time);
                Log.e("sukshiUsername", name);
//                if (name.length() > 12){
//                    name=name.substring(0,12);
//                    username = names[0];
//                }
//                Log.e("sukshiUsername",name);

                if (!enteredName.equals("") && emptySpacecheck(name)) {

                    SharedPreferences.Editor editor = getSharedPreferences("JukshioPrefs", MODE_PRIVATE).edit();
                    editor.putString("Username", name);
                    editor.apply();

                    Intent i = new Intent(LandingActivity.this, POCActivity.class);
                    startActivity(i);
                   /* if (!storeidEdt.getText().toString().isEmpty()) {
                        SharedPreferences.Editor editor = getSharedPreferences("JukshioPrefs", MODE_PRIVATE).edit();
                        editor.putString("Username", name);
                        editor.apply();

                        Intent i = new Intent(LandingActivity.this, MainActivity.class);
                        startActivity(i);
                    }else {
                        Toast.makeText(LandingActivity.this, "Please enter or select storeId", Toast.LENGTH_SHORT).show();
                    }*/
                } else {

                    Toast.makeText(LandingActivity.this, "Please Enter a username and make sure you don't give any spaces", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean emptySpacecheck(String msg) {

        return msg.matches(".*\\w.*");
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

//            mixpanel.track("User Permissions");

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //    @Override
//    public void onBackPressed() {
//
//        finishAffinity();
//    }
    public void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {
            ActivityCompat.requestPermissions(LandingActivity.this, new String[]{android.Manifest.permission.CAMERA/*, android.Manifest.permission.WRITE_EXTERNAL_STORAGE*/}, REQUEST_CAMERA_PERMISSION);
        } else {
            if (jukshioNetworkHelper ==null) {
                jukshioNetworkHelper = new JukshioNetworkHelper(LandingActivity.this, app_id, key, domain_Url);
//                JukshioNetworkHelper = new JukshioNetworkHelper(LandingActivity.this, app_id, env, key);
            }
            jukshioNetworkHelper.initSDK(LandingActivity.this,app_id,key,app_type,domain_Url, new SDKinitializationCallback() {
                @Override
                public void OnInitialized(String s, boolean b) {
                    if (!b)
                        Toast.makeText(LandingActivity.this, s, Toast.LENGTH_SHORT).show();

                    next.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // close the app
                    Toast.makeText(LandingActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            if (jukshioNetworkHelper ==null) {
                jukshioNetworkHelper = new JukshioNetworkHelper(LandingActivity.this, app_id, key, domain_Url);
//                JukshioNetworkHelper = new JukshioNetworkHelper(LandingActivity.this, app_id, env, key);
            }
            jukshioNetworkHelper.initSDK(LandingActivity.this,app_id,key,app_type,domain_Url, new SDKinitializationCallback() {
                @Override
                public void OnInitialized(String s, boolean b) {
                    if (!b) {
                        Toast.makeText(LandingActivity.this, s, Toast.LENGTH_SHORT).show();
                    }else {

                    }
                    next.setVisibility(View.VISIBLE);
                }
            });
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
