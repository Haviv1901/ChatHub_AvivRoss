package com.example.chathub.Activities;

import static com.example.chathub.Utilities.flushTransactionHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_choose_participants);

        userManager = new UserManager(this);
        participants = new ArrayList<Participant>();
        selectedParticipants = new ArrayList<Participant>();

        // views
        btCreateChat = findViewById(R.id.btCreateChat);
        btBackToChooseNameAndPic = findViewById(R.id.btBackToChooseNameAndPic);
        tvChatNameChoosePart = findViewById(R.id.tvChatNameChoosePart);
        lvParticipants = findViewById(R.id.lvParticipants);
        etSearchParticipants = findViewById(R.id.etSearchParticipants);

        // onclicks
        btCreateChat.setOnClickListener(this);
        btBackToChooseNameAndPic.setOnClickListener(this);
        etSearchParticipants.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Utilities.debugToast(CreateChat_ChooseParticipants.this, "search");
                return false;
            }
        });

        // add list view on click listener
        lvParticipants.setOnItemClickListener(this);


        setTitleToChatName();

        // load list view
        loadParticipantsList();



    }

    private void setTitleToChatName()
    {
        Intent intent = getIntent();
        String chatName = intent.getStringExtra("chatName");
        tvChatNameChoosePart.setText(chatName);
        imageBytes = Utilities.getAndFlushTransactionHelper();
    }

    private void setParticipantAdapter()
    {
        participantAdapter = new ParticipantAdapter(this, R.layout.participant, participants);
        lvParticipants.setAdapter(participantAdapter);

    }

    private void loadParticipantsList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String username = snapshot.child("username").getValue(String.class);

                    participants.add(new Participant(username, uid));
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

    private void goBackToChooseNameAndPic()
    {
        finish();
    }

    private void createChat()
    {

        String chatName = tvChatNameChoosePart.getText().toString();

        ChatManager chatManager = new ChatManager(this);
        chatManager.createChat(chatName, selectedParticipants, imageBytes);


        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
        finish();
    }

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