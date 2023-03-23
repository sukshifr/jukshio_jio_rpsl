package com.jukshio.JioRPSLlib.Tracker;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * View class used for drawing out aadhar aspect ratio square with transparent color and background with white color.
 * This is used on top of camera preview class to abstarct only aadhar aspect ratio.
 *
 * **/
public class DocRectangleView extends View {

    private Paint p = new Paint();
    private Paint transparentPaint = new Paint();
    private Paint semiTransparentPaint = new Paint();
    private int parentWidth;
    private int parentHeight;
    double padding;
    Context context;
    int docType;
    /**
     * This constructor is used to initialise view programmatically using java.
     * **/
    public DocRectangleView(Context context) {
        super(context);
        init();
    }
    /**
     * This constructor is used to initialise view using xml declarations.
     * **/
    public DocRectangleView(Context context, double padding, int docType) {
        super(context);
        this.padding = padding;
        this.context = context;
        this.docType = docType;
        init();
    }
    /**
     * This constructor is used to initialise view using xml declarations.
     * **/
    public DocRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    /**
     * This method is used to initialise transparent paints and background paint object.
     * **/
    private void init() {
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        semiTransparentPaint.setColor(getResources().getColor(android.R.color.white));
    }
    /**
     * This invoked to draw the view.
     * Here we get display metrics and draw bitmap on to canvas with white as background and transparent color as aadhar rect view.
     * **/
    @SuppressLint({"DrawAllocation", "CanvasSize"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        int widthPreview = (int) (width * (1-(2*padding)));

        Bitmap bitmap = Bitmap.createBitmap(parentWidth, parentHeight, Bitmap.Config.ARGB_8888);
        Canvas cnvs = new Canvas(bitmap);

        /*Log.e("JukshioWidth",String.valueOf(width *2/3)+","+widthPreview);
        Log.e("JukshioWidth",String.valueOf((widthPreview / 2.9f)+","+(widthPreview / 2.98f)+","+(widthPreview / 1.7f)));
        float heighttt = ((width * 2 / 3) + (widthPreview / 2.9f)) - ((width * 2 / 3) - (widthPreview / 2.9f));
        int widtthhhh =  ((width + widthPreview) / 2) - ((width - widthPreview) / 2) ;
        Log.e("JukshioWH",widtthhhh+","+ heighttt );*/

        RectF rectangle;
        switch (docType) {
            case 0:
                rectangle = new RectF((width - widthPreview) / 2, ((width *2/3) - (widthPreview / 2.9f)), (width + widthPreview) / 2, (width * 2/3) + (widthPreview / 2.9f));

                break;
            case 1:
                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 2.98f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 2.98f));
                break;
            case 2:
                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.68f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                break;
            case 3:
                rectangle = new RectF((width - widthPreview) / 2, ((width *2/3) - (widthPreview / 2.9f)), (width + widthPreview) / 2, (width * 2/3) + (widthPreview / 2.9f));
//                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.42f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
//                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.68f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));

                break;
            case 4:
//                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.42f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.68f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                break;

            case 5:
//                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.42f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.68f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                break;

            case 6:
//                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.42f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
//                rectangle = new RectF((width *2/3) - (widthPreview /2f), (width * 2 / 3) - (widthPreview / 1.68f), (width * 2/3) + (widthPreview/6)  , (width * 2 / 3) + (widthPreview / 1.42f));

//                rectangle = new RectF((width *2/3) - (widthPreview/ 2.2f), ((width * 2 / 3) - (widthPreview / 1.68f)), (width *2/3) , (width * 2 / 3) + (widthPreview / 1.42f));
                rectangle = new RectF((width *2/3) - (widthPreview/ 2.5f), (width *2/3) - (widthPreview / 1.68f), (width *2/3) + (widthPreview/ 13f), (width * 2/3) + (widthPreview / 1.42f));
                break;
            case 7:
                rectangle = new RectF((width - widthPreview) / 2, ((width * 2 / 3) - (widthPreview / 1.68f)), (width + widthPreview) / 2, (width * 2 / 3) + (widthPreview / 1.42f));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + docType);
        }

//        RectF rectangle = new RectF((width - widthPreview) / 2, ((width *2/3) - (widthPreview / 2.9f)), (width + widthPreview) / 2, (width * 2/3) + (widthPreview / 2.9f));

        cnvs.drawRect(0, 0, cnvs.getWidth(), cnvs.getHeight(), semiTransparentPaint);
        cnvs.drawRoundRect(rectangle, 20, 20, transparentPaint);
        canvas.drawBitmap(bitmap, 0, 0, p);
    }
    /**
     * This invoked to get view view measurements before view inflation.
     * **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
