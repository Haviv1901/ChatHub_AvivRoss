package com.example.chathub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chathub.Data_Containers.*;
import com.example.chathub.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ParticipantAdapter extends ArrayAdapter<Participant>
{
    private Context context;
    private List<Participant> participantList;

    public ParticipantAdapter(@NonNull Context context, int resource, List<Participant> participantList)
    {
        super(context, resource, participantList);
        this.context = context;
        this.participantList = participantList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.participant, null);

        TextView partUsername = view.findViewById(R.id.tvParticipantName);
        CheckBox cbIsChoose = view.findViewById(R.id.cbIsChoosen);

        Participant participant = participantList.get(position);

        partUsername.setText(participant.getUsername());
        cbIsChoose.setChecked(participant.isSelected());

        return view;
    }
}