package com.example.chathub.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chathub.R;
import com.example.chathub.Utilities;

import java.io.IOException;

public class CreateChat_NameAndProfilePic extends MainActivity implements View.OnClickListener {

    private ImageButton ibChooseChatImage;
    private EditText etChatName;
    private Button btContinueNameAndPic;
    private Bitmap chatImage;


    private static final int REQUEST_IMAGE_PICK = 1;
    private final String TAG = "CreateChat_NameAndProfilePic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_name_and_profile_pic);

        // views
        ibChooseChatImage = findViewById(R.id.ibChooseChatImage);
        etChatName = findViewById(R.id.etChatName);
        btContinueNameAndPic = findViewById(R.id.btContinueNameAndPic);


        // on clicks

        ibChooseChatImage.setOnClickListener(this);
        btContinueNameAndPic.setOnClickListener(this);


    }

    /**
     * Function: onClick
     * Input: View v - the view that was clicked
     * Output: void
     * Description: This function is called when a view is clicked. It checks which view was clicked and calls the appropriate function.
     */
    @Override
    public void onClick(View v) {

        if (v == btContinueNameAndPic) {
            // continue to next activity
            continueToNextActivity();
        }
        if (v == ibChooseChatImage) {
            // choose chat image
            chooseChatImage();
        }


    }

    /**
     * Function: continueToNextActivity
     * Input: void
     * Output: void
     * Description: This function is called when the user clicks the continue button. It checks if the chat name is valid and if an image was chosen.
     *              If the chat name is valid and an image was chosen, it converts the image to a byte array and starts the next activity.
     */
    private void continueToNextActivity() {
        String chatName = etChatName.getText().toString();

        if (!checkFields(chatName)) {
            return;
        }

        // Convert bitmap to byte array
        byte[] chatImageByteArray = Utilities.convertBitmapToByteArray(chatImage);

        Intent intent = new Intent(this, CreateChat_ChooseParticipants.class);
        intent.putExtra("chatName", chatName);
        Utilities.setTransactionHelper(chatImageByteArray);
        startActivity(intent);
    }

    /**
     * Function: checkFields
     * Input: String chatName - the chat name
     * Output: boolean - true if the chat name is valid and an image was chosen, false otherwise
     * Description: This function checks if the chat name is valid and an image was chosen. If the chat name is not valid or an image was not chosen, it displays an error message.
     */
    private boolean checkFields(String chatName) {

        // chat name at least 4 characters, only english and numbers.

        if (chatName.length() < 4)
        {
            etChatName.setError("Chat name must be at least 4 characters");
            return false;
        }

        if (!chatName.matches("[a-zA-Z0-9]+"))
        {
            etChatName.setError("Chat name must contain only english letters and numbers");
            return false;
        }

        // and bitmap is not null
        if (chatImage == null) {
            // no image chosen
            Toast.makeText(this, "Please choose a chat image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    /**
     * Function: chooseChatImage
     * Input: void
     * Output: void
     * Description: This function is called when the user clicks the choose chat image button. It opens the gallery to choose an image.
     */
    private void chooseChatImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    /**
     * Function: onActivityResult
     * Input: int requestCode - the request code, int resultCode - the result code, Intent data - the data
     * Output: void
     * Description: This function is called when the user returns from the gallery. It gets the image URI, converts it to a bitmap, and sets the chat image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = convertImageToBitmap(selectedImage);
            setChatImage(bitmap);
        }
    }

    /**
     * Function: convertImageToBitmap
     * Input: Uri imageUri - the image URI
     * Output: Bitmap - the bitmap
     * Description: This function converts the image URI to a bitmap.
     */
    private Bitmap convertImageToBitmap(Uri imageUri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function: setChatImage
     * Input: Bitmap bitmap - the chat image
     * Output: void
     * Description: This function sets the chat image.
     */
    private void setChatImage(Bitmap bitmap) {
        // Assuming chatImage is an ImageView
        ibChooseChatImage.setImageBitmap(bitmap);
        chatImage = bitmap;
    }
}