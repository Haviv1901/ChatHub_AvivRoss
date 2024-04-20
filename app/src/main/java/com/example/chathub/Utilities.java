package com.example.chathub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Utilities
{


    private static byte[] transactionHelper;


    private static final Boolean DEBUG = true;
    private static final String TAG = "Utilities";

    public static void setTransactionHelper(byte[] transactionHelper)
    {
        Log.e(TAG, "setTransactionHelper, lengthed: " + transactionHelper.length);
        Utilities.transactionHelper = transactionHelper;
    }

    public static byte[] getTransactionHelper()
    {
        Log.e(TAG, "getTransactionHelper, lengthed: " + transactionHelper.length);
        return transactionHelper;
    }

    public static void flushTransactionHelper()
    {
        transactionHelper = null;
    }

    public static byte[] getAndFlushTransactionHelper()
    {
        byte[] temp = getTransactionHelper();
        flushTransactionHelper();
        return temp;
    }

    public static void debugToast(Context context, String message)
    {
        if(!DEBUG)
        {
            return;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // convert bitmap to byte array, set quality to 0
    public static byte[] convertBitmapToByteArray(Bitmap bitmap)
    {
        return convertBitmapToByteArray(bitmap, 0);
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap, int quality)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArray)
    {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }



}
