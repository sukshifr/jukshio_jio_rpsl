package com.jukshio.JioRPSLlib.ImageAnalysis;

import static com.jukshio.JioRPSLlib.ImageAnalysis.ImageAnalysis.clientFaceModel_Error;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class FaceModel {
    //    private static final String MODEL_PATH = "model_fr2.tflite";
    public static final String MODEL_PATH = "06052021_Face.tflite";

    private static final int IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 255;

    private static final int BATCH_SIZE = 1;
    public static final int Asset_IMAGE_HEIGHT = 224;
    public static final int Asset_IMAGE_WIDTH = 224;
    private static final int NUM_CHANNELS = 3;
    private static final int NUM_BYTES_PER_CHANNEL = 4;
    public static final int assetOutputSize = 1;

    static File modelFilePath;

    private ByteBuffer imgData;

    private MappedByteBuffer tfliteModel;
    private Interpreter tflite;
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
//    public static boolean readyForFRAnalysis = false;

    public FaceModel(AssetManager assetManager) throws IOException, Exception {

        tfliteModel = loadModelFile(assetManager);

//        tfliteOptions.setNumThreads(2);


        tflite = new Interpreter(tfliteModel, tfliteOptions);
        imgData = ByteBuffer.allocateDirect(
                BATCH_SIZE * Asset_IMAGE_HEIGHT * Asset_IMAGE_WIDTH * NUM_CHANNELS * NUM_BYTES_PER_CHANNEL);

        imgData.order(ByteOrder.nativeOrder());
        ImageAnalysis.readyForAnalysis = true;


    }

    public FaceModel(File modelFilePath, int IMAGE_WIDTH, int IMAGE_HEIGHT) throws IOException, Exception {


//        tfliteOptions.setNumThreads(2);
        tflite = new Interpreter(modelFilePath, tfliteOptions);
        imgData = ByteBuffer.allocateDirect(
                BATCH_SIZE * IMAGE_HEIGHT * IMAGE_WIDTH * NUM_CHANNELS * NUM_BYTES_PER_CHANNEL);

        imgData.order(ByteOrder.nativeOrder());


        ImageAnalysis.readyForAnalysis = true;

    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap, int IMAGE_WIDTH, int IMAGE_HEIGHT) {
        final int[] intValues = new int[IMAGE_HEIGHT * IMAGE_WIDTH];
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < IMAGE_HEIGHT; ++i) {
            for (int j = 0; j < IMAGE_WIDTH; ++j) {
                final int val = intValues[pixel++];
                try {
                    addPixelValue(val);
                } catch (Exception exception) {
                    clientFaceModel_Error=clientFaceModel_Error+exception.getMessage();
                }
            }
        }

    }


    private void addPixelValue(int pixelValue)throws Exception {
        imgData.putFloat((((pixelValue >> 16) & 0xFF)) / IMAGE_STD);
        imgData.putFloat((((pixelValue >> 8) & 0xFF)) / IMAGE_STD);
        imgData.putFloat((((pixelValue) & 0xFF)) / IMAGE_STD);
    }


    private Bitmap resizedBitmap(Bitmap bitmap, int height, int width) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private Bitmap croppedBitmap(Bitmap bitmap, int upperCornerX, int upperCornerY, int height, int width) {
        return Bitmap.createBitmap(bitmap, upperCornerX, upperCornerY, width, height);
    }

    public float[] run(Bitmap bitmap, int outputSize, int IMAGE_WIDTH, int IMAGE_HEIGHT) throws  Exception {
        convertBitmapToByteBuffer(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT);

//        Log.e("imgData", Arrays.toString(imgData.array()));
//        Log.e("imgData", imgData.array().length+"");

        float[][] embeddings = new float[1][outputSize];
        tflite.run(imgData, embeddings);

        return embeddings[0];
    }


    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        tfliteModel = null;
    }

}
