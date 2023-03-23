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

import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.isBack;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrBackBlock;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrBackPlots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RearAadhar {

    public Context context;
    int checkBack =0;
    public JSONArray ocrBackBlockArray = new JSONArray();
    String[] states = new String[]{"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar", "Chandigarh", "Dadra and Nagar Haveli", "Daman and Diu", "Lakshadweep", "Delhi", "Puducherry"};

    public RearAadhar(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    public JSONObject inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        JSONObject details = new JSONObject();
        try {
            if (!textRecognizer.isOperational()) {
                return null;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);

            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            for (int b = 0; b < textBlocks.size(); b++){
                TextBlock textBlock = textBlocks.get(b);
                if (textBlock != null && textBlock.getValue() != null) {
                    if (b!=0)ocrBackBlock += ", ";
                    for (Text line: textBlock.getComponents()){
                        String currentLine = line.getValue();
                        ocrBackBlock += line.getValue();

                        JSONObject currentLineObject = new JSONObject();
                        currentLineObject.put("value", currentLine);
                        currentLineObject.put("left", line.getBoundingBox().left);
                        currentLineObject.put("right", line.getBoundingBox().right);
                        currentLineObject.put("top", line.getBoundingBox().top);
                        currentLineObject.put("bottom", line.getBoundingBox().bottom);

                        ocrBackBlockArray.put(currentLineObject);
                    }
                    if (isABackBlock(textBlock)){
                        checkBack++;
                    }
                }
            }
            ocrBackPlots = ocrBackBlockArray.toString();
            //Log.e("shankOCRBackObject", ocrBackPlots);

            isBack = checkBack > 0;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", ocrBackBlock);
            //Log.e("ocrBackBlock", ocrBackBlock);
            jsonObject.put("conf", 0);
            details.put("address", jsonObject);

            for (int b = 0; b < textBlocks.size(); b++){
                TextBlock textBlock = textBlocks.get(b);
                if (textBlock != null && textBlock.getValue() != null) {

                    if (isAddressBlock(textBlock) != null && isAddressBlock(textBlock) != ""){

                        String addressString = isAddressBlock(textBlock);
                        String ss1 = addressString.replace("Address: ", "");
                        String ss2 = ss1.replace("Address:", "");
                        String ss = ss2.replace("Address", "");
                        //ocrBackBlock = ss;
                        //isBack = hasCommas(",", ss) || checkBack > 0;
                        pincode = getPin(ss, 6);
                        /*if (!pincode.equals("")){
                            checkBack++;
                        }*/

                        String state = getState(ss);
                        /*if (!state.equals("")){
                            checkBack++;
                        }*/
                        String[] things = ss.split(",");
                        String y = ss.replaceAll(" ", "");
                        String z = ss.replaceAll("[-?:?.]+", "");
                        if (y != null && y != "" && z.length() > 20){
                            return getAddress(things);
                        }else{

                            for(int c = 0; c < textBlocks.size(); c++) {
                                TextBlock textBlock1 = textBlocks.get(c);

                                if (textBlock1 != null && textBlock1.getValue() != null) {
                                    if (totalBlock(textBlock1) != null && totalBlock(textBlock1) != "") {

                                        String addressString1 = totalBlock(textBlock1);
                                        //ocrBackBlock = addressString1;
                                        //isBack = hasCommas(",", addressString);
                                        pincode = getPin(addressString1, 6);
                                        /*if (!pincode.equals("")){
                                            checkBack++;
                                        }*/
                                        String state1 = getState(addressString1);
                                        /*if (!state1.equals("")){
                                            checkBack++;
                                        }*/

                                        if (addressString1.length() > 20){
                                            String[] things1 = addressString1.split(",");
                                            String y1 = addressString1.replaceAll(" ", "");
                                            String z1 = addressString1.replaceAll("[-?:?.]+", "");
                                            if (y1 != null && y1 != "" && z1.length() > 20) {
                                                return getAddress(things1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            textRecognizer.release();
        }

        return details;
    }

    String pincode = "";

    public boolean hasCommas(String pattern, String block){

        int M = pattern.length();
        int N = block.length();
        int res = 0;

        /* A loop to slide pat[] one by one */
        for (int i = 0; i <= N - M; i++) {

            /* For current index i, check for pattern match */
            int j;
            for (j = 0; j < M; j++) {
                if (block.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }

            // if pat[0...M-1] = txt[i, i+1, ...i+M-1]
            if (j == M) {
                res++;
            }
        }
        if (res >= 4){
            return true;
        }
        return false;
    }

    public JSONObject getAddress(String[] things){

        String pin = "", state = "", address = "";
        int n = things.length;

        if (n <= 3){
            for (int i = 0; i<n-1; i++){
                address = things[i] + ", ";
            }
            address += things[n-1];
        }else {
            String lastString = things[n-1];

            if (lastString.contains(",")){
                String[] lastTwo = lastString.split(",");

                if (lastTwo.length == 2){
                    state = lastTwo[0];
                    pin = lastTwo[1];
                }else state = lastString;

                for (int i = 0; i<n-2; i++){
                    address = things[i] + ", ";
                }
                address += things[n-2];


            }else if(lastString.contains(".")){
                String[] lastTwo = lastString.split(".");

                if (lastTwo.length == 2){
                    state = lastTwo[0];
                    pin = lastTwo[1];
                }else state = lastString;

                for (int i = 0; i<n-2; i++){
                    address += things[i] + ", ";
                }
                address += things[n-2];

            }else if (lastString.contains("-")){
                String[] lastTwo = lastString.split("-");

                if (lastTwo.length == 2){
                    state = lastTwo[0];
                    pin = lastTwo[1];
                }else state = lastString;

                for (int i = 0; i<n-2; i++){
                    address += things[i] + ", ";
                }
                address += things[n-2];

            }else{
                if (isPin(lastString)){
                    state = things[n-2];
                    pin = lastString;

                    for (int i = 0; i<n-3; i++){
                        address += things[i] + ", ";
                    }
                    address += things[n-3];
                }else{
                    pin = "";
                    state = lastString;

                    for (int i = 0; i<n-2; i++){
                        address += things[i] + ", ";
                    }
                    address += things[n-2];
                }
            }
        }

        JSONObject details = new JSONObject();
        try {
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            JSONObject jsonObject3 = new JSONObject();

            jsonObject1.put("value", address);
            jsonObject1.put("conf", 0);
            details.put("address", jsonObject1);
            if (!state.equals("")){
                jsonObject2.put("value", state);
                jsonObject2.put("conf", 0);
                details.put("state", jsonObject2);
            }
            if (!pin.equals("")){
                String pinNoSpace = pin.replaceAll(" ", "");
                jsonObject3.put("value", pinNoSpace);
                jsonObject3.put("conf", 0);
                details.put("pin", jsonObject3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    public String getCurrectName(String name){
        String a = name;
        if (name.contains("S/O")){
            a = "S/O " + name.replace("S/O", "");
        }else if (name.contains("W/O")){
            a = "W/O " + name.replace("W/O", "");
        }else if (name.contains("D/O")){
            a = "D/O " + name.replace("D/O", "");
        }
        String s = a.replaceAll("[-?:?.?0-9]+", "");
        return s;
    }

    public boolean isABackBlock(TextBlock textBlock){

        List<Text> lines = (List<Text>) textBlock.getComponents();
        for (Text line: lines){
            if (isABackLine(line.getValue())){
                return true;
            }
        }
        return false;
    }

    public boolean isABackLine(String line){

        line = line.toLowerCase();
        if (line.contains("unique") || line.contains("identification") || line.contains("authority") || line.contains("uidai") || line.contains("gov.in")){
            return true;
        }else return false;
    }

    public String isAddressBlock(TextBlock textBlock){

        String addressString = "";

        List<Text> lines = (List<Text>) textBlock.getComponents();
        for(Text line: lines) {
            if (line.getValue().contains("Address")) {
                for (Text line1 : lines) {
                    if (thisLineIsOk(line1.getValue())){
                        addressString += line1.getValue();
                    }
                }
                return addressString;
            }
        }
        return  "";
    }

    public boolean thisLineIsOk(String currentLine){
        if (currentLine.contains("UE")||currentLine.contains("IDENT")||currentLine.contains("ident")||currentLine.contains("AUTH")||currentLine.contains("Auth")||currentLine.contains("INDIA")||currentLine.contains("India")||currentLine.contains("NDIA")||currentLine.contains("ndia")){
            return false;
        }
        else if (currentLine.contains("gov")||currentLine.contains("uldal")||currentLine.contains("uidai")||currentLine.contains("help")||currentLine.contains(".in")||currentLine.contains("1947")){
            return false;
        }
        return true;
    }

    public String totalBlock(TextBlock textBlock){

        String addressString = "";

        List<Text> lines = (List<Text>) textBlock.getComponents();
        for (Text line1 : lines) {
            addressString += line1.getValue();
        }
        return addressString;
    }

    public boolean isPin(String string){
        String string0 = string.replaceAll(" ", "");
        String string1 = string0.replaceAll("[a-z]+", "");
        if ((string0.length() == string1.length()) && string1.length() == 6){
            return true;
        }
        return false;
    }

    public static String getPin(String str, int num){
        String arr[] = str.split("[0-9]");
        for(String s:arr)
            if(s.length() == num){
                return s;
            }else return "";

        return "";
    }

    public String getState(String s){

        String[] items = s.split(",");
        //iterate the String array
        for (String state : states) {
            for (String item : items) {
                if (state.equals(item)) {
                    return item;
                }else{
                    return "";
                }
            }
        }
        return "";
    }
}