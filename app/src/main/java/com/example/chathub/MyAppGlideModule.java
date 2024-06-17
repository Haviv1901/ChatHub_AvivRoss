package com.example.chathub;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule
{

    /*
    * this class is a custom glide module for firebase storage
    *
    * explanation: this is a helper class from glide that helps us to load images from firebase storage
    * using glide external library.
    *
    * */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry)
    {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }
}