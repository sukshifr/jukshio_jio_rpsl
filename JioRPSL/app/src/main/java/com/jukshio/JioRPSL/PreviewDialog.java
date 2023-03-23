package com.jukshio.JioRPSL;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class PreviewDialog {
    Bitmap bitmap;
    Activity activity;
    public PreviewDialog(Bitmap bitmap, Activity activity) {
        this.bitmap = bitmap;
        this.activity = activity;
        showDialog(activity);
    }

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.diolog_preview);
        ImageView imageView = dialog.findViewById(R.id.iv_preview);
        imageView.setImageBitmap(bitmap);
        dialog.show();

    }
}