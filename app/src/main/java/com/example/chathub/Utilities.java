package com.example.chathub;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.chathub.Activities.ChatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utilities
{

    // helps with trasnfering big data between activities
    private static byte[] transactionHelper;

    // finals
    // privates
    // debug flag
    private static final Boolean DEBUG = false;
    // log tag
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
    // file extenstions
    public static final String PNG_EXTENSION = ".png";
    public static final String THREE_GPP_EXTENSION = ".3gpp";

    // permissions
    public static final String[] PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    /*
    * Function: setTransactionHelper
    * Inputs: byte[] transactionHelper - the data to be stored
    * Outputs: void
    * Description: This function sets the transactionHelper to the given data.
    * */
    public static void setTransactionHelper(byte[] transactionHelper)
    {
        Log.e(TAG, "setTransactionHelper, lengthed: " + transactionHelper.length);
        Utilities.transactionHelper = transactionHelper;
    }

/*
    * Function: getTransactionHelper
    * Inputs: void
    * Outputs: byte[] - the data stored in transactionHelper
    * Description: This function returns the data stored in transactionHelper.
 */
    public static byte[] getTransactionHelper()
    {
        Log.e(TAG, "getTransactionHelper, lengthed: " + transactionHelper.length);
        return transactionHelper;
    }

    /*
    * Function: flushTransactionHelper
    * Inputs: void
    * Outputs: void
    * Description: This function flushes the transactionHelper.
     */
    public static void flushTransactionHelper()
    {
        transactionHelper = null;
    }

    /*
    * Function: getAndFlushTransactionHelper
    * Inputs: void
    * Outputs: byte[] - the data stored in transactionHelper
    * Description: This function returns the data stored in transactionHelper and flushes it.
     */
    public static byte[] getAndFlushTransactionHelper()
    {
        byte[] temp = getTransactionHelper();
        flushTransactionHelper();
        return temp;
    }

    /*
    * Function: debugToast
    * Inputs: Context context - the context, String message - the message to display
    * Outputs: void
    * Description: This function displays a toast with the given message if the debug flag is set.
    * */
    public static void debugToast(Context context, String message)
    {
        if(!DEBUG)
        {
            return;
        }

        makeToast(context, message);
    }

    /*
    * Function: makeToast
    * Inputs: Context context - the context, String message - the message to display
    * Outputs: void
    * Description: This function displays a toast with the given message.
     */
    public static void makeToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /*
    * Function: convertBitmapToByteArray
    * Inputs: Bitmap bitmap - the bitmap to convert
    * Outputs: byte[] - the byte array of the bitmap
    * Description: This function converts the given bitmap to a byte array.
     */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap)
    {
        return convertBitmapToByteArray(bitmap, 0);
    }

/*
    * Function: convertBitmapToByteArray
    * Inputs: Bitmap bitmap - the bitmap to convert
    * Outputs: byte[] - the byte array of the bitmap
    * Description: This function converts the given bitmap to a byte array.
 */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap, int quality)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }

    /*
    * Function: convertByteArrayToBitmap
    * Inputs: byte[] byteArray - the byte array to convert
    * Outputs: Bitmap - the bitmap of the byte array
    * Description: This function converts the given byte array to a bitmap.
     */
    public static Bitmap convertByteArrayToBitmap(byte[] byteArray)
    {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    /*
    * Function: getTime
    * Inputs: void
    * Outputs: String - the current time in HH:mm format
    * Description: This function returns the current time in HH:mm format.
     */
    public static String getTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    /*
    * Function: uploadFile
    * Inputs: String imageBasePath - the base path of the image, Bitmap image - the image to upload, String extenstion - the extension of the image
    * Outputs: String - the path of the uploaded image
    * Description: This function uploads the given image to the given base path.
     */
    public static String uploadFile(String imageBasePath, Bitmap image, String extenstion)
    {
        // create a byte array of the image
        byte[] byteArray = Utilities.convertBitmapToByteArray(image);

        return uploadFile(imageBasePath, byteArray, extenstion);
    }

    /*
    * Function: uploadFile
    *   Inputs: String imageBasePath - the base path of the image, byte[] byteArray - the byte array of the image, String extenstion - the extension of the image
    *  Outputs: String - the path of the uploaded image
    * Description: This function uploads the given image to the given base path.
     */
    public static String uploadFile(String imageBasePath, byte[] byteArray, String extenstion)
    {

        String imagePath = imageBasePath + "/" + UUID.randomUUID().toString() + extenstion;

        FirebaseStorage sotrageHandler = FirebaseStorage.getInstance();

        // upload the image to the storage
        UploadTask uploadTask = sotrageHandler.getReference(imagePath).putBytes(byteArray);

        return imagePath;
    }

    /*
    * Function: readFileToByteArray
    * Inputs: String filePath - the path of the file to read
    * Outputs: byte[] - the byte array of the file
    * Description: This function reads the given file and returns it as a byte array.
     */
    public static byte[] readFileToByteArray(String filePath) throws IOException
    {
        File file = new File(filePath);
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
            int offset = 0;
            int numRead;
            while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0)
            {
                offset += numRead;
            }
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
        return bytes;
    }
}
