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

/**
 * This is a Singleton class used to hold callback interface on JukshioFaceActivity and JukshioDocActivity.As interface variables gets disposed finish method by java garbage collectors.
 * **/
public class LiveFaceAuthHolder {
    public static LiveFaceAuthHolder mInstance;
    FaceCaptureCompleteHandler onLiveFaceCapturedResultListener;
    DocCaptureCompleteHandler docCaptureCompleteHandler;

    public static LiveFaceAuthHolder getInstance() {
        if(mInstance == null) {
            mInstance = new LiveFaceAuthHolder();
        }
        return mInstance;
    }
    /**
     * This method holds on FaceCaptureCompleteHandler interface.
     */
    public void setLiveFaceAuthResultListener(FaceCaptureCompleteHandler onLiveFaceCapturedResultListener){
        this.onLiveFaceCapturedResultListener = onLiveFaceCapturedResultListener;
    }
    /**
     * This method gives back FaceCaptureCompleteHandler interface.
     */
    public FaceCaptureCompleteHandler  getLiveFaceAuthResultListener(){
        return  onLiveFaceCapturedResultListener;
    }
    /**
     * This method holds on DocCaptureResultListener interface.
     */
    public void setDocCaptureResultListener(DocCaptureCompleteHandler docCaptureResultListener){
        this.docCaptureCompleteHandler = docCaptureResultListener;
    }
    /**
     * This method gives back DocCaptureCompleteHandler interface.
     */
    public DocCaptureCompleteHandler getDocCaptureResultListener(){
        return  docCaptureCompleteHandler;
    }
}
