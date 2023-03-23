package com.jukshio.JioRPSLlib.ImageAnalysis;

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

import static com.jukshio.JioRPSLlib.Activities.JukshioDocActivity.frontOrNot;
import static com.jukshio.JioRPSLlib.Camera.CameraSource.framesAddedCount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.preference.PreferenceManager;

import com.jukshio.JioRPSLlib.LibUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class used for aadhaarAnalysis using tensorflow lite.
 **/
public class AadhaarAnalysis {

    public AssetManager assetManager;
    public Context context;

    public Queue<ImageObject> docBitmapQueue = new LinkedList<>();
    public ArrayList<String> docFrontResultStringArray = new ArrayList<>();
    public ArrayList<String> docBackResultStringArray = new ArrayList<>();
    public boolean okForDocTask, firstDocFrame;
    public static boolean okForDocAnalysis, readyForDocAnalysis = false, docCaptureComplete = false;
    //    public static boolean canAddDocData = true;
    public Bitmap realImage, fakeImage;


//    private final int DOC_INPUT_SIZE = 224;

    //    private Classifier classifier;
//    private Executor docExecutor = Executors.newSingleThreadExecutor();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LibUtils utils;

    public static AadharModel newAadharModelRef, defaultAadharModelRef, assetAadharModelRef;
    public static String useModelforAadhar = "";
    public static String defaultAadharModelName = "", newAadharModelName = "",clientAadharModel_Error="";
    public static boolean newAadharModelDownloaded = false;
    public static File newAadharModelFile, defaultAadharModelFile;

    //width and height
    public static int defaultAadharImageWidth, defaultAadharImageHeight, newAadharImageWidth, newAadharImageHeight, newAadharFrameThreshold, defaultAadharFrameThreshold, newAadharOutputSize;

    public static boolean isNewModelSuccess = false, isNewAadharModelFileExist = false, isDefaultAadharModelFileExist = false;


    public static int docTotalNumFrames = 0, docRealFrames = 0, docScreenFrames = 0;
    public static float realSum = 0, fakeSum = 0;
    public boolean useAssetModel_Aadhar = false;
    public static String runningAadharModelName = "";

    /**
     * Constructor to initialise assetmanager and context.
     **/
    public AadhaarAnalysis(AssetManager assetManager, Context context) {
        this.assetManager = assetManager;
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        utils = new LibUtils();
//        libUtils = new LibUtils();
    }

    /**
     * This is a method initialise tensflow classifier with model stored in the assets.
     **/
    public void initAadhaarAnalysis() {

        newAadharModelDownloaded = preferences.getBoolean("newAadharModelDownloaded", false);
        useAssetModel_Aadhar = preferences.getBoolean("useAssetModel_Aadhar", false);
        if (!useAssetModel_Aadhar) {
            if (newAadharModelDownloaded) {
                loadNewModel();
//            loadDefaultModel();
            } else {
                loadDefaultModel();
            }
        } else {
            loadAssetModel();
        }
    }

    //method for loading newly downloaded model
    private void loadNewModel() {
        if (utils == null)
            utils = new LibUtils();
        try {
            newAadharModelName = preferences.getString("newAadharModelName", "");
//            Log.e("modelName", newAadharModelName);
            if (newAadharModelName != null && !newAadharModelName.isEmpty()) {
                newAadharModelFile = utils.getAadharModelFile(context, newAadharModelName);
                isNewAadharModelFileExist = newAadharModelFile.exists();
            } else {
                isNewAadharModelFileExist = false;
            }
            newAadharImageWidth = preferences.getInt("newAadharImageWidth", 224);
            newAadharImageHeight = preferences.getInt("newAadharImageHeight", 224);
            newAadharFrameThreshold = preferences.getInt("newAadharFrameThreshold", 15);
            if (isNewAadharModelFileExist) {
                try {
                    newAadharModelRef = new AadharModel(newAadharModelFile, newAadharImageWidth, newAadharImageHeight);
                    editor.putString("loadedAadharModelType", "new").apply();
                } catch (IOException e) {
                    clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                    e.printStackTrace();
//                    Log.e("Exception_Model", e.getMessage());
                    loadDefaultModel();
                } catch (Exception e) {
                    clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                    e.printStackTrace();
//                    Log.e("Exception_Model", e.getMessage());
                    loadDefaultModel();
                }
            } else {
                loadDefaultModel();
            }
        } catch (Exception e) {
//            Log.e("Exception_Model", e.getMessage());
            loadDefaultModel();
        }

    }

    // method for loading default saved model
    private void loadDefaultModel() {
        if (utils == null)
            utils = new LibUtils();
        try {
            defaultAadharModelName = preferences.getString("defaultAadharModelName", "");
            if (defaultAadharModelName != null && !defaultAadharModelName.isEmpty()) {
                defaultAadharModelFile = utils.getAadharModelFile(context, defaultAadharModelName);
                isDefaultAadharModelFileExist = defaultAadharModelFile.exists();
            } else {
                isDefaultAadharModelFileExist = false;
            }
            defaultAadharImageWidth = preferences.getInt("defaultAadharImageWidth", 224);
            defaultAadharImageHeight = preferences.getInt("defaultAadharImageHeight", 224);
            defaultAadharFrameThreshold = preferences.getInt("defaultAadharFrameThreshold", 15);
            if (isDefaultAadharModelFileExist) {
                try {
                    defaultAadharModelRef = new AadharModel(defaultAadharModelFile, defaultAadharImageWidth, defaultAadharImageHeight);
                    editor.putString("loadedAadharModelType", "default").apply();
                } catch (IOException e) {
                    e.printStackTrace();
//                    Log.e("Exception_Model", e.getMessage());
                    clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                    loadAssetModel();
                } catch (Exception e) {
                    e.printStackTrace();
                    clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
//                    Log.e("Exception_Model", e.getMessage());
                    loadAssetModel();
                }
            } else {
                loadAssetModel();
            }
        } catch (Exception e) {
//            Log.e("Exception_Model", e.getMessage());
            loadAssetModel();
        }


    }

    // method for loading asset model
    private void loadAssetModel() {

        try {
            assetAadharModelRef = new AadharModel(assetManager);
            editor.putString("loadedAadharModelType", "asset").apply();
        } catch (IOException e) {
            e.printStackTrace();
            clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
        }
    }

    /**
     * This is reset initial values of different variables.
     **/
    public void setupAadhaarAnalysis() {
        docTotalNumFrames = 0;
        /*docRealFrames = 0;
        docScreenFrames = 0;
        realSum = 0;
        fakeSum = 0;*/
        realImage = null;
        fakeImage = null;
        okForDocTask = true;
        firstDocFrame = true;
        okForDocAnalysis = true;
//        canAddDocData = true;
        docFrontResultStringArray.clear();
        docBackResultStringArray.clear();
        docCaptureComplete = false;
        framesAddedCount = 0;
    }

    public void startAadhaarAnalysis() {

        new docAsyncTask().execute();
    }

    /**
     * Method to start image analysis using async task and passing each image object in bitmapQueue obtain on camera frames.
     * Each ImageObject object is compress to create bitmap with byte array as parameter and then scaled down to 224*224 and then processed on tensorflow classifier.
     **/
    @SuppressLint("StaticFieldLeak")
    class docAsyncTask extends android.os.AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            Iterator<ImageObject> docIterator = docBitmapQueue.iterator();

            while (docIterator.hasNext()) {

                if (okForDocAnalysis) {

                    ImageObject docImageObject = docBitmapQueue.remove();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    YuvImage image = new YuvImage(docImageObject.byteArray, docImageObject.parameters.getPreviewFormat(),
                            docImageObject.previewW, docImageObject.previewH, null);
                    int quality = 100;   // adjust this as needed

                    image.compressToJpeg(new Rect(0, 0, docImageObject.previewW, docImageObject.previewH), quality, out);
                    byte[] finalByte = out.toByteArray();
                    Bitmap finalBitmap = BitmapFactory.decodeByteArray(finalByte, 0, finalByte.length);

                    Bitmap bitmap1 = Bitmap.createScaledBitmap(finalBitmap, 224, 224, false);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(docImageObject.angle);
                    Bitmap rotated = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);

                    float[] Aadhar_encodings = new float[0];
                    String loadedModelType = preferences.getString("loadedAadharModelType", "asset");
                    int newoutputSize = preferences.getInt("newAadharOutputSize", 2);
                    int defaultoutputSize = preferences.getInt("defaultAadharOutpuSize", 2);

                    if (!useAssetModel_Aadhar) {
                        if (loadedModelType.equalsIgnoreCase("new")) {
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, newAadharImageWidth, newAadharImageHeight, docImageObject.angle);
                                Aadhar_encodings = newAadharModelRef.run(newbitmap, newoutputSize, newAadharImageWidth, newAadharImageHeight);
                                isNewModelSuccess = true;
                                runningAadharModelName = newAadharModelName;
//                                Log.e("WhichModel_Running", "new");
                            } catch (ArrayIndexOutOfBoundsException arrayE) {
                                clientAadharModel_Error=clientAadharModel_Error+arrayE.getMessage();
                                isNewModelSuccess = false;
                                try {
                                    loadDefaultModel();
                                    if (defaultAadharModelRef!=null) {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultAadharImageWidth, defaultAadharImageHeight, docImageObject.angle);
                                        Aadhar_encodings = defaultAadharModelRef.run(newbitmap, defaultoutputSize, defaultAadharImageWidth, defaultAadharImageHeight);
                                        runningAadharModelName = defaultAadharModelName;
//                                    Log.e("WhichModel_Running", "new_default");
                                    }else {
                                        if (assetAadharModelRef == null)
                                            loadAssetModel();
                                        try {
                                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                            Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                            runningAadharModelName = AadharModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_null");
                                        } catch (Exception exception) {
                                            clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                                        }
                                    }
                                } catch (Exception e) {
                                    clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                                    if (assetAadharModelRef == null)
                                        loadAssetModel();
                                    try {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                        Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                        runningAadharModelName = AadharModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset");
                                    } catch (Exception exception) {
                                        clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                                    }
                                }
                            } catch (Exception e) {
                                clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                                isNewModelSuccess = false;
                                try {
                                    loadDefaultModel();
                                    if (defaultAadharModelRef!=null) {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultAadharImageWidth, defaultAadharImageHeight, docImageObject.angle);
                                        Aadhar_encodings = defaultAadharModelRef.run(newbitmap, defaultoutputSize, defaultAadharImageWidth, defaultAadharImageHeight);
                                        runningAadharModelName = defaultAadharModelName;
//                                    Log.e("WhichModel_Running", "new_default_exception");
                                    }else {
                                        if (assetAadharModelRef == null)
                                            loadAssetModel();
                                        try {
                                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                            Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                            runningAadharModelName = AadharModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_exception_null");
                                        } catch (Exception exception2) {
                                            clientAadharModel_Error=clientAadharModel_Error+exception2.getMessage();
                                        }
                                    }
                                } catch (Exception exception) {
                                    clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                                    if (assetAadharModelRef == null)
                                        loadAssetModel();
                                    try {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                        Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                        runningAadharModelName = AadharModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_exception");
                                    } catch (Exception exception2) {
                                        clientAadharModel_Error=clientAadharModel_Error+exception2.getMessage();
                                    }
                                }
                            }

                        } else if (loadedModelType.equalsIgnoreCase("default")) {
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultAadharImageWidth, defaultAadharImageHeight, docImageObject.angle);
                                Aadhar_encodings = defaultAadharModelRef.run(newbitmap, defaultoutputSize, defaultAadharImageWidth, defaultAadharImageHeight);
                                runningAadharModelName = defaultAadharModelName;
//                                Log.e("WhichModel_Running", "default");
                            } catch (Exception e) {
                                clientAadharModel_Error=clientAadharModel_Error+e.getMessage();
                                if (assetAadharModelRef == null)
                                    loadAssetModel();
                                try {
                                    Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                    Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                    runningAadharModelName = AadharModel.MODEL_PATH;
//                                    Log.e("WhichModel_Running", "default_asset");
                                } catch (Exception exception) {
                                    clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                                }
                            }

                        } else if (loadedModelType.equalsIgnoreCase("asset")) {
                            if (assetAadharModelRef == null)
                                loadAssetModel();
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                runningAadharModelName = AadharModel.MODEL_PATH;
//                                Log.e("WhichModel_Running", "asset");
                            } catch (Exception exception) {
                                clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                            }
                        } else {
                            if (assetAadharModelRef == null)
                                loadAssetModel();
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                                Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                                runningAadharModelName = AadharModel.MODEL_PATH;
//                                Log.e("WhichModel_Running", "asset_else");
                            } catch (Exception exception) {
                                clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                            }
                        }
                    } else {
                        if (assetAadharModelRef == null)
                            loadAssetModel();
                        try {
                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT, docImageObject.angle);
                            Aadhar_encodings = assetAadharModelRef.run(newbitmap, AadharModel.assetOutputSize, AadharModel.Asset_IMAGE_WIDTH, AadharModel.Asset_IMAGE_HEIGHT);
                            runningAadharModelName = AadharModel.MODEL_PATH;
//                            Log.e("WhichModel_Running", "asset");
                        } catch (Exception exception) {
                            clientAadharModel_Error=clientAadharModel_Error+exception.getMessage();
                        }
                    }
//                    long start = System.currentTimeMillis();
//                    Log.e("JukshioSukshiTimeDoc", String.valueOf(stop-start));
//                    Log.e("shankResult", String.valueOf(docResults));
                    if (firstDocFrame) {
                        firstDocFrame = false;
                    } else {
//                        Log.e("JukshioOBDFront", Arrays.toString(Aadhar_encodings));
                        String docResultsString = Arrays.toString(Aadhar_encodings).replace(" ", "");

                        if (frontOrNot) {
//                            Log.e("JukshioOBDFront_", docResultsString);
                            docFrontResultStringArray.add(docResultsString);
                        } else {
//                            Log.e("JukshioOBDBack_", docResultsString);
                            docBackResultStringArray.add(docResultsString);
                        }

                        docTotalNumFrames++;
                    }
                } else {
                    docBitmapQueue.clear();
                }
            }
            return null;
        }
    }

    /**
     * Returns image analysis results.
     **/
    public String getAadhaarIndex() {

        if (utils == null)
            utils = new LibUtils();


        if (docTotalNumFrames != 0) {

            newAadharModelDownloaded = preferences.getBoolean("newAadharModelDownloaded", false);
            if (newAadharModelDownloaded) {
                if (isNewModelSuccess) {
                    //replace all the required new model things to default model
                    //delete old default model and make new model as default
                    newAadharModelName = preferences.getString("newAadharModelName", "");
                    newAadharImageWidth = preferences.getInt("newAadharImageWidth", 224);
                    newAadharImageHeight = preferences.getInt("newAadharImageHeight", 224);
                    newAadharFrameThreshold = preferences.getInt("newAadharFrameThreshold", 15);
                    newAadharOutputSize = preferences.getInt("newAadharOutputSize", 2);

                    //deleting old default model from storage
                    String savedFileName = preferences.getString("defaultAadharModelName", "");
                    if (!savedFileName.isEmpty() && !savedFileName.equals(newAadharModelName)) {
                        File storedModel = utils.getAadharModelFile(context, savedFileName);
                        if (storedModel != null && storedModel.exists()) {
                            storedModel.delete();
                        }
                    }

                    editor.putBoolean("newAadharModelDownloaded", false).apply();
                    editor.putString("defaultAadharModelName", newAadharModelName).apply();
                    editor.putInt("defaultAadharImageWidth", newAadharImageWidth).apply();
                    editor.putInt("defaultAadharImageHeight", newAadharImageHeight).apply();
                    editor.putInt("defaultAadharOutpuSize", newAadharOutputSize).apply();
                    editor.putInt("defaultAadharFrameThreshold", newAadharFrameThreshold).apply();
                    editor.putBoolean("isDefaultAadharModelExist", true).apply();
                }
            }

            String result;
            if (frontOrNot) {
                result = docFrontResultStringArray.toString().replaceAll(" ", "");
            } else {
                result = docBackResultStringArray.toString().replaceAll(" ", "");
            }

            return result;
        } else {
            return "";
        }
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int width, int height, int angle) {

        Bitmap newbitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap newrotated = Bitmap.createBitmap(newbitmap2, 0, 0, newbitmap2.getWidth(), newbitmap2.getHeight(), matrix, true);
        return newrotated;
    }
}