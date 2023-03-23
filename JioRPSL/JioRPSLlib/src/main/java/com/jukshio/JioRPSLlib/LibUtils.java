package com.jukshio.JioRPSLlib;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;

/**
 * LibUtils class is a utilise class mostly used for bitmap crop and resize operations.
 **/
public class LibUtils {
    /**
     * getDocBitmap is to crop  original bitmap to aadhar aspect ratio.
     **/
    public Bitmap getDocBitmap(Bitmap bitmap, double padding) {
        int originalImageWidth0 = bitmap.getWidth();
        int originalImageNeeded = (int) (bitmap.getWidth() * (1 - 2 * padding));

        return Bitmap.createBitmap(bitmap, (originalImageWidth0 - originalImageNeeded) / 2, (int) ((originalImageWidth0 * 2 / 3) - (originalImageNeeded / 2.9f)), originalImageNeeded, (int) (originalImageNeeded / 1.45f));
    }

    public Bitmap getPassportDocBitmap(Bitmap bitmap, double padding) {
        int originalImageWidth0 = bitmap.getWidth();
        int originalImageNeeded = (int) (bitmap.getWidth() * (1 - 2 * padding));

        return Bitmap.createBitmap(bitmap, (originalImageWidth0 - originalImageNeeded) / 2, (int) ((originalImageWidth0 * 2 / 3) - (originalImageNeeded / 2.98f)), originalImageNeeded, (int) (originalImageNeeded / 1.49f));

    }

    public Bitmap getVoterDocBitmap(Bitmap bitmap, double padding) {
        int originalImageWidth0 = bitmap.getWidth();
        int originalImageNeeded = (int) (bitmap.getWidth() * (1 - 2 * padding));

        return Bitmap.createBitmap(bitmap, (originalImageWidth0 - originalImageNeeded) / 2, (int) ((originalImageWidth0 * 2 / 3) - (originalImageNeeded / 1.68f)), originalImageNeeded, (int) (originalImageNeeded / 0.76f));

//        return Bitmap.createBitmap(bitmap, (originalImageWidth0 - originalImageNeeded)/2, (int) ((originalImageWidth0 * 2/3) - (originalImageNeeded / 1.68f)), originalImageNeeded, (int) (originalImageNeeded/0.71f));

    }

    public Bitmap getEAadhaarBotomBitmap(Bitmap bitmap, double padding) {
        int originalImageWidth0 = bitmap.getWidth();
        int originalImageNeeded = (int) (bitmap.getWidth() * (1 - 2 * padding));

        return Bitmap.createBitmap(bitmap, (int) ((originalImageWidth0 * 2 / 3 - originalImageNeeded / 2.5f)), (int) ((originalImageWidth0 * 2 / 3) - (originalImageNeeded / 1.68f)), (int) (originalImageNeeded / 1.98f), (int) (originalImageNeeded / 0.76f));
//       ((width *2/3) - (widthPreview /2f), (width * 2 / 3) - (widthPreview / 1.68f), (width * 2/3) + (widthPreview/6)  , (width * 2 / 3) + (widthPreview / 1.42f));
//       ((width *2/3) - (widthPreview/ 2.5f), (width *2/3) - (widthPreview / 1.68f), (width *2/3) + (widthPreview/ 12.5f), (width * 2/3) + (widthPreview / 1.42f));

    }


    /**
     * getHalfBitmap is to crop document bitmap to half and 10 % from top and 10 % bottom.
     **/

    public Bitmap getHalfBitmap(Bitmap bitmap) {
        int originalImageHeight = bitmap.getHeight();
        int originalImageWidth = bitmap.getWidth();
        return Bitmap.createBitmap(bitmap, 0, originalImageHeight / 10, originalImageWidth / 2, originalImageHeight * 17 / 20);
    }

    /**
     * getSquareBitmap is to crop original face bitmap to square.
     **/
    public Bitmap getSquareBitmap(Bitmap bitmap) {
        int originalImageHeight = bitmap.getHeight();
        int originalImageWidth = bitmap.getWidth();
        return Bitmap.createBitmap(bitmap, originalImageWidth / 10, (originalImageHeight / 2 - 2 * originalImageWidth / 5), 4 * originalImageWidth / 5, 4 * originalImageWidth / 5);
    }

    /**
     * getSquareCompressedBitmap is to compress to face square cropped bitmap which is obtain using landmarks.This limit square bitmap to be 450 of length.
     **/
    public Bitmap getSquareCompressedBitmap(Bitmap bitmap) {
        int originalImageHeight = bitmap.getHeight();
        int originalImageWidth = bitmap.getWidth();
        int finalImageWidth = 450;
        int finalImageHeight = 450 * originalImageHeight / originalImageWidth;
        return Bitmap.createScaledBitmap(bitmap, finalImageWidth, finalImageHeight, false);
    }

    /**
     * get224Bitmap is to crop original face bitmap to 224 * 224.
     **/
    public Bitmap get224Bitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 224, 224, false);
    }

    /**
     * getOutputMediaFile returns file from internal files directory with child as DKYC_Libr
     **/
    public File getOutputMediaFile(Context context, String type) {
        final String TAG = "sukshiJukshio";

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DKYC_Libr");
        File mediaStorageDir = new File(context.getFilesDir(), "POC_Libr");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        long time = System.currentTimeMillis();
        File file = new File(mediaStorageDir.getPath() + File.separator + "POC_" + type + "_" + time + ".jpg");
        return file;
    }

    public File getAadharModelFile(Context context, String name) {
        final String TAG = "sukshiJukshio";

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DKYC_Libr");
        File mediaStorageDir = new File(context.getFilesDir(), "POC_Model_Aadhar");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        long time = System.currentTimeMillis();
        File file = new File(mediaStorageDir.getPath() + File.separator + name);
        return file;
    }

    public File getFaceModelFile(Context context, String name) {
        final String TAG = "sukshiJukshio";

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DKYC_Model_Face");
        File mediaStorageDir = new File(context.getFilesDir(), "POC_Model_Face");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        long time = System.currentTimeMillis();
        File file = new File(mediaStorageDir.getPath() + File.separator + name);
        return file;
    }

}