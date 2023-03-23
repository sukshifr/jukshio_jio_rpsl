package com.jukshio.JioRPSLlib.Networking;

/*
 * Jukshio Corp CONFIDENTIAL

 * Jukshio Corp 2018
 * All Rights Reserved.

 * NOTICE:  All information contained herein is, and remains
 * the property of Jukshio Corp. The intellectual and technical concepts contained
 * herein are proprietary to Jukshio Corp
 * and are protected by trade secret or copyriget law of U.S.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Jukshio Corp
 */

import static com.jukshio.JioRPSLlib.Activities.JukshioDocActivity.aadhaarAnalysis;
import static com.jukshio.JioRPSLlib.ImageAnalysis.AadhaarAnalysis.clientAadharModel_Error;
import static com.jukshio.JioRPSLlib.ImageAnalysis.AadhaarAnalysis.runningAadharModelName;
import static okhttp3.Protocol.HTTP_1_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.appcenter.analytics.Analytics;
import com.jukshio.JioRPSLlib.APICompletionCallback;
import com.jukshio.JioRPSLlib.CrashProof;
import com.jukshio.JioRPSLlib.DeletefilesCallback;
import com.jukshio.JioRPSLlib.LibUtils;
import com.jukshio.JioRPSLlib.R;
import com.jukshio.JioRPSLlib.SDKinitializationCallback;
import com.jukshio.JioRPSLlib.JukshioError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
 * This is helper class used all network operations using okhttp.
 **/
public class JukshioNetworkHelper {
    private APICompletionCallback onResponseReceived;
    private String docPath, facePath, eABottomImgPath, eAFullImgPath;
    public static String path224llll = "", pathHalfDoc = "";
    public Context context;
    public static JSONObject frontJSON = null, rearJSON = null;
    private boolean frontOrNot, allowDataLogging, doSuccessLogs;
    public static boolean isFront = false, isBack = false, isAadhaarJourney = false;
    public static String ocrFrontBlock = "", ocrBackBlock = "", docfaceCoordinates = "", liveFaceCoordinates = "", ocrBackPlots = "", ocrFrontPlots = "";
    private int env, matchType;
    private String referenceId, app_id;
    public static String frontResponse = "", eABottomImgOCRText = "", EA_Full_xCallId = "", EA_Bottom_xCallId = "", eAPOIResponse = "", frontInfo = "", backInfo = "", eafulldocInfo = "", eaFrontInfo = "", eabackInfo = "";
    private String authTok = "", secretKey = "", storeID = "", circleID = "", masterID = "", agentID = "", cafNumber = "", gtrr = "";
    private String ssl_Key = "";
    private SharedPreferences prefs;
    private String sdk_version = "", document = "", journey = "", qr_value = "", deviceModel = "NA", domainUrl = "";
    public static JSONObject paramBE = null;
    public static int document_Type = 0;
    public static JSONObject frontInfoObj = new JSONObject();
    public static JSONObject backInfoObj = new JSONObject();
    public static JSONObject eafulldocInfoObj = new JSONObject();
    public static JSONObject eabottomdocInfoObj = new JSONObject();
    public static JSONObject eafrontInfoObj = new JSONObject();
    public static JSONObject eabackInfoObj = new JSONObject();
    public static JSONObject customerFacematchObj = new JSONObject();
    public int eAFull_trial_count = 1, eABottom_trial_count = 1, eApoi_trial_count = 1, eApoa_trial_count = 1, poi_trial_count = 1, poa_trial_count = 1;
    private boolean useSSL = false;
    public SharedPreferences.Editor editor;
    public String app_type = "", lat = "", lng = "", sub_type = "", match_doc = "", account_number = "", ifsc = "", str1 = "", str2 = "", epic_number = "";

    String checkSumKey = "";
    String AadharSdkModelUrl = "", FaceSdkModelUrl = "", Sdk_Aadhar_ModelPath = "", Sdk_Face_ModelPath = "", Sdk_Aadhar_ModelToken = "", Sdk_Face_ModelToken = "", Aadhar_ModelFileName = "", Face_ModelFileName = "";
    boolean isAndroid = false, isModelUpdate = false;
    //    String updateModelType = "";
    String assetAadharModelName = "22082019_Aadhaar.tflite";
    String assetFaceModelName = "06052021_Face.tflite";
    int aadhar_imageWidth, aadhar_imageHeight, face_imageWidth, face_imageHeight, aadhar_frameThreshold, face_frameThreshold, aadhar_outputSize, face_outputSize;
    LibUtils libUtils;
    File Aadharmodelfile, FaceModelFile;

    private static final long START_TIME_IN_MILLIS = 90000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    TextView timerTv;
    ProgressDialog progressDialog;
    boolean isTimeOver = false;
    public static boolean isAadharModelTobeDownload = false, isFaceModelTobeDownload = false;
    public boolean isDefaultAadharModelFileExist = false, isDefaultFaceModelFileExist = false, isNewAadharModelFileExist = false, isNewFaceModelFileExist = false;
    public boolean useAssetModel_Face = true, useAssetModel_Aadhar = true;
    public static String POI_docType = "", POA_docType = "", POI_journey = "", POA_journey = "";
    Activity activity;
    SDKinitializationCallback sdKinitializationCallback;
    DeletefilesCallback deletefilesCallback;
    String aadhar = "", name = "", father = "", mother = "", dob = "", gender = "", yob = "";
    String husband="",address="",city="",state="",line1="",line2="",pin="",locality="",house_number="",district="",street="",landmark="";

    /**
     * Constructor to initialise with context and string appid and type of environment.
     * This is also used to get auth token by generating jwt token for passing on all network calls.
     **/
    @SuppressLint("CommitPrefEdits")
    public JukshioNetworkHelper(Context context1, String appId, String secureKey, String domain_url) {
        this.context = context1;
        this.app_id = appId;
//        this.env = env;
        this.secretKey = secureKey;
        this.sdk_version = context1.getString(R.string.sdk_version);
        doSuccessLogs = false;
        this.domainUrl = domain_url;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String stringSSLData = context.getResources().getString(R.string.certificate_data);
        try {
            byte[] data = Base64.decode(stringSSLData, Base64.DEFAULT);
            ssl_Key = new String(data, "UTF-8");
//            Log.e("decytpion",ssl_Key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        String previousDateString = prefs.getString("date_string", "");
//        Log.e("authdateString", previousDateString);

        authTok = prefs.getString("auth_token", "");
        useSSL = prefs.getBoolean("useSSL", false);
        progressDialog = new ProgressDialog(context);
        timerTv = (TextView) progressDialog.findViewById(R.id.timerTv);
//        Log.e("prefesAuthTOken1", String.valueOf(useSSL));

//        String currentDate = getDateString();

//        new ApiCallForAuthToken().execute("","YES",null);

   /*     if (!previousDateString.equals("")) {
            if (!currentDate.equals(previousDateString)) {
                if (checkInternetConnection()) {
                    new ApiCallForAuthToken().execute();
                } else {
                    Toast.makeText(context, "No Active Internet Connection found", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (checkInternetConnection()) {
                new ApiCallForAuthToken().execute();
            } else {
                Toast.makeText(context, "No Active Internet Connection found", Toast.LENGTH_SHORT).show();
            }
        }*/
//        initAnalytics(context1);
    }

    public void initSDK(Activity activity, String appId, String secureKey, String app_type,String domain_Url, SDKinitializationCallback sdKinitializationCallback) {
        this.sdKinitializationCallback = sdKinitializationCallback;
        this.activity = activity;
        this.app_id = appId;
        this.secretKey = secureKey;
        this.domainUrl=domain_Url;
//        this.env = env;
        this.app_type = app_type;
        if (app_type.isEmpty())
            this.app_type = "A";
        String previousDateString = prefs.getString("date_string", "");
        String currentDate = getDateString();
//        new ApiCallForAuthToken().execute("","YES",null);
        if (!previousDateString.equals("")) {
            if (!currentDate.equals(previousDateString)) {
                if (checkInternetConnection()) {
                    new ApiCallForAuthToken().execute("", "YES", null);
                } else {
                    Toast.makeText(context, "No Active Internet Connection found", Toast.LENGTH_SHORT).show();
                    this.sdKinitializationCallback.OnInitialized("No Active Internet Connection found", false);
                }
            } else {
                this.sdKinitializationCallback.OnInitialized("init complete", true);
            }
        } else {
            if (checkInternetConnection()) {
                new ApiCallForAuthToken().execute("", "YES", null);
            } else {
                Toast.makeText(context, "No Active Internet Connection found", Toast.LENGTH_SHORT).show();
                this.sdKinitializationCallback.OnInitialized("No Active Internet Connection found", false);
            }
        }

    }



    /**
     * This is AsyncTask to make auth token network call creating new thread and passing on the response to main Ui thread.
     **/
    @SuppressLint("StaticFieldLeak")
    public class ApiCallForAuthToken extends AsyncTask<String, String, CrashProof> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   if (progressDialog == null)
                progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Setting up things...");
//            progressDialog.setCancelable(false);
//                  dialog.setCanceledOnTouchOutside(false);
//            progressDialog.setIndeterminate(false);
            //  progressDialog.setMax(100);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setProgress(0);
//            progressDialog.setMax(100);
            progressDialog.show();
            progressDialog.setContentView(R.layout.settingup_things_dailog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCancelable(false);
            timerTv = (TextView) progressDialog.findViewById(R.id.timerTv);
            timerTv.setVisibility(View.GONE);*/

        }

        @Override
        protected CrashProof doInBackground(String... strings) {

            String url = null, server = null;
            server = domainUrl;
            url = "https://" + server + "/v1/auth_token";
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
                    url = "https://" + server + "/v1/auth_token";
                    break;
                case 1:
                    server = context.getString(R.string.prod_server);
                    url = "https://" + server + "/v1/auth_token";
                    break;
                case 2:
                    server = context.getString(R.string.replica_server);
                    url = "https://" + server + "/v1/auth_token";
                    break;

                case 3:
                    server = context.getString(R.string.ent_prod_server);
                    url = "https://" + server + "/v1/auth_token";
                    break;

                case 4:
                    server = context.getString(R.string.az_entdkyc_repl_server);
                    url = "https://" + server + "/v1/auth_token";
                    break;
//                case 5:
//                    server = context.getString(R.string.az_preprod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;

//                case 4:
//                    server = context.getString(R.string.az_dev_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//
//                case 5:
//                    server = context.getString(R.string.az_prod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//
//                case 6:
//                    server = context.getString(R.string.az_preprod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//
//                case 7:
//                    server = context.getString(R.string.az_sandbox_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//                case 8:
//                    server = context.getString(R.string.new_prod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//
//                case 9:
//                    server = context.getString(R.string.new_ent_prod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
//                case 10:
//                    server = context.getString(R.string.new_az_prod_server);
//                    url = "https://" + server + "/v1/auth_token";
//                    break;
            }
*/
            //Log.e("JukshioSukshiUrl", url);
            OkHttpClient client;
            useSSL = prefs.getBoolean("useSSL", false);
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


            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder.setType(MultipartBody.FORM);
            multipartBodyBuilder.addFormDataPart("app_id", app_id);
            multipartBodyBuilder.addFormDataPart("version", sdk_version);
            multipartBodyBuilder.addFormDataPart("store_id", storeID);
            multipartBodyBuilder.addFormDataPart("circle_id", circleID);
            multipartBodyBuilder.addFormDataPart("master_id", masterID);
            multipartBodyBuilder.addFormDataPart("agent_id", agentID);
            multipartBodyBuilder.addFormDataPart("caf_number", cafNumber);
            multipartBodyBuilder.addFormDataPart("gt_rr", gtrr);
            multipartBodyBuilder.addFormDataPart("isSDK", "1");
            multipartBodyBuilder.addFormDataPart("app_type", app_type);
            multipartBodyBuilder.addFormDataPart("lat", lat);
            multipartBodyBuilder.addFormDataPart("long", lng);
            String jwtToken = JwtGenerator.getToken(app_id, secretKey);

//            Log.e("JukshioSukshiTokenURL", url);
//            Log.e("JukshioSukshiTokenIn", jwtToken);

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + jwtToken)
                    .post(multipartBodyBuilder.build())
                    .build();

            Response response = null;
            CrashProof crashProof;
            try {

                response = client.newCall(request).execute();
//                Log.e("JukshioSukshiTokenResp", response.toString());

                if (response.code() == 200) {

                    Headers headers = response.headers();
//                    Log.e("JukshioAuthTokiHead", headers.toString());
                    String authTOken = headers.get("Auth-Token");
                    Log.e("JukshioSukshiTokenOut", authTOken);
                    ResponseBody responseBody = response.body();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    crashProof = new CrashProof(null, response, 0);
                    crashProof.setResponseBodyJson(jsonObject);
                    crashProof.setAuth_token(authTOken);
                    crashProof.setHeaderStatusCode(200);
//                    Log.e("JukshioAuthTokBody", jsonObject.toString());
                    boolean send_images, useSSL, useAssetModel_Face = false, useAssetModel_Aadhar = false;
                    String Server_ssl_key = "";
                    if (jsonObject.has("send_images")) {
                        send_images = jsonObject.getBoolean("send_images");
                    } else {
                        send_images = true;
                    }

                   /* if (jsonObject.has("ssl_key")) {
                        Server_ssl_key = jsonObject.getString("ssl_key");
                    } else {
                        Server_ssl_key = context.getResources().getString(R.string.certificateKey);
                    }*/

                    String formattedDate = getDateString();
//                    Log.e("JukshioSukshiDate", formattedDate);

                    editor.putString("date_string", formattedDate);
                    editor.putString("auth_token", authTOken);
                    editor.putBoolean("send_images", send_images);
                    editor.apply();
                    return crashProof;
                } else {
                    crashProof = new CrashProof("Something went wrong", response, 1);
                    crashProof.setHeaderStatusCode(response.code());
                    crashProof.setResponseBodyJson(null);
                    return crashProof;
                }
            } catch (SSLException sslE) {
                sslE.printStackTrace();
                crashProof = new CrashProof("error:" + sslE.getMessage(), response, 3);
                crashProof.setHeaderStatusCode(0);
                crashProof.setResponseBodyJson(null);
                return crashProof;
            } catch (IOException e) {
                e.printStackTrace();
                crashProof = new CrashProof("error:" + e.getMessage(), response, 1);
                crashProof.setHeaderStatusCode(0);
                crashProof.setResponseBodyJson(null);
                return crashProof;
            } catch (JSONException e) {
                e.printStackTrace();
                crashProof = new CrashProof("error:" + e.getMessage(), response, 1);
                crashProof.setHeaderStatusCode(0);
                crashProof.setResponseBodyJson(null);
                return crashProof;
            }

        }

        @Override
        protected void onPostExecute(CrashProof crashProof) {
            super.onPostExecute(crashProof);
            if (prefs == null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(context);
                editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            }
            try {

                if (crashProof.getHeaderStatusCode() == 200) {
                    String generatedCheksum = "", serverChecksum = "";
//                    Log.e("responseString",crashProof.getAuth_token());
                    JSONObject responseObj = crashProof.getResponseBodyJson();
                    if (responseObj.has("checksum")) {
                        serverChecksum = responseObj.getString("checksum");
                    }
                    if (responseObj.has("sdk_model_details")) {
                        JSONObject modelObject = responseObj.getJSONObject("sdk_model_details");
                        String encryptionData = context.getResources().getString(R.string.encryption_data);
                        String decryption_data = "";
                        try {
                            byte[] data = Base64.decode(encryptionData, Base64.DEFAULT);
                            decryption_data = new String(data, "UTF-8");
//                            Log.e("decytpion",decryption_data);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        generatedCheksum = generateHashWithHmac256(modelObject.toString(), decryption_data);

//                        Log.e("checkSum",generatedCheksum+"   :     "+modelObject.toString());
                        if (modelObject.has("sdk_config")) {
                            paramBE = modelObject.getJSONObject("sdk_config");

                            if (paramBE != null) {

                                if (paramBE.has("useSSL")) {
                                    useSSL = paramBE.getBoolean("useSSL");
                                } else {
                                    useSSL = false;
                                }
                                if (paramBE.has("useAssetModel_Face")) {
                                    useAssetModel_Face = paramBE.getBoolean("useAssetModel_Face");
                                } else {
                                    useAssetModel_Face = false;
                                }
                                if (paramBE.has("useAssetModel_Aadhar")) {
                                    useAssetModel_Aadhar = paramBE.getBoolean("useAssetModel_Aadhar");
                                } else {
                                    useAssetModel_Aadhar = false;
                                }
                                editor.putBoolean("useSSL", useSSL).apply();
                                editor.putBoolean("useAssetModel_Face", useAssetModel_Face).apply();
                                editor.putBoolean("useAssetModel_Aadhar", useAssetModel_Aadhar).apply();
//                                editor.putBoolean("useAssetModel_Face", true).apply();
//                                editor.putBoolean("useAssetModel_Aadhar", true).apply();
                            }

                        }
                        if (modelObject.has("Sdk_Aadhar_ModelPath")) {
                            String sdkModelPath1 = modelObject.getString("Sdk_Aadhar_ModelPath");
                            byte[] data = Base64.decode(sdkModelPath1, Base64.DEFAULT);
                            Sdk_Aadhar_ModelPath = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Sdk_Aadhar_ModelPath);
                        }
                        if (modelObject.has("Sdk_Face_ModelPath")) {
                            String sdkModelPath1 = modelObject.getString("Sdk_Face_ModelPath");
                            byte[] data = Base64.decode(sdkModelPath1, Base64.DEFAULT);
                            Sdk_Face_ModelPath = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Sdk_Face_ModelPath);
                        }
                        if (modelObject.has("Sdk_Aadhar_ModelToken")) {
                            String sdkModelKey1 = modelObject.getString("Sdk_Aadhar_ModelToken");
                            byte[] data = Base64.decode(sdkModelKey1, Base64.DEFAULT);
                            Sdk_Aadhar_ModelToken = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Sdk_Aadhar_ModelToken);
                        }
                        if (modelObject.has("Sdk_Face_ModelToken")) {
                            String sdkModelKey1 = modelObject.getString("Sdk_Face_ModelToken");
                            byte[] data = Base64.decode(sdkModelKey1, Base64.DEFAULT);
                            Sdk_Face_ModelToken = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Sdk_Face_ModelToken);
                        }
                        if (modelObject.has("Aadhar_ModelFileName")) {
                            String sdkModelName1 = modelObject.getString("Aadhar_ModelFileName");
                            byte[] data = Base64.decode(sdkModelName1, Base64.DEFAULT);
                            Aadhar_ModelFileName = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Aadhar_ModelFileName);
                        }
                        if (modelObject.has("Face_ModelFileName")) {
                            String sdkModelName1 = modelObject.getString("Face_ModelFileName");
                            byte[] data = Base64.decode(sdkModelName1, Base64.DEFAULT);
                            Face_ModelFileName = new String(data, "UTF-8");
//                            Log.e("decryptedURL", Face_ModelFileName);
                        }
                        AadharSdkModelUrl = Sdk_Aadhar_ModelPath + "?" + Sdk_Aadhar_ModelToken;
                        FaceSdkModelUrl = Sdk_Face_ModelPath + "?" + Sdk_Face_ModelToken;
//                        Log.e("decryptedURL", AadharSdkModelUrl);
//                        Log.e("decryptedURL", FaceSdkModelUrl);
                        if (modelObject.has("IsAndroid")) {
                            isAndroid = modelObject.getBoolean("IsAndroid");
                        } else {
                            isAndroid = false;
                        }
                        if (modelObject.has("aadhar_imageWidth")) {
                            String width = modelObject.getString("aadhar_imageWidth");
                            byte[] data = Base64.decode(width, Base64.DEFAULT);
                            aadhar_imageWidth = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            aadhar_imageWidth = 0;
                        }
                        if (modelObject.has("face_imageWidth")) {
                            String width = modelObject.getString("face_imageWidth");
                            byte[] data = Base64.decode(width, Base64.DEFAULT);
                            face_imageWidth = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            face_imageWidth = 0;
                        }
                        if (modelObject.has("aadhar_imageHeight")) {
                            String height = modelObject.getString("aadhar_imageHeight");
                            byte[] data = Base64.decode(height, Base64.DEFAULT);
                            aadhar_imageHeight = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            aadhar_imageHeight = 0;
                        }
                        if (modelObject.has("face_imageHeight")) {
                            String height = modelObject.getString("face_imageHeight");
                            byte[] data = Base64.decode(height, Base64.DEFAULT);
                            face_imageHeight = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            face_imageHeight = 0;
                        }
                        if (modelObject.has("aadhar_frameThreshold")) {
                            String thrshold = modelObject.getString("aadhar_frameThreshold");
                            byte[] data = Base64.decode(thrshold, Base64.DEFAULT);
                            aadhar_frameThreshold = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            aadhar_frameThreshold = 15;
                        }
                        if (modelObject.has("face_frameThreshold")) {
                            String thrshold = modelObject.getString("face_frameThreshold");
                            byte[] data = Base64.decode(thrshold, Base64.DEFAULT);
                            face_frameThreshold = Integer.parseInt(new String(data, "UTF-8"));

                        } else {
                            face_frameThreshold = 15;
                        }
                        if (modelObject.has("aadhar_outputSize")) {
                            String outputsize = modelObject.getString("aadhar_outputSize");
                            byte[] data = Base64.decode(outputsize, Base64.DEFAULT);
                            aadhar_outputSize = Integer.parseInt(new String(data, "UTF-8"));
                        } else {
                            aadhar_outputSize = 1;
                        }
                        if (modelObject.has("face_outputSize")) {
                            String outputsize = modelObject.getString("face_outputSize");
                            byte[] data = Base64.decode(outputsize, Base64.DEFAULT);
                            face_outputSize = Integer.parseInt(new String(data, "UTF-8"));

                        } else {
                            face_outputSize = 1;
                        }

                       /* if (modelObject.has("updateModelType")) {
                            updateModelType = modelObject.getString("updateModelType");
                        } else {
                            updateModelType = "";
                        }*/

//                        Log.e("isAdnroid", isAndroid + "");
                        if (serverChecksum.equals(generatedCheksum)) {
//                            Log.e(TAG1,"Checksum passed");
//                            if (isAndroid) {
//                                if (updateModelType.equalsIgnoreCase("Aadhar")) {

                            if (!assetAadharModelName.equals(Aadhar_ModelFileName)) {
//                                            Log.e(TAG1, ": 1");
                                boolean isnewAadharModelExist = prefs.getBoolean("newAadharModelDownloaded", false);
                                boolean isDefaultModelExist = prefs.getBoolean("isDefaultAadharModelExist", false);


                                if (isnewAadharModelExist) {
                                    String savednewAadharFileName = prefs.getString("newAadharModelName", "");
                                    if (savednewAadharFileName != null && !savednewAadharFileName.isEmpty()) {
                                        if (libUtils == null)
                                            libUtils = new LibUtils();
                                        File storedModel = libUtils.getAadharModelFile(context, savednewAadharFileName);
                                        isNewAadharModelFileExist = storedModel.exists();
                                    } else {
                                        isNewAadharModelFileExist = false;
                                    }

                                    if (isNewAadharModelFileExist) {
                                        if (savednewAadharFileName != null && !savednewAadharFileName.isEmpty() && !savednewAadharFileName.equals(Aadhar_ModelFileName)) {
                                            //download new model
                                            isAadharModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();


                                        } else if (savednewAadharFileName == null || savednewAadharFileName.isEmpty()) {
                                            // download new model
//                                                    Log.e(TAG1, ": 4");
                                            isAadharModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();
                                        } else {

//                                          // load saved model  model
                                            isAadharModelTobeDownload = false;
                                            editor.putString("useModelforAadhar", "default").apply();
                                        }
                                    } else {
                                        // download new model
//                                        Log.e(TAG1, ": 5");
                                        isAadharModelTobeDownload = true;
//                                                new ApiCallFordownloadModelUpdate().execute();
                                    }

                                } else if (isDefaultModelExist) {
                                    String savedAadharFileName = prefs.getString("defaultAadharModelName", "");
                                    if (savedAadharFileName != null && !savedAadharFileName.isEmpty()) {
                                        if (libUtils == null)
                                            libUtils = new LibUtils();
                                        File storedModel = libUtils.getAadharModelFile(context, savedAadharFileName);
                                        isDefaultAadharModelFileExist = storedModel.exists();
                                    } else {
                                        isDefaultAadharModelFileExist = false;
                                    }
                                    if (isDefaultAadharModelFileExist) {
                                        if (savedAadharFileName != null && !savedAadharFileName.isEmpty() && !savedAadharFileName.equals(Aadhar_ModelFileName)) {
                                            //download new model
                                            isAadharModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();


                                        } else if (savedAadharFileName == null || savedAadharFileName.isEmpty()) {
                                            // download new model
//                                                    Log.e(TAG1, ": 4");
                                            isAadharModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();
                                        } else {

//                                          // load saved model  model
                                            isAadharModelTobeDownload = false;
                                            editor.putString("useModelforAadhar", "default").apply();
                                        }
                                    } else {
                                        // download new model
//                                        Log.e(TAG1, ": 5");
                                        isAadharModelTobeDownload = true;
//                                                new ApiCallFordownloadModelUpdate().execute();
                                    }
                                } else {
                                    isAadharModelTobeDownload = true;
//                                            new ApiCallFordownloadModelUpdate().execute();
                                }

                            } else {

//                                      // load default model
                                isAadharModelTobeDownload = false;
                                editor.putString("useModelforAadhar", "default").apply();
                            }
//                                } else if (updateModelType.equalsIgnoreCase("Face")) {

                            if (!assetFaceModelName.equals(Face_ModelFileName)) {
//                                            Log.e(TAG1, ": 1");
                                boolean isnewFaceModelExist = prefs.getBoolean("newFaceModelDownloaded", false);
                                boolean isDefaultModelExist = prefs.getBoolean("isDefaultFaceModelExist", false);
                                if (isnewFaceModelExist) {

                                    String savedNewFaceModelFileName = prefs.getString("newFaceModelName", "");
                                    if (savedNewFaceModelFileName != null && !savedNewFaceModelFileName.isEmpty()) {
                                        if (libUtils == null)
                                            libUtils = new LibUtils();
                                        File storedModel = libUtils.getFaceModelFile(context, savedNewFaceModelFileName);
                                        isNewFaceModelFileExist = storedModel.exists();
                                    } else {
                                        isNewFaceModelFileExist = false;
                                    }
                                    if (isNewFaceModelFileExist) {
                                        if (savedNewFaceModelFileName != null && !savedNewFaceModelFileName.isEmpty() && !savedNewFaceModelFileName.equals(Face_ModelFileName)) {
                                            //download new model
                                            isFaceModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();


                                        } else if (savedNewFaceModelFileName == null || savedNewFaceModelFileName.isEmpty()) {
                                            // download new model
//                                                    Log.e(TAG1, ": 4");
                                            isFaceModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();
                                        } else {

//                                          // load saved model  model
                                            isFaceModelTobeDownload = false;
                                            editor.putString("useModelforFace", "default").apply();
                                        }
                                    } else {
                                        // download new model
//                                        Log.e(TAG1, ": 5");
                                        isFaceModelTobeDownload = true;
//                                                new ApiCallFordownloadModelUpdate().execute();
                                    }

                                } else if (isDefaultModelExist) {
                                    String savedFaceModelFileName = prefs.getString("defaultFaceModelName", "");
                                    if (savedFaceModelFileName != null && !savedFaceModelFileName.isEmpty()) {
                                        if (libUtils == null)
                                            libUtils = new LibUtils();
                                        File storedModel = libUtils.getFaceModelFile(context, savedFaceModelFileName);
                                        isDefaultFaceModelFileExist = storedModel.exists();
                                    } else {
                                        isDefaultFaceModelFileExist = false;
                                    }
                                    if (isDefaultFaceModelFileExist) {
                                        if (savedFaceModelFileName != null && !savedFaceModelFileName.isEmpty() && !savedFaceModelFileName.equals(Face_ModelFileName)) {
                                            //download new model
                                            isFaceModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();


                                        } else if (savedFaceModelFileName == null || savedFaceModelFileName.isEmpty()) {
                                            // download new model
//                                                    Log.e(TAG1, ": 4");
                                            isFaceModelTobeDownload = true;
//                                                    new ApiCallFordownloadModelUpdate().execute();
                                        } else {

//                                          // load saved model  model
                                            isFaceModelTobeDownload = false;
                                            editor.putString("useModelforFace", "default").apply();
                                        }
                                    } else {
                                        // download new model
//                                        Log.e(TAG1, ": 5");
                                        isFaceModelTobeDownload = true;
//                                                new ApiCallFordownloadModelUpdate().execute();
                                    }
                                } else {
                                    isFaceModelTobeDownload = true;
//                                            new ApiCallFordownloadModelUpdate().execute();
                                }

                            } else {

//                                      // load default model
                                isFaceModelTobeDownload = false;
                                editor.putString("useModelforFace", "default").apply();
                            }
                                /*} else {
                                    //load default
                                    editor.putString("useModelforFace", "default").apply();
                                    editor.putString("useModelforAadhar", "default").apply();
                                }*/

                            /*} else {
                                //load default
                                editor.putString("useModelforFace", "default").apply();
                                editor.putString("useModelforAadhar", "default").apply();
                                isFaceModelTobeDownload = false;
                                isAadharModelTobeDownload = false;

                            }*/
                        } else {
                            //checksum not matched
                            // load default model
                            isFaceModelTobeDownload = false;
                            isAadharModelTobeDownload = false;
                            editor.putString("useModelforFace", "default").apply();
                            editor.putString("useModelforAadhar", "default").apply();

                        }

                    } else {
                        //load default model


                        editor.putString("useModelforFace", "default").apply();
                        editor.putString("useModelforAadhar", "default").apply();
                        isFaceModelTobeDownload = false;
                        isAadharModelTobeDownload = false;
                    }


                } else {

                    if (crashProof.getExceptionCode() == 3) {
                        new ApiCallForAuthToken().execute("", "NO", null);
                    } else {
                        //load default model
                        editor.putString("useModelforFace", "default").apply();
                        editor.putString("useModelforAadhar", "default").apply();
                        isFaceModelTobeDownload = false;
                        isAadharModelTobeDownload = false;
                    }
                }
//                new ApiCallFordownloadModelUpdate().execute();


            } catch (JSONException e) {
                e.printStackTrace();
                // load default model
                editor.putString("useModelforFace", "default").apply();
                editor.putString("useModelforAadhar", "default").apply();
                isFaceModelTobeDownload = false;
                isAadharModelTobeDownload = false;
            } catch (IOException e) {
                e.printStackTrace();
                //load default model
                editor.putString("useModelforFace", "default").apply();
                editor.putString("useModelforAadhar", "default").apply();
                isFaceModelTobeDownload = false;
                isAadharModelTobeDownload = false;
            } catch (Exception e) {
                e.printStackTrace();
                //load default model
                editor.putString("useModelforFace", "default").apply();
                editor.putString("useModelforAadhar", "default").apply();
                isFaceModelTobeDownload = false;
                isAadharModelTobeDownload = false;
            }

            useAssetModel_Aadhar = prefs.getBoolean("useAssetModel_Aadhar", false);
            useAssetModel_Face = prefs.getBoolean("useAssetModel_Face", false);
//            useAssetModel_Aadhar = true;
//            useAssetModel_Face = true;
            //Conditions for Checking and proceeding for the new model Download

        }
    }

    /**
     * Return a boolean to detect internet.
     **/
    private boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected();
    }

    /**
     * Returns string with current date.
     **/
    private String getDateString() {

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return dateFormat.format(date);
    }


    /* Method for generating checksum for the given string
     **
     */
    private String generateHashWithHmac256(String message, String key) {
        String messageDigest = "";
        try {
            final String hashingAlgorithm = "HmacSHA256"; //or "HmacSHA1", "HmacSHA512"

            byte[] bytes = hmac(hashingAlgorithm, key.getBytes(), message.getBytes());

            messageDigest = bytesToHex(bytes);

            Log.i("KeyGeneration", "message digest: " + messageDigest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageDigest;
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void updateCountDownText() {
//        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
//        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        int seconds = (int) (mTimeLeftInMillis / 1000);

//        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        timerTv.setText(timeLeftFormatted);

    }

    public void returnRespCallback() {
        sdKinitializationCallback.OnInitialized("init complete", true);
    }

}
