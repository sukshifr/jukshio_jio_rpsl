package com.jukshio.JioRPSLlib.Activities;

/*
 * Jukshio Corp CONFIDENTIAL

 * Jukshio Corp 2018
 * All Rights Reserved.

 * NOTICE:  All information contained herein is, and remains
 * the property of Jukshio Corp. The intellectual and technical concepts contained
 * herein are proprietary to Jukshio Corp
 * and are protected by trade secret or copyright law of U.S.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Jukshio Corp
 */


import static com.jukshio.JioRPSLlib.Camera.CameraSource.needAnalysis;
import static com.jukshio.JioRPSLlib.Camera.CameraSource.takePicture;
import static com.jukshio.JioRPSLlib.ImageAnalysis.AadhaarAnalysis.okForDocAnalysis;
import static com.jukshio.JioRPSLlib.ImageAnalysis.AadhaarAnalysis.readyForDocAnalysis;
import static com.jukshio.JioRPSLlib.LibConstants.IS_DOC_CONTEXT;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.docfaceCoordinates;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.frontJSON;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.isAadhaarJourney;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.isFront;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.isBack;

import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrBackBlock;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrBackPlots;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrFrontPlots;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.path224llll;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.pathHalfDoc;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.rearJSON;
import static com.jukshio.JioRPSLlib.Tracker.FaceDect.previewFaceDetector;
import static com.jukshio.JioRPSLlib.Tracker.FaceGraphic.faceCoordinatesBackupInt;
import static com.jukshio.JioRPSLlib.Tracker.FaceGraphic.mHintOutlinePaint;
import static com.jukshio.JioRPSLlib.Tracker.FaceGraphic.docOrNot;

import static okhttp3.Protocol.HTTP_1_1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.jukshio.JioRPSLlib.CrashProof;
import com.jukshio.JioRPSLlib.Tracker.FaceDect;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.jukshio.JioRPSLlib.Camera.CameraSource;
import com.jukshio.JioRPSLlib.Camera.CameraSourcePreview;
import com.jukshio.JioRPSLlib.DocCaptureCompleteHandler;
import com.jukshio.JioRPSLlib.FrontalAadhar;
import com.jukshio.JioRPSLlib.ImageAnalysis.AadhaarAnalysis;
import com.jukshio.JioRPSLlib.LibUtils;
import com.jukshio.JioRPSLlib.LiveFaceAuthHolder;
import com.jukshio.JioRPSLlib.R;
import com.jukshio.JioRPSLlib.RearAadhar;
import com.jukshio.JioRPSLlib.Tracker.DocRectangleView;
import com.jukshio.JioRPSLlib.Tracker.GraphicOverlay;
import com.jukshio.JioRPSLlib.JukshioError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Here in this activity we render preview on surfaceview capture image onclick process it.
 **/

public class JukshioDocActivity extends AppCompatActivity implements FaceDect.OnMultipleFacesDetectedListener, FaceDect.OnCaptureListener, View.OnClickListener, SensorEventListener {

    private Context context;
    FaceDect faceDect;
    public CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean wasActivityResumed = false;
    ImageView previewImages;
    public ProgressDialog dialog;
    String pathDoc, path224;
    LibUtils libUtils;
    public static Bitmap squareBitmapDoc;
    public boolean previewOrNot, okForCapture = true;
    public JSONObject afterDocCaptured;
    public static boolean frontOrNot;
    private boolean useSSL = false;


    public Button exitBtn, docCaptureBtn;

    FrontalAadhar frontalAadhar;
    private String authTok = "";
    RearAadhar rearAadhar;
    public static String s;
    SensorManager sensorManager;
    Sensor rotationVectorSensor;
    boolean allowTilt = true, shouldSetPadding = false, allowDataLogging;
    static boolean doSuccessLogs;
    int roll = 10, pitch = 10;
    private SharedPreferences prefs;

    double padding = 0.05;
    int currentScreenW, currentScreenH, currentImageWidth, currentImageHeight;
    String docCapturePrompt, selectedDoc, doc_capture;
    Bitmap halfDocBitmap, docBitmap224;
    TextView docInstructionTextview;

    public static AadhaarAnalysis aadhaarAnalysis;
    public int docNum = 0, documentType;
    public boolean eaFlow;
     static String domainUrl;
     String device_model;
     static String app_type;
     static String referenceId;
     String username;
     static String app_id;
     String key;
    private String ssl_Key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_capture);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        doSuccessLogs = false;
        docOrNot = true;
        ocrBackPlots = "";
        ocrFrontPlots = "";
        faceCoordinatesBackupInt = null;

        afterDocCaptured = new JSONObject();
        frontalAadhar = new FrontalAadhar(this);
        rearAadhar = new RearAadhar(this);

        needAnalysis = true;

//        isAadhaarJourney = false;

//        framesAddedCount = 0;

        if (!readyForDocAnalysis) {
            aadhaarAnalysis = new AadhaarAnalysis(getAssets(), JukshioDocActivity.this);
            aadhaarAnalysis.initAadhaarAnalysis();
        }

        aadhaarAnalysis.setupAadhaarAnalysis();

        faceFramedetector = new FaceDetector.Builder(JukshioDocActivity.this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        currentScreenH = metrics.heightPixels;
        currentScreenW = metrics.widthPixels;
        //Log.e("JukshioSukshiSW&H", currentScreenW + "," + currentScreenH);


        Log.e("App_New_Id",app_id+""+app_type+""+referenceId);


        RelativeLayout relativeLayout = findViewById(R.id.myRelativeLayout);
        docCaptureBtn = findViewById(R.id.buttonDoc);
        libUtils = new LibUtils();

        Resources resources = this.getResources();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        context = getApplicationContext();
        dialog = new ProgressDialog(JukshioDocActivity.this);

        exitBtn = findViewById(R.id.exitBtn);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras != null) {
            selectedDoc = extras.getString("document");
            eaFlow = extras.getBoolean("ea_flow");
            previewOrNot = extras.getBoolean("shouldShowReviewScreen");
            frontOrNot = extras.getBoolean("front_view");
            allowTilt = extras.getBoolean("shouldAllowPhoneTilt");
            pitch = extras.getInt("allowedTiltPitch");
            roll = extras.getInt("allowedTiltRoll");
            padding = extras.getDouble("padding");
            shouldSetPadding = extras.getBoolean("shouldSetPadding");
            docCapturePrompt = extras.getString("docCapturePrompt");
            allowDataLogging = extras.getBoolean("allowDataLogging");
            documentType = extras.getInt("doc_type");
            app_id = extras.getString("app_id");
            referenceId = extras.getString("referenceId");
            app_type = extras.getString("app_type");
            domainUrl = extras.getString("domain_url");
//            Log.e("DOCTYPE", String.valueOf(documentType));
            //Log.e("shouldSetPaddingFromExt", String.valueOf(shouldSetPadding));
            //Log.e("paddingFromExtras", String.valueOf(padding));//docCapturePrompt
            if (!shouldSetPadding) {
                padding = 0;
            }

            float docInstruction = 0;
            float docPrompt = 0;

            String docInstructionString = getString(R.string.doc_instruction);


            switch (selectedDoc) {
                case "CARD":
                    docNum = 0;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 2.9f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 5);
                    break;
                case "Passport":
                    docNum = 1;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 2.98f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 5);
                    break;
                case "VoterID":
                    docNum = 2;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 1.42f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);
                    break;
                case "DL":
                    docNum = 3;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 2.9f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 5);
                    //old instructions
                    /*docInstruction= ((currentScreenW * 2 / 3) + (currentScreenW / 1.42f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);*/
                    break;
                case "Other":
                    docNum = 4;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 1.52f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);
//                    docInstructionString ="";
                    break;
                case "eaadhaar":
                    docNum = 5;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 1.52f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);
//                    docInstructionString ="";
                    docCapturePrompt = "";
                    break;
                case "eaadhaar-bottom":
                    docNum = 6;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 1.52f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);
//                    docInstructionString ="";
                    docCapturePrompt = "";
                    break;
                case "Passbook":
                    docNum = 7;
                    docInstruction = ((currentScreenW * 2 / 3) + (currentScreenW / 1.42f));
                    docPrompt = (currentScreenW * 2 / 3 + currentScreenW / 2);
                    break;
            }

            DocRectangleView docRectangleView = new DocRectangleView(JukshioDocActivity.this, padding, docNum);
            relativeLayout.addView(docRectangleView);
            TextView titleView = new TextView(this);
            RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            titleViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            titleViewParams.setMargins(40, 40, 40, 0);
            titleView.setLayoutParams(titleViewParams);
            titleView.setTextColor(getResources().getColor(R.color.colorTitle));
            titleView.setTextSize(18);
            titleView.setText(getResources().getString(R.string.document_capture));
            titleView.setBackgroundColor(getResources().getColor(R.color.white));
            relativeLayout.addView(titleView);

            docInstructionTextview = new TextView(this);
//            if (docNum==4||docNum==5||docNum==6){
//                docInstructionTextview.setVisibility(View.INVISIBLE);
//            }
            RelativeLayout.LayoutParams docInstructionTextviewParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            docInstructionTextviewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            docInstructionTextviewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            docInstructionTextviewParams.setMargins(15, (int) docInstruction, 15, 10);

//            docInstructionTextviewParams.setMargins(40, (int) (currentScreenW * 2 / 3 + currentScreenW / 2.9f), 40, 0);
            docInstructionTextview.setLayoutParams(docInstructionTextviewParams);
            docInstructionTextview.setTextColor(getResources().getColor(R.color.red_color));
            docInstructionTextview.setTextSize(18);
            docInstructionTextview.setGravity(Gravity.CENTER);
            docInstructionTextview.setText(docInstructionString);
            docInstructionTextview.setBackgroundColor(getResources().getColor(R.color.white));
            relativeLayout.addView(docInstructionTextview);

            TextView docPromptTextview = new TextView(this);
            RelativeLayout.LayoutParams ddocPromptTextviewParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ddocPromptTextviewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            ddocPromptTextviewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            ddocPromptTextviewParams.setMargins(40, (int) docPrompt, 40, 0);
//            ddocPromptTextviewParams.setMargins(40, (currentScreenW * 2 / 3 + currentScreenW / 5), 40, 0);
            docPromptTextview.setLayoutParams(ddocPromptTextviewParams);
            docPromptTextview.setTextColor(getResources().getColor(R.color.white));
            docPromptTextview.setTextSize(18);
            docPromptTextview.setGravity(Gravity.CENTER);
            docPromptTextview.setText(docCapturePrompt);
            docPromptTextview.setBackgroundColor(getResources().getColor(R.color.clear));
            relativeLayout.addView(docPromptTextview);

        } else {
            try {
                throw new Exception("please specify preview in Intent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if ((frontOrNot && !eaFlow) || (eaFlow && documentType == 11)) {
            pathHalfDoc = "";
            docfaceCoordinates = "";
            isAadhaarJourney = false;
//            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DKYC_Libr");
           /* File folder = new File(context.getFilesDir(), "DKYC_Libr");

            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
                folder.delete();
            }*/
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        previewImages = findViewById(R.id.previewDocImage1);
        previewImages.setVisibility(View.GONE);
        mPreview = findViewById(R.id.previewDoc);
        mGraphicOverlay = findViewById(R.id.faceOverlayDoc);
        createCameraSourceFront();
        startCameraSource();

        mHintOutlinePaint = new Paint();
        mHintOutlinePaint.setColor(resources.getColor(R.color.green_color));
        mHintOutlinePaint.setStyle(Paint.Style.STROKE);
        mHintOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.hintStroke));

        docCaptureBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.USER_EXIT, "User has pressed cancel button"), null,null);

            }
        });
        initAnalytics();


    }

    public void initAnalytics() {
        //   AppCenter.start(getApplication(), "edae2aab-a04a-49cd-8c64-ae5872a61a44", Analytics.class, Crashes.class);
        // AppCenter.start(getApplication(), "2215a2b1-5bba-4845-8e03-3af284cce984", Analytics.class, Crashes.class);
        AppCenter.start(getApplication(), "cd537e46-cd2f-434b-815d-630aeaadc4da", Analytics.class, Crashes.class);
        Analytics.setEnabled(allowDataLogging);
    }

    /**
     * Static method to start self activity passing parameters like DocCaptureCompleteListener.
     **/
    public static void start(Context context,JSONObject docParams, DocCaptureCompleteHandler onDocCaptured) {
        logDocCaptureStartEvent(frontOrNot);
        Intent intent = new Intent(context, JukshioDocActivity.class);
        try {

            intent.putExtra("front_view", docParams.getBoolean("front_view"));

            if (docParams.has("ea_flow")) {
                intent.putExtra("ea_flow", docParams.getBoolean("ea_flow"));
            } else {
                intent.putExtra("ea_flow", false);
            }

            if (docParams.has("doc_type")) {
                intent.putExtra("doc_type", docParams.getInt("doc_type"));
            } else {
                intent.putExtra("doc_type", 1);
            }

            if (docParams.has("document")) {
                intent.putExtra("document", docParams.getString("document"));
            } else {
                intent.putExtra("document", "CARD");
            }

            if (docParams.has("shouldShowReviewScreen")) {
                intent.putExtra("shouldShowReviewScreen", docParams.getBoolean("shouldShowReviewScreen"));
            } else {
                intent.putExtra("shouldShowReviewScreen", true);
            }

            if (docParams.has("allowDataLogging")) {
                intent.putExtra("allowDataLogging", docParams.getBoolean("allowDataLogging"));
            } else {
                intent.putExtra("allowDataLogging", false);
            }

            if (docParams.has("shouldAllowPhoneTilt")) {
                intent.putExtra("shouldAllowPhoneTilt", docParams.getBoolean("shouldAllowPhoneTilt"));
            } else {
                intent.putExtra("shouldAllowPhoneTilt", true);
            }

            if (docParams.has("allowedTiltRoll")) {
                intent.putExtra("allowedTiltRoll", docParams.getInt("allowedTiltRoll"));
            } else {
                intent.putExtra("allowedTiltRoll", 10);
            }

            if (docParams.has("allowedTiltPitch")) {
                intent.putExtra("allowedTiltPitch", docParams.getInt("allowedTiltPitch"));
            } else {
                intent.putExtra("allowedTiltPitch", 10);
            }

            if (docParams.has("padding")) {
                intent.putExtra("padding", docParams.getDouble("padding"));
            } else {
                intent.putExtra("padding", 0.05);
            }


            if (docParams.has("shouldSetPadding")) {
                intent.putExtra("shouldSetPadding", docParams.getBoolean("shouldSetPadding"));
            } else {
                intent.putExtra("shouldSetPadding", false);
            }

            if (docParams.has("docCapturePrompt")) {
                intent.putExtra("docCapturePrompt", docParams.getString("docCapturePrompt"));
            } else {
                intent.putExtra("docCapturePrompt", "Document");
            }
            if (docParams.has("docCapturePrompt")) {
                intent.putExtra("docCapturePrompt", docParams.getString("docCapturePrompt"));
            } else {
                intent.putExtra("docCapturePrompt", "Document");
            }
            if (docParams.has("domain_url")) {
                intent.putExtra("domain_url", docParams.getString("domain_url"));

            } else {
                intent.putExtra("domain_url", "domain_url");
            }
            if (docParams.has("app_id")) {
                intent.putExtra("app_id", docParams.getString("app_id"));

            } else {
                intent.putExtra("app_id", app_id);
            }
            if (docParams.has("app_type")) {
                intent.putExtra("app_type", docParams.getString("app_type"));

            } else {
                intent.putExtra("app_type", app_type);

            }
            if (docParams.has("referenceId")) {
                intent.putExtra("referenceId", docParams.getString("referenceId"));

            } else {
                intent.putExtra("referenceId", referenceId);
            }
if (docParams.has("domain_url")) {
                intent.putExtra("domain_url", docParams.getString("domain_url"));

            } else {
                intent.putExtra("domain_url", domainUrl);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        LiveFaceAuthHolder.getInstance().setDocCaptureResultListener(onDocCaptured);
    }

    /**
     * Method for logging aws events on start of doc capture intent.
     **/
    public static void logDocCaptureStartEvent(Boolean isFront) {
        if (doSuccessLogs) {
            Map<String, String> properties = new HashMap<>();
            if (isFront) {
                properties.put("DocType", "Front");
            } else {
                properties.put("DocType", "Back");
            }
            Analytics.trackEvent("OnStartDocCapture", properties);
        }


    }

    ///////////////////////////////////////////////////// Gyroscope start ///////////////////////////////////////////////////////////

    /**
     * This method is invoked on gyroscope sensor change event thus by handling horizontal position of mobile.
     **/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (allowTilt) {
            docCaptureBtn.setOnClickListener(this);
            docCaptureBtn.setBackground(getResources().getDrawable(R.drawable.enable_camera));
        } else {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
            float[] orientations = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientations);
            for (int i = 0; i < 3; i++) {
                orientations[i] = (float) (Math.toDegrees(orientations[i]));
            }
            if (orientations[1] > 90 - pitch && orientations[1] < 90 + pitch) {
                docCaptureBtn.setOnClickListener(this);
                docCaptureBtn.setBackground(getResources().getDrawable(R.drawable.enable_camera));
                docInstructionTextview.setTextColor(getResources().getColor(R.color.colorTitle));
                docInstructionTextview.setText(getString(R.string.doc_instruction));
            } else {
                docCaptureBtn.setOnClickListener(null);
                docCaptureBtn.setBackground(getResources().getDrawable(R.drawable.disable_camera));
                docInstructionTextview.setTextColor(getResources().getColor(R.color.red_color));
                docInstructionTextview.setText(getString(R.string.doc_instruction_red));
            }
        }
    }

    /**
     * Method to get availability of gyroscope on phone.
     **/
    public boolean isGyroscopeAvailable() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    /***
     * This is method is invoked on gyrocope accuracy change.
     * **/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * This method is invoked by system on start of activity and here we are registering the gyroscope.
     **/
    @Override
    protected void onStart() {
        super.onStart();
        if (isGyroscopeAvailable()) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * This method is invoked by system on activity close and here we ae de-registering the gyroscope.
     **/
    @Override
    protected void onStop() {
        super.onStop();
        if (isGyroscopeAvailable()) {
            sensorManager.unregisterListener(this);
        }
    }

    ///////////////////////////////////////////////////// Gyroscope end ///////////////////////////////////////////////////////////

    @Override
    public void onMultipleFacesDetected(int n) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonDoc) {

            okForDocAnalysis = false;
            if (okForCapture) {
                okForCapture = false;
                takePicture = true;
                //takePicture();
            }
        }
    }

    /**
     * This method invoked by camerasource class onfirst availability of frame and onclick of button.
     **/

    @Override
    public void onCapture(byte[] data, int angle) {

        dialog.setMessage("Receiving Image...");
        dialog.setCancelable(false);
        dialog.show();

        stopCameraSource();

       /* if (frontOrNot){
            String obdAadhaar= aadhaarAnalysis.getAadhaarIndex();
            Log.e("JukshioOBDFront1", obdAadhaar);
        }else {
            String obdAadhaar= aadhaarAnalysis.getAadhaarIndex();
            Log.e("JukshioOBDBack1", obdAadhaar);
        }*/


        Bitmap OriginalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedbitmap = Bitmap.createBitmap(OriginalBitmap, 0, 0, OriginalBitmap.getWidth(), OriginalBitmap.getHeight(), matrix, true);
        currentImageWidth = rotatedbitmap.getWidth();
        currentImageHeight = rotatedbitmap.getHeight();
        //Log.e("JukshioSukshiIW&H", rotatedbitmap.getWidth() + ", " + rotatedbitmap.getHeight());
        //Log.e("JukshioSukshiSW&H", currentScreenW + "," + currentScreenH);
        if (docNum == 0 || docNum == 3) {
            squareBitmapDoc = libUtils.getDocBitmap(rotatedbitmap, padding);
        } else if (docNum == 1) {
            squareBitmapDoc = libUtils.getPassportDocBitmap(rotatedbitmap, padding);
        } else if (docNum == 6) {
            squareBitmapDoc = libUtils.getEAadhaarBotomBitmap(rotatedbitmap, padding);
            matrix.postRotate(180);
            squareBitmapDoc = Bitmap.createBitmap(squareBitmapDoc, 0, 0, squareBitmapDoc.getWidth(), squareBitmapDoc.getHeight(), matrix, true);

        } else {
//            Log.e("JukshioHeight", String.valueOf(rotatedbitmap.getHeight()));
//            Log.e("JukshioWeight", String.valueOf(rotatedbitmap.getWidth()));

            /*if ((rotatedbitmap.getHeight() / rotatedbitmap.getWidth()) > 1.4f){
                squareBitmapDoc = libUtils.getVoterDocBitmap(rotatedbitmap, padding);
            }else {
                squareBitmapDoc= rotatedbitmap;
            }*/
            squareBitmapDoc = libUtils.getVoterDocBitmap(rotatedbitmap, padding);
        }
//        squareBitmapDoc = libUtils.getDocBitmap(rotatedbitmap, padding);
        docBitmap224 = libUtils.get224Bitmap(squareBitmapDoc);
        //squareBitmapDocCompressed = libUtils.getDocCompressedBitmap(squareBitmapDoc);
        if (frontOrNot) {
            if (selectedDoc.equals("CARD") && documentType == 1) {
                isAadhaarJourney = true;
                halfDocBitmap = libUtils.getHalfBitmap(squareBitmapDoc);
            }
        }
        /*squareBitmapDoc = libUtils.getDocBitmap(rotatedbitmap, padding);
        docBitmap224 = libUtils.get224Bitmap(squareBitmapDoc);
        //squareBitmapDocCompressed = libUtils.getDocCompressedBitmap(squareBitmapDoc);
        if (frontOrNot){
            halfDocBitmap = libUtils.getHalfBitmap(squareBitmapDoc);
        }*/

        isFront = false;
        isBack = false;
        ocrBackBlock = "";
        logDocPictureTakenEvent(frontOrNot);

        if (frontOrNot) {

            if (selectedDoc.equals("CARD") && documentType == 1) {

                frontJSON = frontalAadhar.inspectFromBitmap(squareBitmapDoc);
                if (frontJSON != null) {
                    s = String.valueOf(frontJSON);
                    //Log.e("frontJson", frontJSON.toString());
                } else s = "";

                if (faceFramedetector.isOperational()) {
                    new getFaceCs().execute();
                }
            }

            if (previewOrNot) {
                moveToPreview();
            } else {
//                saveHalfDoc(halfDocBitmap);
                saveFile(squareBitmapDoc);
            }

        } else {

            if (selectedDoc.equals("CARD") && documentType == 1) {

                rearJSON = rearAadhar.inspectFromBitmap(squareBitmapDoc);
                if (rearJSON != null) {
                    s = String.valueOf(rearJSON);
                    //Log.e("rearJson", rearJSON.toString());
                } else {
                    s = "";
                }
            }

            if (previewOrNot) {
                moveToPreview();
            } else {
//                saveHalfDoc(halfDocBitmap);
                saveFile(squareBitmapDoc);
            }
        }
    }

    /**
     * method to log event onPictureTaken.
     **/

    public void logDocPictureTakenEvent(Boolean isFront) {
        if (doSuccessLogs) {
            Map<String, String> properties = new HashMap<>();
            if (isFront) {
                properties.put("DocType", "Front");
            } else {
                properties.put("DocType", "Back");
            }

            Analytics.trackEvent("OnDocPictureTaken", properties);
        }
    }


    /**
     * Method to move to preview activity just to confirm image from users.
     **/

    public void moveToPreview() {
        logOnOpenPreview(frontOrNot);
        Intent intent = new Intent(JukshioDocActivity.this, PreviewActivity.class);
        intent.putExtra(IS_DOC_CONTEXT, 0);
        startActivityForResult(intent, 200);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    /**
     * Mehtod to log onOpenPreview.
     **/
    public void logOnOpenPreview(boolean isFront) {
        if (doSuccessLogs) {
            Map<String, String> properties = new HashMap<>();
            if (isFront) {
                properties.put("DocType", "Front");
            } else {
                properties.put("DocType", "Back");
            }

            Analytics.trackEvent("OnPreviewDoc", properties);
        }
    }

    /**
     * This method is invoked by the system when acitivy passes on result to this activity.
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            switch (resultCode) {
                case RESULT_OK:
                    okForStartCam = false;
//                    dialog.show();
//                    saveHalfDoc(halfDocBitmap);
                    saveFile(squareBitmapDoc);

                    break;

                case RESULT_CANCELED:
                    aadhaarAnalysis.setupAadhaarAnalysis();
                    okForStartCam = true;
                    okForCapture = true;
                    if (frontOrNot) {
                        docfaceCoordinates = "";
                        faceCoordinatesBackupInt = null;
                    }
                    dialog.dismiss();
                    Toast.makeText(this, "Photo rejected. Please retake picture", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * Mehtod to save half doc for better image recognition for models and save it to local internal storage.
     **/
    public void saveHalfDoc(Bitmap bitmap) {

        File file = libUtils.getOutputMediaFile(JukshioDocActivity.this, "DocHalf");
        if (frontOrNot) {
            pathHalfDoc = file.getPath();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();

                    save224(docBitmap224);
//                    saveFile(squareBitmapDoc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to save file to local internal storage.
     **/
    public void saveFile(Bitmap bitmap) {

        File file = libUtils.getOutputMediaFile(JukshioDocActivity.this, selectedDoc + "_full");
        pathDoc = file.getPath();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();

                    if (frontOrNot) {
                        if (selectedDoc.equals("CARD") && documentType == 1) {
                            saveHalfDoc(halfDocBitmap);
                        } else {
                            save224(docBitmap224);
                        }

                    } else {
//                        if (documentType==11 || documentType==12){
//                            afterSaving();
//                        }else {
                        save224(docBitmap224);
//                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to save file with 224 size of image analysis.
     **/
    public void save224(Bitmap bitmap) {

        File file = libUtils.getOutputMediaFile(JukshioDocActivity.this, selectedDoc + "_224");
        path224 = file.getPath();
        path224llll = path224;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();

                    afterSaving();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to hold instance of docCaptureHandler on completion of activity.
     **/
    public void afterSaving() {

        try {
            if (frontOrNot) {
                afterDocCaptured.put("imageUri", pathDoc);
            } else afterDocCaptured.put("imageUri", pathDoc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        dialog.dismiss();
        logDocCaptureComplete(frontOrNot);
        if (checkInternetConnection()) {
            new ApiCallForAadhaarMasking().execute();
        } else {
            LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.NO_INTERNET, "No Active Internet Connection found"), null, null);
            logNoInternetEvent("OnOcrCall", referenceId);

        }
      ;


//        LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(null, afterDocCaptured);
//        finish();
    }

    /**
     * Method to log CaptureComplete.
     **/
    public void logDocCaptureComplete(Boolean isFront) {
        if (doSuccessLogs) {
            Map<String, String> properties = new HashMap<>();
            if (isFront) {
                properties.put("DocType", "Front");
            } else {
                properties.put("DocType", "Back");
            }
            Analytics.trackEvent("OnCompleteDocCapture", properties);
        }


    }

    /**
     * Class to create thread to get face coordinates.
     **/
    @SuppressLint("StaticFieldLeak")
    public class getFaceCs extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            docfaceCoordinates = getFaceCoordinates(halfDocBitmap);
//            Log.e("JukshioSukshiCoordsDoc", docfaceCoordinates);
            return null;
        }
    }

    FaceDetector faceFramedetector;

    public String getFaceCoordinates(Bitmap bitmap) {

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceFramedetector.detect(frame);

        if (faces.size() == 1) {
            Face face = faces.valueAt(0);

            float faceX = face.getPosition().x;
            float faceY = face.getPosition().y;
            float faceWidth = face.getWidth();
            float faceHeight = face.getHeight();
//            Log.e("JukshioSukshiXYWH", faceX + ", " + faceY + ", " + faceWidth + ", " +faceHeight);

            JSONObject faceCoordinates = new JSONObject();
            try {
                faceCoordinates.put("left", faceX);
                faceCoordinates.put("right", faceX + faceWidth);
                faceCoordinates.put("top", faceY);
                faceCoordinates.put("bottom", faceY + faceHeight);
                faceCoordinates.put("last", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.e("coordinatesImage", faceCoordinates.toString());
            return faceCoordinates.toString();
        } else if (faces.size() == 0) {
            JSONObject faceCoordinatesBackup = new JSONObject();

            if (faceCoordinatesBackupInt != null) {
                try {

                    float ratio1 = (float) currentImageHeight / currentImageWidth;
//                    Log.e("JukshioSukshiRatio1", String.valueOf(ratio1));
                    //float ratio2 = (float)

                    float cardW = (float) currentImageWidth * 9 / 10;
                    float cardH = cardW / 1.45f;

                    float topFG = (faceCoordinatesBackupInt.getInt("top"));
                    float bottomFG = (faceCoordinatesBackupInt.getInt("bottom"));
                    float leftFG = (faceCoordinatesBackupInt.getInt("left"));
                    float rightFG = (faceCoordinatesBackupInt.getInt("right"));

                    float topI = (topFG * currentImageWidth) / currentScreenW;
                    float bottomI = (bottomFG * currentImageWidth) / currentScreenW;
                    float leftI = (leftFG * currentImageWidth) / currentScreenW;
                    float rightI = (rightFG * currentImageWidth) / currentScreenW;

                    float topC = topI - ((currentImageHeight - cardH) / 2) - (cardH / 10);
                    float bottomC = bottomI - ((currentImageHeight - cardH) / 2) - (cardH / 10);
                    float leftC = leftI - ((currentImageWidth - cardW) / 2);
                    float rightC = rightI - ((currentImageWidth - cardW) / 2);

                    faceCoordinatesBackup.put("left", leftC);
                    faceCoordinatesBackup.put("right", rightC);
                    faceCoordinatesBackup.put("top", topC);
                    faceCoordinatesBackup.put("bottom", bottomC);
                    faceCoordinatesBackup.put("last", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.e("coordinatesFrame", faceCoordinatesBackup.toString());
                return faceCoordinatesBackup.toString();
            } else {
                return "";
            }

        } else {
            return "";
        }
    }

    /*public String getFaceCoordinatesOnlyBackup(Bitmap bitmap) {
        JSONObject faceCoordinatesBackup = new JSONObject();
        if (faceCoordinatesBackupInt != null) {
            try {

                float ratio1 = (float) currentImageHeight/currentImageWidth;
                Log.e("JukshioSukshiRatio1", String.valueOf(ratio1));
                //float ratio2 = (float)

                float cardW = (float) currentImageWidth * 9 / 10;
                float cardH = cardW / 1.45f;

                float topFG = (faceCoordinatesBackupInt.getInt("top"));
                float bottomFG = (faceCoordinatesBackupInt.getInt("bottom"));
                float leftFG = (faceCoordinatesBackupInt.getInt("left"));
                float rightFG = (faceCoordinatesBackupInt.getInt("right"));

                float topI = (topFG * currentImageWidth) / currentScreenW;
                float bottomI = (bottomFG * currentImageWidth) / currentScreenW;
                float leftI = (leftFG * currentImageWidth) / currentScreenW;
                float rightI = (rightFG * currentImageWidth) / currentScreenW;

                float topC = topI - ((currentImageHeight - cardH)/2) - (cardH/10);
                float bottomC = bottomI - ((currentImageHeight - cardH)/2) - (cardH/10);
                float leftC = leftI - ((currentImageWidth - cardW)/2);
                float rightC = rightI - ((currentImageWidth - cardW)/2);

                faceCoordinatesBackup.put("left", leftC);
                faceCoordinatesBackup.put("right", rightC);
                faceCoordinatesBackup.put("top", topC);
                faceCoordinatesBackup.put("bottom", bottomC);
                faceCoordinatesBackup.put("last", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.e("coordinatesFrame", faceCoordinatesBackup.toString());
            return faceCoordinatesBackup.toString();
        } else {
            return "";
        }
    }*/

    /**
     * Method to instanstiate CamSourceClass.
     **/
    private void createCameraSourceFront() {
        faceDect = new FaceDect(this, mGraphicOverlay);
        mCameraSource = new CameraSource.Builder(context, previewFaceDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                .setRequestedFps(30.0f)
                .build();
        startCameraSource();
    }

    /**
     * Method to start camersource class.
     **/
    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Method to stop camSource class.
     **/
    private void stopCameraSource() {
        mPreview.stop();
    }

    boolean okForStartCam = true;

    /**
     * Lifecycle method of activity invoked on activity goes into background to back online.
     **/
    @Override
    protected void onResume() {
        super.onResume();

        if (okForStartCam) {

            if (wasActivityResumed) {
                createCameraSourceFront();
            }
            startCameraSource();
        }
    }

    /**
     * Interface Callback on shutter of camera invoked by cameraSource.
     **/

    final CameraSource.ShutterCallback cameraSourceShutterCallback = new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {

            dialog.setMessage("Receiving Image...");
            dialog.setCancelable(false);
            dialog.show();
        }
    };
    /**
     * Interface callback on camerasocurcepicture taken.
     ***/
    final CameraSource.PictureCallback cameraSourcePictureCallback = new CameraSource.PictureCallback() {
        @SuppressLint("NewApi")
        @Override
        public void onPictureTaken(byte[] data) {
            stopCameraSource();
        }
    };

    /**
     * Method to invoked camersource take picture method of camera1.
     **/
    public void takePicture() {
        if (mCameraSource != null)
            mCameraSource.takePicture(cameraSourceShutterCallback, cameraSourcePictureCallback);
    }

    /**
     * Lifecycle method invoked by the system onclose of activity.
     **/
    @Override
    protected void onPause() {
        super.onPause();
        wasActivityResumed = true;
        stopCameraSource();
    }

    /**
     * Lifecycle method invoked by the system on Close of activiy just after on pause.
     **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCameraSource();
        if (previewFaceDetector != null) {
            previewFaceDetector.release();
        }
    }

    /**
     * Lifecycle method invoked by the system on backbutton pressed.
     **/
    @Override
    public void onBackPressed() {
    }
    private boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected();
    }
    public void logNoInternetEvent(String ActivityName, String referenceId) {
        if (doSuccessLogs) {
            Map<String, String> properties = new HashMap<>();
            properties.put("Activity", ActivityName);
            properties.put("ReferenceId", referenceId);
            Analytics.trackEvent("No Internet", properties);
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class ApiCallForAadhaarMasking extends AsyncTask<String, String, CrashProof> {

        @Override
        protected CrashProof doInBackground(String... strings) {

            String url = null, server = null;
            server = domainUrl;
            url = "https://" + server + "/v1/aadhar_masking";
            String useSSLStr = "";
            try {
                useSSLStr = strings[1];
            } catch (Exception e) {
                useSSLStr = "NO";
            }
/*
            switch (env) {
                case 0:
                    server = context.getString(R.string.staging_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;
                case 1:
                    server = context.getString(R.string.prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 2:
                    server = context.getString(R.string.replica_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 3:
                    server = context.getString(R.string.ent_prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 4:
                    server = context.getString(R.string.az_entdkyc_repl_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;
//                case 5:
//                    server = context.getString(R.string.az_preprod_server);
//                    url = "https://" + server + "/v1/aadhar_masking";
//                    break;
              */
/*  case 5:
                    server = context.getString(R.string.az_dev_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 6:
                    server = context.getString(R.string.az_preprod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;
                case 7:
                    server = context.getString(R.string.az_prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 8:
                    server = context.getString(R.string.az_sandbox_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;

                case 9:
                    server = context.getString(R.string.new_prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;
                case 10:
                    server = context.getString(R.string.new_ent_prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;
                case 11:
                    server = context.getString(R.string.new_az_prod_server);
                    url = "https://" + server + "/v1/aadhar_masking";
                    break;*//*

            }
*/
            //Log.e("JukshioSukshiUrl", url);

            //checking trail count

            OkHttpClient client;
            useSSL = prefs.getBoolean("useSSL", false);
            authTok = prefs.getString("auth_token", "");
            if (useSSL && useSSLStr.equalsIgnoreCase("YES")) {
                String stringSSLData = context.getResources().getString(R.string.certificate_data);
                try {
                    byte[] data = Base64.decode(stringSSLData, Base64.DEFAULT);
                    ssl_Key = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {

                    CertificatePinner certificatePinner = new CertificatePinner.Builder()
                            .add(server, "sha256/" + ssl_Key)
                            .build();

                    client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .protocols(Collections.singletonList(HTTP_1_1))
                            .certificatePinner(certificatePinner)
                            .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                            .build();
                } catch (Exception e) {
                    client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .protocols(Collections.singletonList(HTTP_1_1))
                            .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                            .build();
                }
            } else {
                client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .protocols(Collections.singletonList(HTTP_1_1))
                        .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                        .build();
            }


            File imageFile = null, imageFile224;
            String delay = "";
            String decision = "";
//            docPath ="";
            if (!pathDoc.equals("")) {
                imageFile = new File(pathDoc);

//                Log.e("AadharImageIndex", delay);
                //Log.e("JukshioSukshiORN",referenceId);


                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                multipartBodyBuilder.setType(MultipartBody.FORM); //this may not be needed

                multipartBodyBuilder.addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/jpg"), imageFile));
                multipartBodyBuilder.addFormDataPart("app_id", app_id);
                multipartBodyBuilder.addFormDataPart("referenceId", referenceId);
                multipartBodyBuilder.addFormDataPart("doc_type", String.valueOf(documentType));
                multipartBodyBuilder.addFormDataPart("device_model", "deviceModel");
                multipartBodyBuilder.addFormDataPart("app_type", app_type);
                if (frontOrNot) {
                    multipartBodyBuilder.addFormDataPart("is_back", "0");
                } else {
                    multipartBodyBuilder.addFormDataPart("is_back", "1");
                }

                authTok = prefs.getString("auth_token", "");
                //            Log.e("prefesAuthTOken1", authTok);

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + authTok)
                        .url(url)
                        .post(multipartBodyBuilder.build())
                        .build();

                Response docResponse;
                CrashProof crashProof;

                try {
                    docResponse = client.newCall(request).execute();
//                    Log.e("JukshioSukshiDocResp", String.valueOf(docResponse));

                    if (docResponse.code() == 400) {
                        return new CrashProof("Bad Request", null, 0);
                    } else if (docResponse.code() == 401) {
                        return new CrashProof("Unathorised request", null, 0);
                    } else if (docResponse.code() == 500 || docResponse.code() == 502) {
                        return new CrashProof("Internal Server Error", null, 0);
                    } else {
                        Headers headers = docResponse.headers();
                        JSONObject headerObject = getHeadersData(headers);
                        String refID = "", reqId = "", info = "";
                        try {
                            if (headerObject.has("Referenceid")) {
                                refID = headerObject.getString("Referenceid");
                            } else {
                                refID = "";
                            }
                            if (headerObject.has("Requestid")) {
                                reqId = headerObject.getString("Requestid");
                            } else {
                                reqId = "";
                            }
                            if (headerObject.has("Info")) {
                                info = headerObject.getString("Info");
                            } else {
                                info = "";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.e("xcall", reqId);

                        ResponseBody responseBody = docResponse.body();
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.e("JukshioSukshiDocRespB", jsonObject.toString());
//                        int respCode = jsonObject.getInt("statusCode");
//                        Log.e("JukshioSukshiDocRespB", String.valueOf(respCode));
                        String statusCode = jsonObject.getString("statusCode");
//                        Log.e("JukshioSukshiDocRespB", statusCode);

                        crashProof = new CrashProof(null, docResponse, 0);
                        crashProof.setReferenceid(refID);
                        crashProof.setRequestid(reqId);
                        crashProof.setHeaderStatusCode(docResponse.code());
                        crashProof.setBodyStatusCode(statusCode);
                        crashProof.setResponseBodyJson(jsonObject);
                        return crashProof;
                    }
                } catch (SSLException sslE) {
                    sslE.printStackTrace();
                    return new CrashProof("SSL Error:" + sslE.getMessage(), null, 3);
                } catch (IOException e) {
//                    Log.e("JukshioSukshiIO", e.getMessage());
                    return new CrashProof("RequestTimedout:" + e.getMessage(), null, 1);
                } catch (JSONException e) {
//                    Log.e("JukshioSukshiJ", e.toString());
                    return new CrashProof("JsonException:" + e.getMessage(), null, 2);
                }
            } else {

                return new CrashProof("Please retake the picture", null, 0);
            }
        }

        @Override
        protected void onPostExecute(CrashProof crashProof) {
            super.onPostExecute(crashProof);
            if (crashProof.getErrorType() != null) {
                if (crashProof.getExceptionCode() == 1) {
                    dialog.dismiss();

                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.REQUEST_TIME_OUT, "Request timed out"), null, null);
                    finish();
                } else if (crashProof.getExceptionCode() == 2) {
                    dialog.dismiss();
                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.RESPONSE_JSON_E, "Json Exception"), null, null);
                    finish();
                } else if (crashProof.getExceptionCode() == 3) {
                    new ApiCallForAadhaarMasking().execute("", "NO", null);
                } else {
                    dialog.dismiss();
                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.REQUEST_TIME_OUT, crashProof.getErrorType()), null, null);
                    finish();

                }
            } else if (crashProof.getResponse() != null) {

                if (crashProof.getHeaderStatusCode() == 502 || crashProof.getHeaderStatusCode() == 500) {
                    dialog.dismiss();
                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.INTERNAL_SERVER_ERROR, "Internal Server Error"), null, null);
                    finish();
                } else if (crashProof.getHeaderStatusCode() == 400) {
                    dialog.dismiss();
                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.BAD_REQUEST, "Bad request"), null, null);
                    finish();
                } else if (crashProof.getHeaderStatusCode() == 401) {
                    dialog.dismiss();
                    LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.UN_AUTH, "Unauthorized request"), null, null);
                    finish();
                } else {
                    try {
                        JSONObject header = new JSONObject();
                        header.put("Reference-Id", crashProof.getReferenceid());
                        header.put("Request-Id", crashProof.getRequestid());

                        ResponseBody responseBody = crashProof.getResponse().body();
                        JSONObject jsonObject = crashProof.getResponseBodyJson();

                        if (responseBody != null) {

                            if (crashProof.getBodyStatusCode().equals("200")) {
                                if (jsonObject.has("config")) {
                                    jsonObject.remove("config");
                                }
                                /*if (jsonObject.has("config")) {
                                    paramBE = jsonObject.getJSONObject("config");

//                                    Log.e("santhuConfig", paramBE.toString());

                                    jsonObject.remove("config");

//                                    Log.e("santhuConfig2", jsonObject.toString());

                                    if (paramBE != null) {

                                        if (paramBE.has("Rx_L")) {
                                            try {
                                                Rx_L = paramBE.getDouble("Rx_L");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Rx_L = 0.7;
                                            }
                                        }
                                        if (paramBE.has("Rx_U")) {
                                            try {
                                                Rx_U = paramBE.getDouble("Rx_U");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Rx_U = 1.4;
                                            }
                                        }
                                        if (paramBE.has("Ry_L")) {
                                            try {
                                                Ry_L = paramBE.getDouble("Ry_L");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Ry_L = 0.8;
                                            }

                                        }
                                        if (paramBE.has("Ry_U")) {
                                            try {
                                                Ry_U = paramBE.getDouble("Ry_U");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Ry_U = 1.7;
                                            }

                                        }

                                        if (paramBE.has("slope")) {
                                            try {
                                                slope_T = paramBE.getDouble("slope");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                slope_T = 0.0556;
                                            }

                                        }
                                    }


                                }*//*else {
                                    Rx_L = 0.7f;
                                    Rx_U = 1.4f;
                                    Ry_L = 0.8f;
                                    Ry_U = 1.7f;
                                    slope_T = 1 / 18;
                                }*/
                                dialog.dismiss();
                                LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(null,jsonObject,header);
                                finish();


                            }  else {
                                dialog.dismiss();
                                LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.PROPER_AADHAAR_NOT_FOUND, jsonObject.getString("error")), null, null);
                                finish();
//                                logDocApiErrorEvent(referenceId, "Bad Request");
                            }
                        } else {
                            dialog.dismiss();
                            LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.BAD_REQUEST, "Bad Request"), null, null);
                            finish();
                        }
                    } catch (JSONException e) {
                        dialog.dismiss();
                        LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.RESPONSE_JSON_E, "JSON Exception"), null, null);
                        finish();
                    }
                }
            } else {
                dialog.dismiss();
                LiveFaceAuthHolder.getInstance().getDocCaptureResultListener().onResult(new JukshioError(JukshioError.RESPONSE_NULL, "Request Failed"), null, null);
                finish();
            }
        }
    }
    private JSONObject getHeadersData(Headers headers) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (int i = 0, size = headers.size(); i < size; i++) {
            result.put(headers.name(i), headers.value(i));
        }
        JSONObject headerObject = new JSONObject(result);
//        Log.e("checkHeaderObj",headerObject+"");
        return headerObject;
    }



}