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

import android.hardware.Camera;
/**
 * This is data or POJO class used hold byte array and image dimensions like height,width and angle.
 * **/
public class ImageObject {

    public byte[] byteArray;
    public int previewW, previewH, angle;
    public Camera.Parameters parameters;

    public ImageObject(byte[] byteArray, int previewW, int previewH, Camera.Parameters parameters, int angle) {
        this.byteArray = byteArray;
        this.previewW = previewW;
        this.previewH = previewH;
        this.parameters = parameters;
        this.angle = angle;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public int getPreviewW() {
        return previewW;
    }

    public int getPreviewH() {
        return previewH;
    }

    public Camera.Parameters getParameters() {
        return parameters;
    }

    public int getAngle() {
        return angle;
    }
}
