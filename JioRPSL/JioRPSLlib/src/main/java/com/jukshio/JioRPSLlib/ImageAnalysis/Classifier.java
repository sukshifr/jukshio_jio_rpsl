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

import android.graphics.Bitmap;

import java.text.DecimalFormat;
import java.util.List;

public interface Classifier {

    class Recognition {

        private final String id;
        private final String title;
        private final Float confidence;

        public Recognition(final String id, final String title, final Float confidence) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + ",";
            }

            if (confidence != null) {

                DecimalFormat df = new DecimalFormat("#.######");
                resultString += df.format(confidence * 100.0f) + "]";
            }

            return resultString.trim();
        }
    }

    List<Recognition> recognizeImage(Bitmap bitmap);

    void close();
}
