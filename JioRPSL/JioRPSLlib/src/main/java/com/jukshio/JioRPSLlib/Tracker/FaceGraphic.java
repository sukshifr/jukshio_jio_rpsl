package com.jukshio.JioRPSLlib.Tracker;

/**
 * Jukshio Corp CONFIDENTIAL
 * <p>
 * Jukshio Corp 2018
 * All Rights Reserved.
 * <p>
 * NOTICE:  All information contained herein is, and remains
 * the property of Jukshio Corp. The intellectual and technical concepts contained
 * herein are proprietary to Jukshio Corp
 * and are protected by trade secret or copyright law of U.S.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Jukshio Corp
 */
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.paramBE;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.jukshio.JioRPSLlib.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class uses GraphicOverlay.Graphic as parent class.This is used to draw face boxes on camera preview.
 **/
public class FaceGraphic extends GraphicOverlay.Graphic {

    private Resources resources;
    public static Paint mHintOutlinePaint;
    private volatile Face mFace;
    public static boolean faceIsInTheBox, docOrNot, faceRatioOk;
    public static JSONObject faceCoordinatesBackupInt;

    public FaceGraphic(GraphicOverlay overlay, Context context) {
        super(overlay);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = false;
        resources = context.getResources();
    }

    public void setId(int id) {
    }

    /**
     * This is to update faces by passing out Face object on detection mostly used on FaceDect class.
     **/
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * This to remove face graphic box on non-detection case.
     **/
    public void goneFace() {
        mFace = null;
    }

    //Draw a box around the face.
    float left = 0, right = 0, top = 0, bottom = 0;

    /**
     * This is called to draw face boxes when Face Object is provided.
     * Face Graphic Overlay is drawn using Face coordinates.Face Ratio  is calculated here.
     **/
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            return;
        }

        float centerX = translateX(face.getPosition().x + face.getWidth() / 2.0f);
        float centerY = translateY(face.getPosition().y + face.getHeight() / 2.0f);
        float offsetX = scaleX(face.getWidth() / 2.0f);
        float offsetY = scaleY(face.getHeight() / 2.0f);

        /*//Draw a box around the face.
        float left, right, top, bottom;*/

        if (docOrNot) {
            left = centerX - offsetX;
            right = centerX + offsetX;
            top = centerY - offsetY;
            bottom = centerY + offsetY;
        }


        float faceArea = (right - left) * (bottom - top);

        /**** ================================ params from BE =============================**/


        int face_ratio = 6;
        if (paramBE != null) {
            if (paramBE.has("face_ratio")) {
                try {
                    face_ratio = paramBE.getInt("face_ratio");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**** ================================ params from BE =============================**/

        //Log.e("JukshioSukshiFRatio", String.valueOf(faceRatio));
        //Log.e("JukshioSukshiFRatioOk", String.valueOf(faceRatioOk));

        if (mHintOutlinePaint != null) {

            if (docOrNot) {

                mHintOutlinePaint = new Paint();
                mHintOutlinePaint.setColor(resources.getColor(R.color.yellow_color));
                mHintOutlinePaint.setStyle(Paint.Style.STROKE);
                mHintOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.faceHintStroke));

                //Log.e("JukshioSukshiBottom", left + "," + right + "," + top + "," + bottom);
                canvas.drawRect(left, top, right, bottom, mHintOutlinePaint);

                /*float width = circleX * 2;
                float height = circleY * 2;

                float left1 = centerX - offsetX * 0.75f;
                float right1 = centerX + offsetX * 0.75f;
                float top1 = (centerY - offsetY * 0.75f) - (width*2/3) - (width/2.9f);
                float bottom1 = (centerY + offsetY * 0.75f) - ((width*2/3) - (width/2.9f));

                float left2 = left1 * 0.75f;
                float right2 = right1 * 1.25f;
                float top2 = top1 * 0.75f;
                float bottom2 = bottom1 * 1.25f;*/

                faceCoordinatesBackupInt = new JSONObject();
                try {
                    faceCoordinatesBackupInt.put("left", left);
                    faceCoordinatesBackupInt.put("right", right);
                    faceCoordinatesBackupInt.put("top", top);
                    faceCoordinatesBackupInt.put("bottom", bottom);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //canvas.drawRect(left, top, right, bottom, mHintOutlinePaint);



            }
        }
    }

    /**
     * Return a boolean to detect if face is in box or not.
     **/

    /**
     * Return a boolean to detect whelther point is in circle or not.This is calculated by finding distance from centre to given point and comparing with circle radius.
     **/


}