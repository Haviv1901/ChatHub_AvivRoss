package com.example.chathub.Activities;

import static com.example.chathub.Utilities.flushTransactionHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chathub.Adapters.ParticipantAdapter;
import com.example.chathub.Data_Containers.Participant;
import com.example.chathub.Managers.ChatManager;
import com.example.chathub.Managers.UserManager;
import com.example.chathub.R;
import com.example.chathub.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreateChat_ChooseParticipants extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {



    // views
    private Button btCreateChat, btBackToChooseNameAndPic;
    private TextView tvChatNameChoosePart;
    private EditText etSearchParticipants;

    // list view
    private ListView lvParticipants;
    private ParticipantAdapter participantAdapter;
    private List<Participant> participants;

    // managers
    private UserManager userManager;

    // consts
    private static final String TAG = "CreateChat_ChooseParticipants";

    // other
    private List<Participant> selectedParticipants;
    private byte[] imageBytes;

    /**
     * Function: onCreate
     * Input: Bundle savedInstanceState - A mapping from String keys to various Parcelable values.
     * Output: void
     * Description: This function is called when the activity is starting. It initializes the activity, sets the content view,
     *              initializes the UserManager, participants list, selected participants list, views, and sets onClick listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_choose_participants);

        userManager = new UserManager(this);
        participants = new ArrayList<Participant>();
        selectedParticipants = new ArrayList<Participant>();
        selectedParticipants.add(new Participant(userManager.getCurrentUsername(), userManager.getCurrentUid()));

        // views
        btCreateChat = findViewById(R.id.btCreateChat);
        btBackToChooseNameAndPic = findViewById(R.id.btBackToChooseNameAndPic);
        tvChatNameChoosePart = findViewById(R.id.tvChatNameChoosePart);
        lvParticipants = findViewById(R.id.lvParticipants);
        etSearchParticipants = findViewById(R.id.etSearchParticipants);

        // onclicks
        btCreateChat.setOnClickListener(this);
        btBackToChooseNameAndPic.setOnClickListener(this);
        etSearchParticipants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start have just replaced old text that had length before.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called to notify you that, somewhere within s, the text has
                // been changed.
                loadParticipantsList(etSearchParticipants.getText().toString());
            }
        });

        // add list view on click listener
        lvParticipants.setOnItemClickListener(this);


        setTitleToChatName();

        // load list view
        loadParticipantsList("");



    }

    /**
     * Function: setTitleToChatName
     * Input: None
     * Output: void
     * Description: This function sets the title of the activity to the chat name.
     */
    private void setTitleToChatName()
    {
        Intent intent = getIntent();
        String chatName = intent.getStringExtra("chatName");
        tvChatNameChoosePart.setText(chatName);
        imageBytes = Utilities.getAndFlushTransactionHelper();
    }

    /**
     * Function: setParticipantAdapter
     * Input: None
     * Output: void
     * Description: This function sets the participant adapter for the list view.
     */
    private void setParticipantAdapter()
    {
        removeUserLoggedInFromParticipantsList();
        participantAdapter = new ParticipantAdapter(this, R.layout.participant, participants);
        lvParticipants.setAdapter(participantAdapter);

    }

    /**
     * Function: removeUserLoggedInFromParticipantsList
     * Input: None
     * Output: void
     * Description: This function removes the user logged in from the participants list.
     */
    private void removeUserLoggedInFromParticipantsList()
    {
        for (int i = 0; i < participants.size(); i++)
        {
            if(participants.get(i).getUid().equals(userManager.getCurrentUid()))
            {
                participants.remove(i);
                break;
            }
        }

    }

    /**
     * Function: loadParticipantsList
     * Input: String search - the search string
     * Output: void
     * Description: This function loads the participants list from Firebase. It adds the participants to the list and sets the participant adapter.
     */
    private void loadParticipantsList(String search)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                participants.clear(); // Clear the participants list

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String username = snapshot.child("username").getValue(String.class);

                    if(username.toLowerCase().contains(search.toLowerCase()))
                    {
                        participants.add(new Participant(username, uid));
                    }
                }
                // Do something with the map of users
                setParticipantAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadParticipantsList:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Function: onClick
     * Input: View v - the view
     * Output: void
     * Description: This function is called when a view has been clicked. It checks if the view is the create chat button or the back to choose name and pic button.
     */
    @Override
    public void onClick(View v)
    {

        if(v == btCreateChat)
        {
            // create chat
            createChat();
        }
        if(v == btBackToChooseNameAndPic)
        {
            // go back to choose name and pic
            goBackToChooseNameAndPic();
        }

    }

    /**
     * Function: goBackToChooseNameAndPic
     * Input: None
     * Output: void
     * Description: This function finishes the activity and goes back to the choose name and pic activity.
     */
    private void goBackToChooseNameAndPic()
    {
        finish();
    }

    /**
     * Function: createChat
     * Input: None
     * Output: void
     * Description: This function creates the chat and finishes the activity.
     */
    private void createChat()
    {

        String chatName = tvChatNameChoosePart.getText().toString();

        ChatManager chatManager = new ChatManager(this);
        chatManager.createChat(chatName, selectedParticipants, imageBytes);


        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Function: onItemClick
     * Input: AdapterView<?> parent - the parent view, View view - the view, int position - the position, long id - the id
     * Output: void
     * Description: This function is called when an item in the list view has been clicked. It checks if the participant is already selected, if it is, it removes it from the list, if not, it adds it to the list.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Participant participant = participants.get(position);

        // if the participant is already selected, remove it from the list
        if(selectedParticipants.contains(participant))
        {
            selectedParticipants.remove(participant);
            participant.setSelected(false);
        }
        else
        {
            selectedParticipants.add(participant);
            participant.setSelected(true);
        }

        // get the checkbox from the view
        CheckBox cbIsChoosen = view.findViewById(R.id.cbIsChoosen);

        // set the check box to the current selected state of the participant
        cbIsChoosen.setChecked(participant.isSelected());

        participantAdapter.notifyDataSetChanged();
    }

}