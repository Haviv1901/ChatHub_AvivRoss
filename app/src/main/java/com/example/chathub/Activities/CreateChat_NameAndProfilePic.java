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


    private void chooseChatImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = convertImageToBitmap(selectedImage);
            setChatImage(bitmap);
        }
    }

    private Bitmap convertImageToBitmap(Uri imageUri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setChatImage(Bitmap bitmap) {
        // Assuming chatImage is an ImageView
        ibChooseChatImage.setImageBitmap(bitmap);
        chatImage = bitmap;
    }
}