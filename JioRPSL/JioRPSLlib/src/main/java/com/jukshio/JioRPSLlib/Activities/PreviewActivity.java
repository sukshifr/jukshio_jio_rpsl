package com.jukshio.JioRPSLlib.Activities;

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

import static com.jukshio.JioRPSLlib.Activities.JukshioDocActivity.aadhaarAnalysis;
import static com.jukshio.JioRPSLlib.Activities.JukshioDocActivity.squareBitmapDoc;

import static com.jukshio.JioRPSLlib.LibConstants.IS_DOC_CONTEXT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jukshio.JioRPSLlib.Networking.JukshioNetworkHelper;
import com.jukshio.JioRPSLlib.R;
import com.jukshio.JioRPSLlib.SDKinitializationCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * PreviewActivity to preview images from DocCaptureActivity and FaceCaptureActivity.
 * **/
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_retake, bt_use_this;
    TextView tv_response;
    ImageView iv_document;
    public ProgressDialog dialog;
    JukshioNetworkHelper jukshioNetworkHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        dialog = new ProgressDialog(PreviewActivity.this);
        tv_response = findViewById(R.id.tv_response);
        bt_retake = findViewById(R.id.bt_retake);
        iv_document = findViewById(R.id.iv_document);
        bt_use_this = findViewById(R.id.bt_use_this);
        bt_retake.setOnClickListener(this);
        bt_use_this.setOnClickListener(this);
        processIntent(getIntent());

    }

    public void processIntent(Intent intent) {
        int is_doc_context = intent.getIntExtra(IS_DOC_CONTEXT, 2);
        switch (is_doc_context) {
            case 0:
                Glide.with(PreviewActivity.this).load(squareBitmapDoc).into(iv_document);
                //iv_document.setImageBitmap(squareBitmapDoc);
                break;
            case 1:
                //v_document.setImageBitmap(squareFace450Bitmap);
                break;
            default:
                Toast.makeText(this, "Please take photo properly", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent2 = new Intent();
        if (id == R.id.bt_retake) {

            aadhaarAnalysis.setupAadhaarAnalysis();
            setResult(Activity.RESULT_CANCELED, intent2);
            finish();

        } else if (id == R.id.bt_use_this) {

            setResult(Activity.RESULT_OK, intent2);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {

    }
}
