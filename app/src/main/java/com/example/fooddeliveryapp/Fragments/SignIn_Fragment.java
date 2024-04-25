package com.example.fooddeliveryapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.service.autofill.RegexValidator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fooddeliveryapp.ApplicationUtilities.FormValidator;
import com.example.fooddeliveryapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignIn_Fragment extends Fragment {

    FormValidator form = new FormValidator();
    String email, password;

    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailField, passwordField;

    //on redirect to another fragment within the activity
    Handler.Callback registeredCallbackForFragmentSwitch;

    //Inform Authentication activity to start the activity procedure by callback
    Handler.Callback startSignInProcedure;

    public SignIn_Fragment(Handler.Callback fragmentSwitchingCallback, Handler.Callback SignInProcedureCallback) {
        // Required empty public constructor
        registeredCallbackForFragmentSwitch = fragmentSwitchingCallback;
        // Required empty public constructor
        startSignInProcedure = SignInProcedureCallback;
    }


    public boolean validateFields()
    {
        int flag = 1;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        Object emailReturn = FormValidator.emailValidator(email, null, null);
        Object passwordReturn = FormValidator.passwordValidator(password, 8, null);

        if ( !(emailReturn instanceof Boolean) )
        {
            emailLayout.setError(emailReturn.toString());
            flag = 0;
        }
        else
            emailLayout.setError(null);

        if ( !(passwordReturn instanceof Boolean) )
        {
            passwordLayout.setError(passwordReturn.toString());
            flag = 0;
        }
        else
            passwordLayout.setError(null);


        if( flag == 1 )
            return true;
        return false;
    }

    View.OnClickListener signin_request = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if( !validateFields() ) return;

            //preparing a data object
            Bundle formData = new Bundle();
            formData.putString("FORM", "sign_in");
            formData.putString("userEmail", email);
            formData.putString("userPassword", password);

            //preparing message object
            Message message = new Message();
            message.setData(formData);

            //calling callback from the authentication activity by throwing additiional data
            startSignInProcedure.handleMessage(message);
        }
    };

    View.OnFocusChangeListener focusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            validateFields();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        //fetching references
        emailLayout = view.findViewById(R.id.signin_email);
        passwordLayout = view.findViewById(R.id.signin_password);

        emailField = view.findViewById(R.id.signin_email_field);
        passwordField = view.findViewById(R.id.signin_password_field);


        //when the focus will be changed
        emailField.setOnFocusChangeListener(focusChange);
        passwordField.setOnFocusChangeListener(focusChange);

        //when the user starts typing remove all errors
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError(null);
            }
        });
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordLayout.setError(null);
            }
        });

        //signin manager process
        view.findViewById(R.id.signin_signin_button).setOnClickListener(signin_request);

        //when click on forgot password button
        view.findViewById(R.id.signin_forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparing data bundle instance
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "forgot_password");
                //sending as message to the registered callback parameters
                Message responseMessage = new Message();
                responseMessage.setData(bundle);
                //calling the callback with the provided message
                registeredCallbackForFragmentSwitch.handleMessage(responseMessage);
            }
        });

        //when click on signup button
        view.findViewById(R.id.signin_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparing data bundle instance
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "sign_up");
                //sending as message to the registered callback parameters
                Message responseMessage = new Message();
                responseMessage.setData(bundle);
                //calling the callback with the provided message
                registeredCallbackForFragmentSwitch.handleMessage(responseMessage);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}