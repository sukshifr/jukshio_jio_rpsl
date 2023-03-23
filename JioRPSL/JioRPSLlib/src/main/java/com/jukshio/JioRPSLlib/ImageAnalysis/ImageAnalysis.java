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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class used for imageAnalysis using tensorflow lite.
 **/

public class ImageAnalysis {

    public AssetManager assetManager;
    public Context context;

    public Queue<ImageObject> bitmapQueue = new LinkedList<>();
    public ArrayList<String> resultStringArray = new ArrayList<>();
    public boolean okForTask, firstFrame;
    public static boolean okForAnalysis, readyForAnalysis = false, canAddData = true;
    public int totalNumFrames;
    public Bitmap realImage, fakeImage;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LibUtils utils;

    //    public static Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    public static FaceModel newFaceModelRef, defaultFaceModelRef, assetFaceModelRef;
    public static String useModelforFace = "";
    public static String defaultFaceModelName = "", newFaceModelName = "";
    public static boolean newFaceModelDownloaded = false;
    public static File newFaceModelFile, defaultFaceModelFile;
    public static String loadedModelType = "", runningFaceModelName = "",clientFaceModel_Error="";

    //width and height
    public static int defaultFaceImageWidth, defaultFaceImageHeight, newFaceImageWidth, newFaceImageHeight, newFaceFrameThreshold, defaultFaceFrameThreshold, newFaceOutputSize;

    public static boolean isNewModelSuccess = false, isNewFaceModelFileExist = false, isDefaultFaceModelFileExist = false;
    public boolean useAssetModel_Face = false;

    /**
     * Constructor to initialise assetmanager and context.
     **/
    public ImageAnalysis(AssetManager assetManager, Context context) {
        this.assetManager = assetManager;
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        utils = new LibUtils();
    }

    /**
     * This is a method initialise tensflow classifier with model stored in the assets.
     **/
    public void initImageAnalysis() {


//        useModelforFace = preferences.getString("useModelforFace", "default");
        newFaceModelDownloaded = preferences.getBoolean("newFaceModelDownloaded", false);
        useAssetModel_Face = preferences.getBoolean("useAssetModel_Face", false);
        if (!useAssetModel_Face) {
            if (newFaceModelDownloaded) {
                loadNewModel();
//            loadDefaultModel();
            } else {
                loadDefaultModel();
            }
        } else {
            loadAssetModel();
        }

    }

    private void loadNewModel() {
        if (utils == null)
            utils = new LibUtils();
        try {
            newFaceModelName = preferences.getString("newFaceModelName", "");
//            Log.e("modelName", newFaceModelName);
            if (newFaceModelName != null && !newFaceModelName.isEmpty()) {
                newFaceModelFile = utils.getFaceModelFile(context, newFaceModelName);
                isNewFaceModelFileExist = newFaceModelFile.exists();
            } else {
                isNewFaceModelFileExist = false;
            }
            newFaceImageWidth = preferences.getInt("newFaceImageWidth", 224);
            newFaceImageHeight = preferences.getInt("newFaceImageHeight", 224);
            newFaceFrameThreshold = preferences.getInt("newFaceFrameThreshold", 15);
            if (isNewFaceModelFileExist) {
                try {
                    newFaceModelRef = new FaceModel(newFaceModelFile, newFaceImageWidth, newFaceImageHeight);
                    editor.putString("loadedFaceModelType", "new").apply();
                } catch (IOException e) {
                    e.printStackTrace();
                    clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
//                    Log.e("Exception_Model", e.getMessage());
                    loadDefaultModel();
                } catch (Exception e) {
                    e.printStackTrace();
                    clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
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

    private void loadDefaultModel() {
        if (utils == null)
            utils = new LibUtils();
        try {
            defaultFaceModelName = preferences.getString("defaultFaceModelName", "");
            if (defaultFaceModelName != null && !defaultFaceModelName.isEmpty()) {
                defaultFaceModelFile = utils.getFaceModelFile(context, defaultFaceModelName);
                isDefaultFaceModelFileExist = defaultFaceModelFile.exists();
            } else {
                isDefaultFaceModelFileExist = false;
            }
            defaultFaceImageWidth = preferences.getInt("defaultFaceImageWidth", 224);
            defaultFaceImageHeight = preferences.getInt("defaultFaceImageHeight", 224);
            defaultFaceFrameThreshold = preferences.getInt("defaultFaceFrameThreshold", 15);
            if (isDefaultFaceModelFileExist) {
                try {
                    defaultFaceModelRef = new FaceModel(defaultFaceModelFile, defaultFaceImageWidth, defaultFaceImageHeight);
                    editor.putString("loadedFaceModelType", "default").apply();
                } catch (IOException e) {
                    e.printStackTrace();
                    clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
//                    Log.e("Exception_Model", e.getMessage());
                    loadAssetModel();
                } catch (Exception e) {
                    e.printStackTrace();
                    clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
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

    private void loadAssetModel() {

        try {
            assetFaceModelRef = new FaceModel(assetManager);
            editor.putString("loadedFaceModelType", "asset").apply();
        } catch (IOException e) {
            e.printStackTrace();
            clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
        }
    }

    /**
     * This is reset initial values of different variables.
     **/
    public void setupImageAnalysis() {
        totalNumFrames = 0;
        realImage = null;
        fakeImage = null;
        okForTask = true;
        firstFrame = true;
        okForAnalysis = true;
        canAddData = true;
        resultStringArray.clear();
    }

    public void startImageAnalysis() {

        new MyAsyncTask().execute();
    }

    /**
     * Method to start image analysis using async task and passing each image object in bitmapQueue obtain on camera frames.
     * Each ImageObject object is compress to create bitmap with byte array as parameter and then scaled down to 224*224 and then processed on tensorflow classifier.
     **/
    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends android.os.AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            Iterator<ImageObject> iterator = bitmapQueue.iterator();

            while (iterator.hasNext()) {

                if (okForAnalysis) {
                    long startTime=System.currentTimeMillis();
                    ImageObject currentImageObject = bitmapQueue.remove();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    YuvImage image = new YuvImage(currentImageObject.byteArray, currentImageObject.parameters.getPreviewFormat(),
                            currentImageObject.previewW, currentImageObject.previewH, null);
                    int quality = 100;   // adjust this as needed

                    image.compressToJpeg(new Rect(0, 0, currentImageObject.previewW, currentImageObject.previewH), quality, out);
                    byte[] finalByte = out.toByteArray();
                    Bitmap finalBitmap = BitmapFactory.decodeByteArray(finalByte, 0, finalByte.length);


                    float[] face_encodings = new float[0];
                    loadedModelType = preferences.getString("loadedFaceModelType", "asset");
                    int newoutputSize = preferences.getInt("newFaceOutputSize", 1);
                    int defaultoutputSize = preferences.getInt("defaultFaceOutpuSize", 1);
                    if (!useAssetModel_Face) {
                        if (loadedModelType.equalsIgnoreCase("new")) {
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, newFaceImageWidth, newFaceImageHeight, currentImageObject.angle);
                                face_encodings = newFaceModelRef.run(newbitmap, newoutputSize, newFaceImageWidth, newFaceImageHeight);
                                isNewModelSuccess = true;
                                runningFaceModelName = newFaceModelName;
//                                Log.e("WhichModel_Running", "new");
                            } catch (ArrayIndexOutOfBoundsException arrayE) {
                                clientFaceModel_Error=clientFaceModel_Error+arrayE.getMessage();
                                isNewModelSuccess = false;
                                try {
                                    loadDefaultModel();
                                    if (defaultFaceModelRef!=null) {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultFaceImageWidth, defaultFaceImageHeight, currentImageObject.angle);
                                        face_encodings = defaultFaceModelRef.run(newbitmap, defaultoutputSize, defaultFaceImageWidth, defaultFaceImageHeight);
                                        runningFaceModelName = defaultFaceModelName;
//                                    Log.e("WhichModel_Running", "new_default");
                                    }else {
                                        if (assetFaceModelRef == null)
                                            loadAssetModel();
                                        try {
                                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                            face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                            runningFaceModelName = FaceModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_null");
                                        } catch (Exception exception) {
                                            clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                                        }
                                    }
                                } catch (Exception e) {
                                    clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
                                    if (assetFaceModelRef == null)
                                        loadAssetModel();
                                    try {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                        face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                        runningFaceModelName = FaceModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset");
                                    } catch (Exception exception) {
                                        clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                                    }

                                }
                            } catch (Exception e) {
                                clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
                                isNewModelSuccess = false;
                                try {
                                    loadDefaultModel();
                                    if (defaultFaceModelRef!=null) {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultFaceImageWidth, defaultFaceImageHeight, currentImageObject.angle);
                                        face_encodings = defaultFaceModelRef.run(newbitmap, defaultoutputSize, defaultFaceImageWidth, defaultFaceImageHeight);
                                        runningFaceModelName = defaultFaceModelName;
//                                    Log.e("WhichModel_Running", "new_default_exception");
                                    }else {
                                        if (assetFaceModelRef == null)
                                            loadAssetModel();
                                        try {
                                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                            face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                            runningFaceModelName = FaceModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_exception_null");
                                        } catch (Exception exception2) {
                                            clientFaceModel_Error=clientFaceModel_Error+exception2.getMessage();
                                        }
                                    }
                                } catch (Exception exception) {
                                    clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                                    if (assetFaceModelRef == null)
                                        loadAssetModel();
                                    try {
                                        Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                        face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                        runningFaceModelName = FaceModel.MODEL_PATH;
//                                        Log.e("WhichModel_Running", "new_asset_exception");
                                    } catch (Exception exception2) {
                                        clientFaceModel_Error=clientFaceModel_Error+exception2.getMessage();
                                    }
                                }
                            }

                        } else if (loadedModelType.equalsIgnoreCase("default")) {
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, defaultFaceImageWidth, defaultFaceImageHeight, currentImageObject.angle);
                                face_encodings = defaultFaceModelRef.run(newbitmap, defaultoutputSize, defaultFaceImageWidth, defaultFaceImageHeight);
                                runningFaceModelName = defaultFaceModelName;
//                                Log.e("WhichModel_Running", "default");
                            } catch (Exception e) {
                                clientFaceModel_Error=clientFaceModel_Error+e.getMessage();
                                if (assetFaceModelRef == null)
                                    loadAssetModel();
                                try {
                                    Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                    face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                    runningFaceModelName = FaceModel.MODEL_PATH;
//                                    Log.e("WhichModel_Running", "default_asset");
                                } catch (Exception exception) {
                                    clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                                }
                            }

                        } else if (loadedModelType.equalsIgnoreCase("asset")) {
                            if (assetFaceModelRef == null)
                                loadAssetModel();
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                runningFaceModelName = FaceModel.MODEL_PATH;
//                                Log.e("WhichModel_Running", "asset");
                            } catch (Exception exception) {
                                clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                            }
                        } else {
                            if (assetFaceModelRef == null)
                                loadAssetModel();
                            try {
                                Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                                face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                                runningFaceModelName = FaceModel.MODEL_PATH;
//                                Log.e("WhichModel_Running", "asset_else");
                            } catch (Exception exception) {
                                clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                            }
                        }
                    } else {
                        if (assetFaceModelRef == null)
                            loadAssetModel();
                        try {
                            Bitmap newbitmap = getRotatedBitmap(finalBitmap, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT, currentImageObject.angle);
                            face_encodings = assetFaceModelRef.run(newbitmap, FaceModel.assetOutputSize, FaceModel.Asset_IMAGE_WIDTH, FaceModel.Asset_IMAGE_HEIGHT);
                            runningFaceModelName = FaceModel.MODEL_PATH;
//                            Log.e("WhichModel_Running", "asset");
                        } catch (Exception exception) {
                            clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                        }
                    }
                    if (firstFrame) {
                        firstFrame = false;
                    } else {

                        if (canAddData) {
                            resultStringArray.add(Arrays.toString(face_encodings));
                        }
                        totalNumFrames++;
                    }
//                    Log.e("ModelRunninTime",System.currentTimeMillis()-startTime+"");
                } else {
                    bitmapQueue.clear();
                }
            }
            return null;
        }
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int width, int height, int angle) {

        Bitmap newbitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap newrotated = Bitmap.createBitmap(newbitmap2, 0, 0, newbitmap2.getWidth(), newbitmap2.getHeight(), matrix, true);
        return newrotated;
    }

    /**
     * Returns image analysis results.
     **/
    public String getImageIndex() {

        if (utils == null)
            utils = new LibUtils();


        if (totalNumFrames != 0) {

            newFaceModelDownloaded = preferences.getBoolean("newFaceModelDownloaded", false);
            if (newFaceModelDownloaded) {
                if (isNewModelSuccess) {
                    //replace all the required new model things to default model
                    //delete old default model and make new model as default
                    newFaceModelName = preferences.getString("newFaceModelName", "");
                    newFaceImageWidth = preferences.getInt("newFaceImageWidth", 224);
                    newFaceImageHeight = preferences.getInt("newFaceImageHeight", 224);
                    newFaceFrameThreshold = preferences.getInt("newFaceFrameThreshold", 15);
                    newFaceOutputSize = preferences.getInt("newFaceOutputSize", 1);

                    //deleting old default model from storage
                    String savedFileName = preferences.getString("defaultFaceModelName", "");
                    if (!savedFileName.isEmpty() && !savedFileName.equals(newFaceModelName)) {
                        File storedModel = utils.getFaceModelFile(context, savedFileName);
                        if (storedModel != null && storedModel.exists()) {
                            storedModel.delete();
                        }
                    }

                    editor.putBoolean("newFaceModelDownloaded", false).apply();
                    editor.putString("defaultFaceModelName", newFaceModelName).apply();
                    editor.putInt("defaultFaceImageWidth", newFaceImageWidth).apply();
                    editor.putInt("defaultFaceImageHeight", newFaceImageHeight).apply();
                    editor.putInt("defaultFaceOutpuSize", newFaceOutputSize).apply();
                    editor.putInt("defaultFaceFrameThreshold", newFaceFrameThreshold).apply();
                    editor.putBoolean("isDefaultFaceModelExist", true).apply();
                }
            }

            String result = resultStringArray.toString().replaceAll(" ", "");
            return result;
        } else {
            return "";
        }
    }
}