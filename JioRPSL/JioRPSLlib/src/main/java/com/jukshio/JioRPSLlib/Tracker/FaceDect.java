package com.jukshio.JioRPSLlib.Tracker;

/**
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

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class FaceDect {

    private Context context;

    public static boolean straightFaceFound = false;
    public static boolean detectorAvailable = true;

    public static OnMultipleFacesDetectedListener onMultipleFacesDetectedListener;
    public static OnCaptureListener onCaptureListener;

    public static FaceDetector previewFaceDetector = null;
    private GraphicOverlay mGraphicOverlay;
    private FaceGraphic mFaceGraphic;

    public static double Rx_L = 0.7, Rx_U = 1.4, Ry_L = 0.8, Ry_U = 1.7;
    public static double slope_T = 18;

    /**
     * Interface callback on multiple faces detected with face count.
     * **/
    public interface OnMultipleFacesDetectedListener {
        void onMultipleFacesDetected(int n);
    }
    /**
     * Interface callback return captured image byte array and angle of orientation image.
     * **/
    public interface OnCaptureListener {
        void onCapture(byte[] data, int angle);
    }
    /**
     * Constructor initialise context and graphicoverlay objects and initialising multiple face interface and oncapture interface.
     *
     * **/
    public FaceDect(Context mcontext, GraphicOverlay graphicOverlay) {

        this.context = mcontext;
        mGraphicOverlay = graphicOverlay;

        initialisefaceDetec();
        this.onMultipleFacesDetectedListener = (OnMultipleFacesDetectedListener) context;
        this.onCaptureListener= (OnCaptureListener) context;
    }
    /**
     * This  method used to initialise FaceDetector and set it with mutliprocessor using its Builder class.
     * Multiprocessor is a class to handle high speed frames stream optimised to use multiple processors.
     * **/
    public void initialisefaceDetec() {

        previewFaceDetector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(false)
                .setTrackingEnabled(true)
                .build();

        if (previewFaceDetector.isOperational()) {
            //Log.e("Detector","found");
            detectorAvailable = true;
            previewFaceDetector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());
        } else {
            detectorAvailable = false;
            //Log.e("Detector","Not found");
        }
    }
    /**
     * This is class implementing mutilprocessorfactory.on create it then passes on newly create graphicfacetracker class.
     * **/
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {

        private GraphicOverlay mOverlay;
        /**
         * Constructor to initialise graphicovelay.
         * **/
        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, context);
        }
        /**
         * on new face detected this method is called to add new face item.
         * **/
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }
        /**
         * Ondetector detected is this method is called and is used to get landmarks.
         * Using landmarks to find the slope of two eye points and calculating the rx and ry.
         * FaceGraphic is then updated with face object.
         * **/
        @SuppressLint("ResourceAsColor")
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, final Face face) {

            List<Landmark> landmarks = face.getLandmarks();
            int landmarksCount = landmarks.size();
//            Log.e("landmarksCount", String.valueOf(landmarksCount));

            if (landmarksCount == 12) {
                float slope = (landmarks.get(1).getPosition().y - landmarks.get(0).getPosition().y) / (landmarks.get(1).getPosition().x - landmarks.get(0).getPosition().x);
                float Ry = (landmarks.get(3).getPosition().y - landmarks.get(2).getPosition().y) / (landmarks.get(2).getPosition().y - (landmarks.get(0).getPosition().y + landmarks.get(1).getPosition().y) / 2);
                float Rx = (landmarks.get(1).getPosition().x - landmarks.get(2).getPosition().x) / (landmarks.get(2).getPosition().x - landmarks.get(0).getPosition().x);

                //Log.e("JukshioSukshiAngle", String.valueOf(Math.atan(slope)*180/3.14));


//                Log.e("santhuValues",Rx_L+","+Rx_U+","+Ry_L+","+Ry_U+","+slope_T);
                /** ================================ params from BE =============================**/

                /*float Rx_L = 0, Rx_U = 0, Ry_L = 0, Ry_U = 0;
                double slope_T = 0;

                if (paramBE != null) {

                    if (paramBE.has("Rx_L")) {
                        try {
                            Rx_L = (float) paramBE.get("Rx_L");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Rx_L = 0.7f;
                    }
                    if (paramBE.has("Rx_H")) {
                        try {
                            Rx_U = (float) paramBE.get("Rx_U");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Rx_U = 1.4f;
                    }
                    if (paramBE.has("Ry_L")) {
                        try {
                            Ry_L = (float) paramBE.get("Ry_L");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Ry_L = 0.8f;
                    }
                    if (paramBE.has("Ry_H")) {
                        try {
                            Ry_U = (float) paramBE.get("Ry_U");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Ry_U = 1.7f;
                    }

                    if (paramBE.has("slope")) {
                        try {
                            slope_T = paramBE.getDouble("slope");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        slope_T = 1 / 18;
                    }
                }else {
                    Rx_L = 0.7f;
                    Rx_U = 1.4f;
                    Ry_L = 0.8f;
                    Ry_U = 1.7f;
                    slope_T = 1 / 18;
                }*/


                /**** ================================ params from BE =============================**/

//                double slopeThreshold = Math.tan(Math.PI/18);
                double slopeThreshold = Math.tan(Math.PI/slope_T);
                //Log.e("SRxRy", Rx + ", " + Ry + ", " + slope + ", " + slopeThreshold);


//                if(0.7 <= Ry && Ry <= 1.8 && 0.6 <= Rx && Rx <= 1.5 && ((-1 * slopeThreshold) < slope) && (slope < slopeThreshold)){
                if(Ry_L <= Ry && Ry <= Ry_U && Rx_L <= Rx && Rx <= Rx_U && ((-1 * slopeThreshold) < slope) && (slope < slopeThreshold)){
                    straightFaceFound = true;
                }else{
                    straightFaceFound = false;
                }
            }else if (landmarksCount == 8){
                float slope = (landmarks.get(1).getPosition().y - landmarks.get(0).getPosition().y) / (landmarks.get(1).getPosition().x - landmarks.get(0).getPosition().x);
                float Ry = (landmarks.get(7).getPosition().y - landmarks.get(2).getPosition().y) / (landmarks.get(2).getPosition().y - (landmarks.get(0).getPosition().y + landmarks.get(1).getPosition().y) / 2);
                float Rx = (landmarks.get(1).getPosition().x - landmarks.get(2).getPosition().x) / (landmarks.get(2).getPosition().x - landmarks.get(0).getPosition().x);

                double slopeThreshold = Math.tan(Math.PI/18);
                //Log.e("SRxRy", Rx + ", " + Ry + ", " + slope + ", " + slopeThreshold);

                if(0.8 <= Ry && Ry <= 1.7 && 0.7 <= Rx && Rx <= 1.4 && ((-1 * slopeThreshold) < slope) && (slope < slopeThreshold)){
                    straightFaceFound = true;
                }else{
                    straightFaceFound = false;
                }
            } else{
                straightFaceFound = false;
            }

            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }
        /**
         * On No face detected is method is called and previously inflated graphic overlay is removed.
         * **/
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

            mFaceGraphic.goneFace();
            mOverlay.remove(mFaceGraphic);
            mOverlay.clear();
        }
        /**
         * OnDone overlay is removed.
         * **/
        @Override
        public void onDone() {

            mFaceGraphic.goneFace();
            mOverlay.remove(mFaceGraphic);
            mOverlay.clear();
        }
    }
}