package com.example.fooddeliveryapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fooddeliveryapp.ApplicationUtilities.FormValidator;
import com.example.fooddeliveryapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class ForgotPassword_Fragment extends Fragment {

    FormValidator form = new FormValidator();

    String email;
    TextInputLayout emailLayout;
    TextInputEditText emailField;

    //on redirect to another fragment within the activity
    Handler.Callback registeredCallbackForFragmentSwitch;

    //Inform Authentication activity to start the activity procedure by callback
    Handler.Callback startForgotPasswordProcedure;

    public ForgotPassword_Fragment(Handler.Callback fragmentSwitchingCallback, Handler.Callback ForgotPasswordProcedureCallback)
    {
        // Required empty public constructor
        this.registeredCallbackForFragmentSwitch = fragmentSwitchingCallback;
        this.startForgotPasswordProcedure = ForgotPasswordProcedureCallback;
    }

    //field validator
    public boolean validateFields()
    {
        int flag = 1;
        email = emailField.getText().toString();

        Object emailReturn = FormValidator.emailValidator(email, null, null);

        if ( !(emailReturn instanceof Boolean) )
        {
            emailLayout.setError(emailReturn.toString());
            flag = 0;
        }
        else
            emailLayout.setError(null);


        if( flag == 1 )
            return true;
        return false;
    }


    View.OnClickListener forgot_password_request = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if( !validateFields() ) return;

            //preparing a data object
            Bundle formData = new Bundle();
            formData.putString("FORM", "forgot_password");
            formData.putString("userEmail", email);

            //preparing message object
            Message message = new Message();
            message.setData(formData);

            //calling callback from the authentication activity by throwing additiional data
            startForgotPasswordProcedure.handleMessage(message);
        }
    };

    View.OnFocusChangeListener focusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            validateFields();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        //fetching references
        emailLayout = view.findViewById(R.id.forgot_password_email);

        emailField = view.findViewById(R.id.forgot_password_email_field);


        //when the focus will be changed
        emailField.setOnFocusChangeListener(focusChange);

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

        //forgot password manager process
        view.findViewById(R.id.forgot_password_forgot_password_button).setOnClickListener(forgot_password_request);

        view.findViewById(R.id.forgot_password_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparing data bundle instance
                Bundle bundle = new Bundle();
                bundle.putString("purpose", "sign_in");
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