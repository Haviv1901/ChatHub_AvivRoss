package com.example.chathub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class MessageAdapter extends ArrayAdapter<Message>
{
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(@NonNull Context context, int resource, int textViewResourceId, List<Message> messageList)
    {
        super(context, resource, textViewResourceId, messageList);
        this.context = context;
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.message, null);

        TextView content = view.findViewById(R.id.tvMessageContent);
        TextView time = view.findViewById(R.id.tvMessageTime);
        TextView sender = view.findViewById(R.id.tvSenderName);
        ImageView image = view.findViewById(R.id.ivMessageImage);

        Message message = messageList.get(position);

        content.setText(message.getContent());
        time.setText(message.getDate());
        sender.setText(message.getSender());

        if(message.getImage() == null || message.getImage().isEmpty())
        {
            image.setVisibility(View.GONE);
        }
        else
        {
            // Get a reference to the storage object
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageReference islandRef = storageReference.child(message.getImage());

            final long MAX_DOWNLOAD = 1024 * 1024 * 20;
            islandRef.getBytes(MAX_DOWNLOAD).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // bytes to bitmap and then load to image
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        return view;
    }
}