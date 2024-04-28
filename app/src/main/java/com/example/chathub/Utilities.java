package com.example.chathub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utilities
{


    private static byte[] transactionHelper;


    // finals
    // privates
    private static final Boolean DEBUG = true;
    private static final String TAG = "Utilities";
    // publics
    // paths to firebase storage files:
    public static final String CHAT_IMAGE_PATH_STORAGE  = "images";
    public static final String CHAT_AUDIO_PATH_STORAGE = "Audio";
    public static final String CHAT_ICONS_STORAGE = "Group_Icon";
    // paths to realtime db:
    public static final String CHATS_PATH_DB = "Chats";
    public static final String USERS_PATH_DB = "Users";
    public static final String UTILITIES_PATH_DB = "Utilities";
    // global chat
    public static final String GLOBAL_CHAT_NAME = "THIS_CHAT_IS_255.255.255.255";



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

        makeToast(context, message);
    }

    public static void makeToast(Context context, String message)
    {
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

    public static String getTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    public static String uploadFile(String imageBasePath, Bitmap image)
    {
        // create a byte array of the image
        byte[] byteArray = Utilities.convertBitmapToByteArray(image);

        return uploadFile(imageBasePath, byteArray);
    }

    public static String uploadFile(String imageBasePath, byte[] byteArray)
    {

        String imagePath = imageBasePath + "/" + UUID.randomUUID().toString() + ".png";

        FirebaseStorage sotrageHandler = FirebaseStorage.getInstance();

        // upload the image to the storage
        UploadTask uploadTask = sotrageHandler.getReference(imagePath).putBytes(byteArray);

        return imagePath;
    }



}
