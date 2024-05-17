package com.example.chathub.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathub.Data_Containers.Message;
import com.example.chathub.Adapters.MessageAdapter;
import com.example.chathub.Data_Containers.VoiceMessageData;
import com.example.chathub.Managers.ChatManager;
import com.example.chathub.R;
import com.example.chathub.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.List;



public class ChatActivity extends MainActivity implements View.OnClickListener {

    // views
    private ListView messegesListView;
    private ImageView ibBack, ibCameraButton, ibMicrophoneButton, ibSendButton, ivImage, ivXIcon;
    private EditText etMessage;
    private LinearLayout imageBar;
    private TextView tvGroupName;

    // chat related
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private Boolean isImage; // did the user load an image
    private Bitmap image; // the image the user loaded
    private Boolean isRecording; // is the user recording a voice message

    // managers
    private ChatManager chatManager; // handles all chat realted things. Controler

    // consts
    private final String TAG = "ChatActivity";

    // voice recording
    private static String recordingFilePath = null; // path to recorded file
    private MediaRecorder recorder = null; // the recorder object
    private MediaPlayer player = null; // the recording player onject

    // for notificatiopn
    public static String chatNameForNotifications = ""; // the name of the chat for notifications


    /**
     * Function: onCreate
     * Input: Bundle savedInstanceState
     * Output: None
     * Description: This function is called when the activity is first created. It initializes the views, sets up the chat manager, and sets up the listeners for the messages.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recordingFilePath = getExternalCacheDir().getAbsolutePath();
        recordingFilePath += "/recordedFileTEMP.3gp";



        // managers
        Intent intent = getIntent();
        String chatName = intent.getStringExtra("chatName");
        int chatId = intent.getIntExtra("chatId", -1);

        checkForValidChat(chatName, chatId);

        chatManager = new ChatManager(chatName, chatId, this);
        chatNameForNotifications = chatName;

        // views

        messegesListView = findViewById(R.id.messegesListView);
        ibBack = findViewById(R.id.ibBack);
        ibCameraButton = findViewById(R.id.ibCameraButton);
        ibMicrophoneButton = findViewById(R.id.ibMicrophoneButton);
        etMessage = findViewById(R.id.etMessage);
        ibSendButton = findViewById(R.id.ibSendButton);
        ivImage = findViewById(R.id.ivImage);
        imageBar = findViewById(R.id.imageBar);
        ivXIcon = findViewById(R.id.ivXIcon);
        tvGroupName = findViewById(R.id.tvGroupName);

        // onclicks

        ibBack.setOnClickListener(this);
        ibCameraButton.setOnClickListener(this);
        ibMicrophoneButton.setOnClickListener(this);
        ibSendButton.setOnClickListener(this);
        ivXIcon.setOnClickListener(this);

        // set group name
        tvGroupName.setText(chatName);

        // set listener
        getMessagesFromFirebase();

        // set image bar to invisible
        hideImageBar();

        isRecording = false;


    }

    /**
     * Function: onDestroy
     * Input: None
     * Output: None
     * Description: This function is called before the activity is destroyed. It's the final call that the activity receives.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatNameForNotifications = "";
    }

    /**
     * Function: playButtonPressed
     * Input: Button playButton
     * Output: None
     * Description: This function is called when the play button is pressed. It retrieves data from the button and checks for errors.
     */
    private void playButtonPressed(Button playButton)
    {
        // retrieve data from button and check for errors
        VoiceMessageData audioMessageData = (VoiceMessageData) playButton.getTag();
        if(audioMessageData == null)
        {
            Log.e(TAG, "Error: audio path is empty");
            return;
        }
        ProgressBar progressBar = audioMessageData.progressBar;
        String audioFilePath = audioMessageData.audioFilePath;
        if(audioFilePath == null || audioFilePath.isEmpty() || progressBar == null)
        {
            Log.e(TAG, "Error getting voice message data from button tag");
            return;
        }

        // set loading
        progressBar.setVisibility(View.VISIBLE);

        // download file from firebase storage.
        chatManager.downloadAudioFromStorage(audioFilePath, this::playFile, progressBar);
    }

    /**
     * Function: playFile
     * Input: File file
     * Output: None
     * Description: This function is called when the file is ready to be played. It plays the file.
     */
    private void playFile(File file) {
        player = new MediaPlayer();
        try {
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }



    /**
     * Function: checkForValidChat
     * Input: String chatName, int chatId
     * Output: None
     * Description: This function checks if the chat is valid. If the chat id is -1, it logs an error. If the chat name is null, it logs an error and finishes the activity.
     */
    private void checkForValidChat(String chatName, int chatId)
    {
        if(chatId == -1)
        {
            Log.e(TAG, "Error: failed to get chat id, set to -1");
        }
        if(chatName == null)
        {
            Log.e(TAG, "Error: failed to get chat name");
            finish();
        }
    }

    /**
     * Function: getMessagesFromFirebase
     * Input: None
     * Output: None
     * Description: This function sets up the messages listener.
     */
    private void getMessagesFromFirebase()
    {
        chatManager.setupMessagesListener(this::updateMessagesList);
    }

    /**
     * Function: updateMessagesList
     * Input: List<Message> newMessages
     * Output: None
     * Description: This function updates the messages list.
     */
    private void updateMessagesList(List<Message> newMessages)
    {
        ChatManager.sortMessageListById(newMessages);

        messages = newMessages;
        messageAdapter = new MessageAdapter(ChatActivity.this, R.layout.text_message, messages, this::playButtonPressed);
        messegesListView.setAdapter(messageAdapter);
        messegesListView.setSelection(messages.size() - 1);
    }


    /**
     * Function: onClick
     * Input: View v
     * Output: None
     * Description: This function is called when a view is clicked. It checks which view was clicked and calls the appropriate function.
     */
    @Override
    public void onClick(View v)
    {
            if(v == ibBack)
            {
                //go to chat list activity
                backOnClick();
            }
            else if(v == ibCameraButton)
            {
                pickFromGallery();
            }
            else if(v == ibMicrophoneButton)
            {
                microphoneOnClick();
            }
            else if(v == ibSendButton)
            {
                //send message
                sendMessage();
            }
            else if(v == ivXIcon)
            {
                //remove image
                hideImageBar();
            }
            else
            {
                //do nothing
            }


    }

    /**
     * Function: microphoneOnClick
     * Input: None
     * Output: None
     * Description: This function is called when the microphone button is clicked. It starts or stops recording based on the current state.
     */
    private void microphoneOnClick()
    {
        if(isRecording)
        {
            ibMicrophoneButton.setImageResource(R.drawable.microphone);
            stopRecording();
        }
        else
        {
            startRecording();
            ibMicrophoneButton.setImageResource(R.drawable.microphone_green);
        }



    }

    /**
     * Function: stopRecording
     * Input: None
     * Output: None
     * Description: This function stops the recording and uploads the audio file to Firebase.
     */
    private void stopRecording()
    {
        recorder.stop();
        recorder.release();
        recorder = null;

        // extract file to byte arr
        byte[] audioByteArr = getByteArrayFromFile();
        if (audioByteArr == null) return;

        String audioLength = getAudioDuration(recordingFilePath);

        // upload file to firebase
        chatManager.uploadAudio(audioByteArr, audioLength);
        isRecording = false;

    }

    /**
     * Function: getAudioDuration
     * Input: String recordingFilePath
     * Output: String
     * Description: This function gets the duration of the audio file.
     */
    private String getAudioDuration(String recordingFilePath)
    {
        // get the duration of the recording
        MediaPlayer mediaPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(recordingFilePath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration(); // duration in milliseconds
            mediaPlayer.release();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the duration of the audio file", e);
        }

        // conver duration to MM:SS format
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return minutes + ":" + seconds;
    }

    /**
     * Function: getByteArrayFromFile
     * Input: None
     * Output: byte[]
     * Description: This function reads the audio file and returns it as a byte array.
     */
    @Nullable
    private byte[] getByteArrayFromFile() {
        byte[] audioByteArr;
        try
        {
            audioByteArr = Utilities.readFileToByteArray(recordingFilePath);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Failed to read audio file", e);
            return null;
        }
        return audioByteArr;
    }

    /**
     * Function: startRecording
     * Input: None
     * Output: None
     * Description: This function starts the recording.
     */
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Use the device microphone as the audio source
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Use the 3GPP media file format
        recorder.setOutputFile(recordingFilePath); // Set the output file path
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Use the AMR narrowband audio encoder

        try {
            recorder.prepare(); // Prepare the recorder
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start(); // Start recording
        isRecording = true;
    }

    /**
     * Function: hideImageBar
     * Input: None
     * Output: None
     * Description: This function hides the image bar.
     */
    private void hideImageBar()
    {
        ivImage.setVisibility(View.GONE);
        imageBar.setVisibility(View.GONE);
        imageBar.setVisibility(View.GONE);
        isImage = false;
    }

    /**
     * Function: showImageBar
     * Input: None
     * Output: None
     * Description: This function shows the image bar.
     */
    private void showImageBar()
    {
        ivImage.setVisibility(View.VISIBLE);
        imageBar.setVisibility(View.VISIBLE);
        imageBar.setVisibility(View.VISIBLE);
        isImage = true;
    }

    /**
     * Function: pickFromGallery
     * Input: None
     * Output: None
     * Description: This function opens the gallery and allows the user to pick an image.
     */
    private void pickFromGallery()
    {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,0);
    }

    /**
     * Function: onActivityResult
     * Input: int requestCode, int resultCode, @Nullable Intent data
     * Output: None
     * Description: This function is called when an activity returns a result. It gets the image from the gallery and sets it in the image view.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            if(selectedImage == null)
            {
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: selected image is null");
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }



            //put image in ImageView
            ivImage.setImageURI(selectedImage);

            // Get Bitmap from Uri
            try
            {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                showImageBar();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function: sendMessage
     * Input: None
     * Output: None
     * Description: This function sends the message to the database.
     */
    private void sendMessage()
    {

        String message = etMessage.getText().toString();
        // check that the message is not empty
        if(message.isEmpty() && isImage == false)
        {
            return;
        }

        // send message to data base
        if(isImage)
        {
            chatManager.sendTextMessage(message, image);
        }
        else
        {
            chatManager.sendTextMessage(message);
        }


        // reset the message box
        etMessage.setText("");
        hideImageBar();
    }

    /**
     * Function: onStop
     * Input: None
     * Output: None
     * Description: This function is called when the activity is stopped. It releases the recorder and player.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    /**
     * Function: backOnClick
     * Input: None
     * Output: None
     * Description: This function is called when the back button is clicked. It finishes the activity.
     */
    private void backOnClick()
    {
        finish();
    }
}