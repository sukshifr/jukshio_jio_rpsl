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

import org.json.JSONObject;
/**
 * This interface provides callbacks on face capture complete.
 * **/
public interface FaceCaptureCompleteHandler {
    void onResult(JukshioError error, JSONObject jsonFaceResult, JSONObject header);
}
