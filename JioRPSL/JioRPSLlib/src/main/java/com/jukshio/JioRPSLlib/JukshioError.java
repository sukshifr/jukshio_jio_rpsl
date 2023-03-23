package com.jukshio.JioRPSLlib;

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

/**
 * This is an Error data class intended to give error callbacks.This class is generic and used all over project.
 *
 * **/

public class JukshioError {

    public static final int RESPONSE_NULL = 11;
    public static final int NOCAMERAMODE = 12;
    public static final int NO_CAMERA_SOURCE = 13;
    public static final int REQUEST_TIME_OUT = 14;
    public static final int RESPONSE_JSON_E = 15;
    public static final int INTERNAL_SERVER_ERROR = 16;
    public static final int NO_INTERNET = 17;
    public static final int PROPER_AADHAAR_NOT_FOUND = 18;
    public static final int NO_WHITE_BACKGROUND  = 19;
    public static final int CLOSED_EYES = 20;
    public static final int NOT_LIVE = 21;
    public static final int BAD_REQUEST = 22;
    public static final int FACE_NOT_FOUND = 23;
    public static final int UN_AUTH = 24;
    public static final int NOT_BACK = 25;
    public static final int LOW_QUALITY_IMAGE = 26;
    public static final int NO_APP_ID = 27;
    public static final int USER_EXIT = 28;
    public static final int NO_DOC_TYPE =29;


    public int errorCode;
    public String errorMsg;

    public JukshioError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
