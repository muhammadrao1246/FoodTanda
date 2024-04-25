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
import com.example.fooddeliveryapp.Models.User;
import com.example.fooddeliveryapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Fragment extends Fragment {

    FormValidator form = new FormValidator();

    String name, email, newPassword, confirmPassword;

    TextInputLayout nameLayout, emailLayout, newPasswordLayout, confirmPasswordLayout;
    TextInputEditText nameField, emailField, newPasswordField, confirmPasswordField;

    //on redirect to another fragment within the activity
    Handler.Callback registeredCallbackForFragmentSwitch;

    //Inform Authentication activity to start the activity procedure by callback
    Handler.Callback startSignUpProcedure;

    public SignUp_Fragment(Handler.Callback fragmentSwitchingCallback, Handler.Callback SignUpProcedureCallback) {
        // Required empty public constructor
        registeredCallbackForFragmentSwitch = fragmentSwitchingCallback;
        // Required empty public constructor
        startSignUpProcedure = SignUpProcedureCallback;
    }

    public boolean validateFields()
    {
        int flag = 1;
        name = nameField.getText().toString();
        email = emailField.getText().toString();
        newPassword = newPasswordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();

        Object nameReturn = FormValidator.nameValidator(name, null, null);
        Object emailReturn = FormValidator.emailValidator(email, null, null);
        Object passwordReturn = FormValidator.passwordValidator(newPassword, 8, null);

        if ( !(nameReturn instanceof Boolean) )
        {
            nameLayout.setError(nameReturn.toString());
            flag = 0;
        }
        else
            nameLayout.setError(null);

        if ( !(emailReturn instanceof Boolean) )
        {
            emailLayout.setError(emailReturn.toString());
            flag = 0;
        }
        else
            emailLayout.setError(null);

        if ( !(passwordReturn instanceof Boolean) )
        {
            newPasswordLayout.setError(passwordReturn.toString());
            flag = 0;
        }
        else
            newPasswordLayout.setError(null);

        if( !confirmPassword.isEmpty() )
        {
            if( !newPassword.equals(confirmPassword) )
            {
                confirmPasswordLayout.setError("New password and Confirm password must be same.");
                flag = 0;
            }
            else
                confirmPasswordLayout.setError(null);
        }
        else
            confirmPasswordLayout.setError("Confirm password field should not be empty.");


        if( flag == 1 )
            return true;
        return false;
    }

    //if the button of sign up is clicked
    View.OnClickListener signup_request = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //and all fields are validated
            if( !validateFields() ) return;

            //preparing a data object
            Bundle formData = new Bundle();
            formData.putString("FORM", "sign_up");
            formData.putString("userName", name);
            formData.putString("userEmail", email);
            formData.putString("userPassword", confirmPassword);
            formData.putString("userNumber", null);
            formData.putString("userLocation", null);

            //preparing message object
            Message message = new Message();
            message.setData(formData);

            //calling callback from the authentication activity by throwing additiional data
            startSignUpProcedure.handleMessage(message);
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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //fetching references
        nameLayout = view.findViewById(R.id.signup_name);
        emailLayout = view.findViewById(R.id.signup_email);
        newPasswordLayout = view.findViewById(R.id.signup_new_password);
        confirmPasswordLayout = view.findViewById(R.id.signup_confirm_password);

        nameField = view.findViewById(R.id.signup_name_field);
        emailField = view.findViewById(R.id.signup_email_field);
        newPasswordField = view.findViewById(R.id.signup_new_password_field);
        confirmPasswordField = view.findViewById(R.id.signup_confirm_password_field);


        //when the focus will be changed
        emailField.setOnFocusChangeListener(focusChange);
        newPasswordField.setOnFocusChangeListener(focusChange);

        //when the user starts typing remove all errors
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameLayout.setError(null);
            }
        });
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
        newPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newPasswordLayout.setError(null);
            }
        });
        confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmPasswordLayout.setError(null);
            }
        });

        //signin manager process
        view.findViewById(R.id.signup_signup_button).setOnClickListener(signup_request);

        view.findViewById(R.id.signup_signin).setOnClickListener(new View.OnClickListener() {
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