package com.jukshio.JioRPSL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jukshio.JioRPSLlib.APICompletionCallback;
import com.jukshio.JioRPSLlib.Activities.JukshioDocActivity;
import com.jukshio.JioRPSLlib.DocCaptureCompleteHandler;
import com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper;
import com.jukshio.JioRPSLlib.SDKinitializationCallback;
import com.jukshio.JioRPSLlib.JukshioError;

import org.json.JSONException;
import org.json.JSONObject;

public class POCActivity extends AppCompatActivity {

    int REQUEST_CAMERA_PERMISSION = 200;
    Button aadhar_btn, aadharback_btn,aadhar_front_preview_btn, aadhar_back_preview_btn;
    RelativeLayout mem_aadhar_rl,mem_aadharback_rl;
    JukshioNetworkHelper jukshioNetworkHelper;
    public ProgressDialog dialog;
    JSONObject faceJson;
    RadioButton cameraRadioButton;
    RadioGroup radioGroup;
    String cameraMode;
    String memfaceimg = "", nomineefaceimg = "";
    SharedPreferences prefs;
    boolean shouldUseBackCamera, allowData;
    String frontdocimg = "", backdocimg = "", memvoterfrontimg = "", memvoterbackimg = "", nomineevoterfrontimg = "", nomineevoterbackimg = "", fmem1voterfrontimg = "", fmem1voterbackimg = "", fmem2voterfrontimg = "", fmem2voterbackimg = "", app_id, store_id, eaadhaarFullPath = "", eaadhaarBottomPath = "", selectedEACard, circle_id = "", master_id = "", agent_id = "", caf_number = "", gt_rr = "0", passbookdocimg = "";
    int env, docType, voterType;
    String app_type = "", lat = "", lng = "", sub_type = "", username = "";
    String phonemodel = "";
    String domainUrl = "";
    public static String selectedCard = "";
    public SharedPreferences.Editor editor;
    boolean isFrontOrNot = false, isAaadhar = false;
    boolean isMemberFaceClick = false;
    PreviewDialog previewDialog;
    Bitmap maskdoc_front, maskdoc_back;
    String key;

    @SuppressLint({"CommitPrefEdits", "MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocactivity);

        checkPermissions();//Permissions

        String key = "oljsGtPZWqQEjPcVKOrDBqNLLulfPDrhlvRHoNVRHkqkpjFPZWAOxlFugtYAAopO";
        String sslKey = "j8wGqDwODbCMnkhNjrHZNAuYbD5uMDNRkOiyizN9n84=";

        allowData = false;
        env = 0;
        app_id = getApplicationContext().getString(R.string.app_id_config);
//        app_id = "entdkyc";
//        ORN = "CR7777777777";
        store_id = "0703928364";
        app_type = "A";
        domainUrl = "savesolution-stag.Jukshiocorp.com";
        domainUrl = "apis-demo-stag.jukshio.com";
//        https://apis-az-preprod.Jukshiocorp.com/v2/auth_token
//        domainUrl = "turing-az-loadtest.Jukshiocorp.com";
//        https://turing-az-loadtest.Jukshiocorp.com/#/
//        domainUrl = "apis-dev-staging.Jukshiocorp.com";
//        domainUrl= "apis-dkyc-sandbox.jukshio.com";
//        domainUrl="apis-dkyc-sb.presentiva.in";

        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(POCActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        phonemodel = Build.MANUFACTURER + "," + Build.MODEL + "," + Build.VERSION.RELEASE + "," + android_id + "," + Build.ID/*+","+Build.getSerial()*/;

        Log.e("phonemodel", phonemodel);

        dialog = new ProgressDialog(this);
        faceJson = new JSONObject();
        prefs = getSharedPreferences("JukshioPrefs", MODE_PRIVATE);
        editor = prefs.edit();
        username = prefs.getString("Username", "");

        aadhar_btn = findViewById(R.id.aadhar_btn);
        aadharback_btn = findViewById(R.id.aadharback_btn);
//        aadharback_btn = findViewById(R.id.aadharback_btn);



        mem_aadhar_rl = findViewById(R.id.mem_aadhar_rl);
        mem_aadharback_rl = findViewById(R.id.mem_aadharback_rl);


        aadhar_front_preview_btn = findViewById(R.id.aadharfront_preview_btn);
        aadhar_back_preview_btn = findViewById(R.id.aadharback_preview_btn);
//        faceMatchcheck = findViewById(R.id.mainToFaceMatchBtn);

        // Create an ArrayAdapter using the string array and a default spinner layout


//        JukshioNetworkHelper = new JukshioNetworkHelper(POCActivity.this, app_id, env, key/*, domainUrl*/);
        jukshioNetworkHelper = new JukshioNetworkHelper(POCActivity.this, app_id, key, domainUrl);
        jukshioNetworkHelper.initSDK(POCActivity.this, app_id, key, app_type,domainUrl, new SDKinitializationCallback() {
            @Override
            public void OnInitialized(String message, boolean isSuccess) {
                if (!isSuccess) {
                    Toast.makeText(POCActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        JukshioNetworkHelper.makeFilesDelete(new DeletefilesCallback() {
            @Override
            public void OnDelete(String message, boolean isSuccess) {

            }
        });
*/




        aadhar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    selectedCard = "Aadhaar";
                    docType = 1;
                    isFrontOrNot = true;
                    isAaadhar = true;
                    docFrontParams();


            }
        });
        aadharback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docBackParams();
            }
        });


        aadhar_front_preview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewDialog previewDialog = new PreviewDialog(maskdoc_front, POCActivity.this);
            }
        });
        aadhar_back_preview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewDialog previewDialog = new PreviewDialog(maskdoc_back, POCActivity.this);

            }
        });



    }


    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(POCActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(POCActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private void docFrontParams() {
        try {
            JSONObject params = new JSONObject();
            double padding = 0.05;
            boolean shouldSetPadding = true;
            int roll = 10, pitch = 10;
            String docCapturePrompt = "Front Side";
            params.put("domain_url", domainUrl);
            params.put("device_model", phonemodel);
            params.put("front_view", true);
            params.put("shouldShowReviewScreen", true);
            params.put("shouldAllowPhoneTilt", false);
            params.put("allowedTiltRoll", roll);//integer
            params.put("allowedTiltPitch", pitch);//integer
            params.put("shouldSetPadding", shouldSetPadding);//boolean
            params.put("padding", padding);//double
            params.put("allowDataLogging", allowData);
            params.put("key",key);
            params.put("app_id",app_id);
            params.put("referenceId",username);
            params.put("app_type",app_type);
            params.put("docCapturePrompt", docCapturePrompt);//String
            params.put("document", "CARD");
            params.put("doc_type", 1);
            JukshioDocActivity.start(POCActivity.this, docCaptureCompleteHandler, params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    DocCaptureCompleteHandler docCaptureCompleteHandler= new DocCaptureCompleteHandler() {
        @Override
        public void onResult(JukshioError error, JSONObject jsonDocResult, JSONObject header) {
            if (error == null) {
                dialog.dismiss();
                try {
                    aadhar_front_preview_btn.setVisibility(View.VISIBLE);
                    aadhar_btn.setVisibility(View.GONE);
                    mem_aadhar_rl.setBackground(getResources().getDrawable(R.drawable.button_background_green));

                    if (jsonDocResult.has("base64Image")) {
                        String imageString = jsonDocResult.getString("base64Image");
                        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                        maskdoc_front = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        Toast.makeText(POCActivity.this, "Document Captured Successfully", Toast.LENGTH_SHORT).show();
//                        docBackParams();

//                        maskdocParams();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else {
                dialog.dismiss();
                Intent intent = new Intent(POCActivity.this, SuccessActivity.class);
                intent.putExtra("message", error.errorMsg);
                intent.putExtra("isSuccess", false);
                intent.putExtra("doc_type", 0);
                startActivity(intent);
                Log.e("maskrespone", error.errorMsg);
            }
        }
    };
    DocCaptureCompleteHandler docCaptureCompleteHandler1= new DocCaptureCompleteHandler() {
        @Override
        public void onResult(JukshioError error, JSONObject jsonDocResult, JSONObject header) {
            if (error == null) {
                dialog.dismiss();
                try {
                    aadharback_btn.setVisibility(View.GONE);
                    aadhar_back_preview_btn.setVisibility(View.VISIBLE);
                    mem_aadharback_rl.setBackground(getResources().getDrawable(R.drawable.button_background_green));

                    if (jsonDocResult.has("base64Image")) {
                        String imageString = jsonDocResult.getString("base64Image");
                        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                        maskdoc_back = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        Toast.makeText(POCActivity.this, "Document Captured Successfully", Toast.LENGTH_SHORT).show();

//                        maskdocParams();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else {
                dialog.dismiss();
                Intent intent = new Intent(POCActivity.this, SuccessActivity.class);
                intent.putExtra("message", error.errorMsg);
                intent.putExtra("isSuccess", false);
                intent.putExtra("doc_type", 0);
                startActivity(intent);
                Log.e("maskrespone", error.errorMsg);
            }
        }
    };

    JSONObject params = new JSONObject();

    public void backDoc() {

        try {

                    isFrontOrNot = false;
                    params.put("document", "CARD");
                    params.put("doc_type", 1);
                    docBackParams();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void docBackParams() {
        try {
            double padding = 0.05;
            boolean shouldSetPadding = true;
            int roll = 30, pitch = 30;
            String docCapturePrompt = "Back Side";
            params.put("domain_url", domainUrl);
            params.put("front_view", false);
            params.put("shouldShowReviewScreen", true);
            params.put("device_model", phonemodel);
            params.put("shouldAllowPhoneTilt", false);
            params.put("allowedTiltRoll", roll);//integer
            params.put("allowedTiltPitch", pitch);//integer
            params.put("allowDataLogging", allowData);
            params.put("shouldSetPadding", shouldSetPadding);//boolean
            params.put("padding", padding);//double
            params.put("docCapturePrompt", docCapturePrompt);//String
            params.put("sub_type", sub_type);
            params.put("app_id",app_id);
            JukshioDocActivity.start(POCActivity.this, docCaptureCompleteHandler1,params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            switch (resultCode) {
                case RESULT_OK:
                    if (docType == 1) {
                        selectedCard = "Aadhaar";
                        backDoc();
                    }
                    break;
                case RESULT_CANCELED:
                    try {
                        if (docType == 1) {

                            isFrontOrNot = true;
                            docType = 1;
                            selectedCard = "Aadhaar";
                            /*params.put("document", "CARD");
                            params.put("doc_type", 1);*/
                            docFrontParams();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;


            }
        }
        if (requestCode == 206) {
            switch (resultCode) {
                case RESULT_OK:
//                    selectedCard = "VoterID";
                    backDoc();
                    break;
            }
        }


    }

    private void showPreview(String imgpath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgpath);
        PreviewDialog previewDialog = new PreviewDialog(bitmap, POCActivity.this);

    }
}