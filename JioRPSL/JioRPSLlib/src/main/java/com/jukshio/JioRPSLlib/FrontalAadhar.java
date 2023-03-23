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

import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.isFront;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrFrontBlock;
import static com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper.ocrFrontPlots;

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

public class FrontalAadhar {

    public Context context;
    public int blockNumber = 0, genderIndex = 0, aadharIndex = 0;
    public String aadhar, name, year, date;
    public JSONObject ocrData = new JSONObject();
    public ArrayList<String> ocrFront = new ArrayList<>();
    public JSONArray ocrFrontBlockArray = new JSONArray();
    boolean foundAadhar = false, searchForName = true, searchForFather = true, searchForAadhar = true, searchForGender = true, searchForDOB = true, searchForYOB = true, noSpacesInNumber = false, otherKeywordsFound = false;
    boolean headerFound = false;
    public FrontalAadhar(Context context) {
        this.context = context;
    }

    public JSONObject inspectFromBitmap(Bitmap bitmap) {

        /*bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.masked_aadhaar);*/

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
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

            //for (TextBlock textBlock : textBlocks) {
            for (int j=0; j<textBlocks.size(); j++) {
                TextBlock currentBlock = textBlocks.get(j);
                if (currentBlock != null && currentBlock.getValue() != null) {
                    blockNumber++;

                    List<Text> lines = (List<Text>) currentBlock.getComponents();

                    //for(Text line: lines){
                    for(int i=0; i<lines.size();){
                        String currentLine = lines.get(i).getValue();
                        ocrFront.add(currentLine);

                        JSONObject currentLineObject = new JSONObject();
                        currentLineObject.put("value", currentLine);
                        currentLineObject.put("left", lines.get(i).getBoundingBox().left);
                        currentLineObject.put("right", lines.get(i).getBoundingBox().right);
                        currentLineObject.put("top", lines.get(i).getBoundingBox().top);
                        currentLineObject.put("bottom", lines.get(i).getBoundingBox().bottom);

                        ocrFrontBlockArray.put(currentLineObject);

                        if (isHeader(currentLine)){
                            //Log.e("headerIndex", String.valueOf(j));
                            headerFound = true;
                            //address.put("Header", line.getValue());
                        }else if (searchForFather && isFather(currentLine)){
                            //Log.e("fatherIndex", String.valueOf(j));

                            searchForFather = false;
                            String fatherName;
                            if (currentLine.contains(":")){
                                fatherName = splitString(currentLine, ": ");
                            }else {
                                if (currentLine.length()>8){
                                    fatherName = currentLine.substring(8);
                                }else {
                                    fatherName = currentLine;
                                }
                            }

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", fatherName);
                            ocrData.put("father", jsonObject);

                        }else if (searchForDOB && isDOB(currentLine)){
                            //Log.e("dobIndex", String.valueOf(j));

                            searchForDOB = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", date);
                            ocrData.put("dob", jsonObject);

                        }else if (searchForYOB && isYOB(currentLine)){
                            //Log.e("yobIndex", String.valueOf(j));

                            searchForYOB = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", year);
                            ocrData.put("yob", jsonObject);

                        }else if (searchForGender && currentLine.toLowerCase().contains("male") && !isFemale(currentLine)){
                            //Log.e("maleIndex", String.valueOf(j));
                            genderIndex = j;

                            searchForGender = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", "Male");
                            ocrData.put("gender", jsonObject);

                        }else if (searchForGender && isFemale(currentLine)){
                            //Log.e("femaleIndex", String.valueOf(j));
                            genderIndex = j;

                            searchForGender = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", "Female");
                            ocrData.put("gender", jsonObject);

                        }else if (searchForGender && isTransG(currentLine)){
                            //Log.e("transIndex", String.valueOf(j));
                            genderIndex = j;

                            searchForGender = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", "Transgender");
                            ocrData.put("gender", jsonObject);

                        }else if (searchForAadhar && isAadhar(currentLine)){
                            //Log.e("aadharIndex", String.valueOf(j));
                            aadharIndex = j;

                            searchForAadhar = false;
                            foundAadhar = true;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", aadhar);
                            ocrData.put("aadhaar", jsonObject);

                        }else if (searchForName && isName(currentLine) && !isHeader(currentLine) && !otherKeywordFound(currentLine) && j != 0){
                            //Log.e("nameIndex", String.valueOf(j));

                            searchForName = false;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("conf", 0);
                            jsonObject.put("value", name);
                            ocrData.put("name", jsonObject);
                        }else {
                            otherKeywordFound(currentLine);
                        }

                        i++;
                    }
                }
            }
            ocrFrontBlock = String.valueOf(ocrFront);
            ocrFrontPlots = ocrFrontBlockArray.toString();
            //Log.e("shankOCRFrontObject", ocrFrontPlots);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            textRecognizer.release();
        }

        isFront = ((headerFound && foundAadhar && !noSpacesInNumber) || (aadharIndex-genderIndex == 1)) && !otherKeywordsFound;
        //isFront = isAadharIndex >= 2 && foundAadhar && !noSpacesInNumber && !otherKeywordsFound;

        return ocrData;
    }

    public String splitString(String thisWord, String withThis){

        String[] parts = thisWord.split(withThis);

        if(parts.length == 2){
            return parts[1];
        }else if (parts.length == 3){
            return parts[2];
        }else if (parts.length == 1){
            return parts[0];
        }else {
            return "";
        }
    }

    public boolean isAadhar(String aadhar){

        if (aadhar.length() >= 10){
            //Log.e("maskedAadhaar0", "here");
            String aadhar1 = aadhar.replaceAll("[:?.?/?,?a-z?A-Z]+", "");
            int len = aadhar1.length();
            String aadhar2 = aadhar1.replaceAll(" ", "");
            if (aadhar.length() == len){
                this.aadhar = aadhar2;
                if (aadhar2.length() == len){
                    noSpacesInNumber = true;
                }
                return true;
            }
            //Log.e("maskedAadhaar1", "here");
            String aadhar11 = aadhar.replaceAll("[:?.?/?,?>?<]+", "");
            String aadhar22 = aadhar11.replaceAll(" ", "");
            String afterX0 = aadhar22.replaceAll("x", "");
            String afterX = afterX0.replaceAll("X", "");
            String afterAZ = afterX0.replaceAll("[:?.?/?,?a-z?A-Z]+", "");
            if (aadhar.length() == aadhar11.length() && aadhar22.length() != afterX.length() && afterX.length() == afterAZ.length()){
//                Log.e("maskedAadhaar2", aadhar22);


                this.aadhar = aadhar22.replaceAll("X","x");
//                Log.e("maskedAadhaar22", this.aadhar);

                return true;
            }
        }
        return false;
    }

    public boolean isName(String name){

        if (name.length() >= 10 && !name.contains(":")){
            String name1 = name.replaceAll(" ", "");
            int len = name1.length();
            String name2 = name1.replaceAll("[:?.?/?,?0-9]+", "");
            if (len == name2.length() && !name2.contains("Fem") && !name2.contains("Male") && !name2.contains("male") && !name2.contains("FEM") && !name2.contains("MALE") && !name2.contains(":")){
                this.name = name;
                return true;
            }else return false;
        }
        return false;
    }

    public boolean isTransG(String sexString){
        sexString = sexString.toLowerCase();
        if(sexString.contains("trans") || sexString.contains("gender")){
            return true;
        }return false;
    }

    public boolean isFemale(String sexString){
        if(sexString.contains("Fem") || sexString.contains("emale") || sexString.contains("EMALE")|| sexString.contains("FEM")){
            return true;
        }return false;
    }

    public boolean isYOB(String yearString){
        if(yearString.contains("Year") || yearString.contains("YOB") || yearString.contains("YO8") || yearString.contains("Yo8") || yearString.contains("YoB") || yearString.contains("Birth")){

            if (yearString.length()>5){
                yearString = yearString.substring(yearString.length()-5);
            }
            String s2 = yearString.replaceAll("[:?a-z?A-Z]+", "");
            String s3 = s2.replaceAll(" ", "");
            this.year = s3;

            return  true;
        }else return false;
    }

    public boolean isDOB(String dateString){
        if(dateString.contains("DOB") || dateString.contains("Date") || dateString.contains("DO8")|| dateString.contains("DoB")|| dateString.contains("Do8")){

            if (dateString.length()>11){
                dateString = dateString.substring(dateString.length()-11);
            }
            String s2 = dateString.replaceAll("[:?a-z?A-Z]+", "");
            String s3 = s2.replaceAll(" ", "");
            this.date = s3;

            return true;
        }else return false;
    }

    public boolean isFather(String fatherString){
        if(fatherString.contains("Father") || fatherString.contains("ather") || fatherString.contains("Fat")){

            return true;
        }else return false;
    }

    public boolean isHeader(String headerString){

        headerString = headerString.toLowerCase();
        if (headerString.contains("govern") || headerString.contains("overnm") || headerString.contains("vern") || headerString.contains("gover") || headerString.contains("vernm") || headerString.contains("ernm") || headerString.contains("india") || headerString.contains("ndia")){
            return true;
        }else return false;
    }

    public boolean otherKeywordFound(String line){

        line = line.toLowerCase();
        if (line.contains("account") || line.contains("permanent") || line.contains("signature") || line.contains("income") || line.contains("election") || line.contains("passport") || line.contains("tax") || line.contains("nationality") || line.contains("voter") || line.contains("unique") || line.contains("republic") || line.contains("<<<<")){
            otherKeywordsFound = true;
            return true;
        }
        return false;
    }
}