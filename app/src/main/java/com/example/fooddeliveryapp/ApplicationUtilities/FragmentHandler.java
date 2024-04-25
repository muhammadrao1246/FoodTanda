package com.example.fooddeliveryapp.ApplicationUtilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentHandler {
    public Fragment fragment;
    public AppCompatActivity context;

    int containerID;

    public FragmentHandler(Fragment fragment, AppCompatActivity context, int containerID) {
        this.fragment = fragment;
        this.context = context;
        this.containerID = containerID;
    }

    public void loadFragment(String purpose, boolean backStackAllowed)
    {
        //getting fragment manager
        FragmentManager fragmentManager = context.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        switch (purpose)
        {
            case "add":
                fragmentTransaction.add(containerID , fragment);
                break;
            case "remove":
                fragmentTransaction.remove(fragment);
                break;
            case "hide":
                fragmentTransaction.hide(fragment);
                break;
            case "show":
                fragmentTransaction.show(fragment);
                break;
            case "attach":
                fragmentTransaction.attach(fragment);
                break;
            case "detach":
                fragmentTransaction.detach(fragment);
                break;
            case "replace":
                fragmentTransaction.replace(containerID, fragment);
                break;
        }


        if( backStackAllowed ) fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());

        fragmentTransaction.commit();
    }
}
