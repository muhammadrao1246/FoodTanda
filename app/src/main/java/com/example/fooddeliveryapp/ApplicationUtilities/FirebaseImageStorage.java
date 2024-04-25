package com.example.fooddeliveryapp.ApplicationUtilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FirebaseImageStorage {


    public static void putImageOnImageView(String imagePath, ImageView imageView, ShimmerFrameLayout shimmer)
    {

        shimmer.startShimmer();
        //fethcing references to the specific file
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath);

        final long MAXFILESIZE = 10*(1024 * 1024); //maximum 10mb
        //storing image in temp file

        storageReference.getBytes(MAXFILESIZE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap imageD = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        imageView.setImageBitmap(imageD);

                        shimmer.stopShimmer();
                        shimmer.hideShimmer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Image Attachment Failed on:\t"+imagePath);
                    }
                });

    }
}
