package com.example.fooddeliveryapp.Models;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.DatabaseId;

import java.util.Map;

public class Database {

    FirebaseFirestore con; //connection to firebase store
    public AppCompatActivity context;


    public Database(AppCompatActivity context)
    {
        this.context = context;
        con = FirebaseFirestore.getInstance();
    }

    public void insert(String tableName, Map<String, Object> tableData, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure)
    {
        con.collection(tableName)
                .add(tableData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

}
