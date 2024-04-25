package com.example.fooddeliveryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fooddeliveryapp.ApplicationUtilities.Debugger;
import com.example.fooddeliveryapp.ApplicationUtilities.DialogsManager;
import com.example.fooddeliveryapp.ApplicationUtilities.FragmentHandler;
import com.example.fooddeliveryapp.ApplicationUtilities.LoaderActivator;
import com.example.fooddeliveryapp.Fragments.ForgotPassword_Fragment;
import com.example.fooddeliveryapp.Models.User;
import com.example.fooddeliveryapp.Fragments.SignIn_Fragment;
import com.example.fooddeliveryapp.Fragments.SignUp_Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaAction;
import com.google.android.recaptcha.RecaptchaClient;
import com.google.android.recaptcha.RecaptchaTasksClient;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.internal.RecaptchaActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;

import io.grpc.okhttp.internal.framed.FrameReader;

public class Authentication_Page extends AppCompatActivity {

    LoaderActivator loader;
    FirebaseAuth auth;
    GoogleSignInClient signInClient;
    int RC_SIGNIN = 1;
    int RC_SIGNUP = 2;

    String userId = "";
    MaterialToolbar topNav;
    FragmentHandler handler;
    Debugger debug;
    String intention;

    HashMap<String, Fragment> supportedFragments = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        supportedFragments.put("Sign In", new SignIn_Fragment(callbackFragmentSwitcher, fragmentAuthenticationHandler));
        supportedFragments.put("Sign Up", new SignUp_Fragment(callbackFragmentSwitcher, fragmentAuthenticationHandler));
        supportedFragments.put("Forgot Password", new ForgotPassword_Fragment(callbackFragmentSwitcher, fragmentAuthenticationHandler));
//        supportedFragments.put("Google Sign In", new SignIn_Fragment(callback));
//        supportedFragments.put("Google Sign Up", new SignUp_Fragment());

    }

//    FIREBASE AUTHENTICATION PROCESS ROUTINES


    public void emailVerificationHandler()
    {
        FirebaseUser user = auth.getCurrentUser();

            //please verify your account before sign in
            //an verification link is sent to your mail inbox
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //verification email is sent
                                auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                    @Override
                                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                        if( firebaseAuth.getCurrentUser().isEmailVerified() )
                                        {
                                            loader.hide();
                                            onSuccessDialog("You account have been verified successfully. Enjoy your food!");
                                        }
                                        else{
                                            loader.hide();
                                            //user account not verified still please try later or change your email to a valid and a real one
                                            DialogsManager.basicAlert(Authentication_Page.this, "error",
                                                    "Email verification link is sent. Please check your inbox and verify your email. before logging in again.",
                                                    "Authentication Failed",
                                                    "Go Back",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            onBackPressed();
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                            else
                            {
                                loader.hide();
                                //user account not verified still please try later or change your email to a valid and a real one
                                DialogsManager.basicAlert(Authentication_Page.this, "error",
                                        "Email verification link is sent. Please check your inbox and verify your email. before logging in again.",
                                        "Authentication Failed",
                                        "Go Back",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                onBackPressed();
                                            }
                                        });

                            }

                        }
                    });

    }
    public void firebaseForgotPasswordWithEmail(Bundle formData)
    {
        String email = formData.getString("userEmail").trim();

        //verifying user first

        //sending email also affter completting the reCaptcha token to ensure there is a legit user
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            loader.hide();
                            DialogsManager.basicAlert(Authentication_Page.this, "success",
                                    "Please check your mail inbox to reset or change your password.",
                                    "Password Reset Email Sent",
                                    "Go Back",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //auth.getCurrentUser().delete();
                                            onBackPressed();
                                        }
                                    });
                        }
                        else
                        {
                            FirebaseAuthenticationErrors(task.getException());
                        }
                    }
                });

    }

    public void firebaseSignInAuthenticationWithEmailAndPassword(Bundle formData)
    {
        String email = formData.getString("userEmail").trim();
        String password = formData.getString("userPassword");

        //begining the firebase auth signing in process
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the task is successful no error occured till
                        if( task.isSuccessful() )
                        {
                            loader.hide();
                            //saving the current user to firebase user
                            FirebaseUser user = auth.getCurrentUser();

                            if( !user.isEmailVerified() )
                            {
                                loader.show();
                                emailVerificationHandler();
                            }
                            else{
                                onSuccessDialog("You have successfully logged in.");
                            }

                            //continuing the next app logic redirecting the user to dashboard


                        }
                        else
                        {
                            FirebaseAuthenticationErrors(task.getException());
                        }
                    }
                });
    }


    public void firebaseSignUpAuthenticationWithEmailAndPassword(Bundle formData)
    {
        String name = formData.getString("userName");
        String email = formData.getString("userEmail").trim();
        final String password = formData.getString("userPassword");

        //first we have to check whether an account is already registered with this email or not
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if( task.isSuccessful() )
                        {
                            SignInMethodQueryResult result = task.getResult();
                            //means the sign in methods are bound to the email which indirectly means an account exists already
                            if( result != null && result.getSignInMethods() != null )
                            {
                                List<String> all_methods_with_email = result.getSignInMethods();

                                //means there are no previous account registered with this email
                                if( all_methods_with_email.size() == 0 )
                                {
                                    //initiating the signup process of account creation
                                    auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    //user signed up successfully
                                                    if( task.isSuccessful() )
                                                    {
                                                        //storing the user information
                                                        FirebaseUser user = auth.getCurrentUser();

                                                        //then storing the secondary user details in user collection
                                                        // in the firestore
                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                        db.collection("users")
                                                                .document(auth.getUid())
                                                                .set(User.getMap(name, email, null, password, null, null))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        loader.hide();
                                                                        //data stored in the users collection

                                                                        //Also have to send verification email initially but the account is created
                                                                        //whether user clicks on email verification or not

                                                                        user.sendEmailVerification()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if( task.isSuccessful() )
                                                                                        {
                                                                                            onSuccessDialog("You have successfully Signed Up. \nPlease check your inbox an Email Verification Link is sent, So you should verify your account before making any purchases via app.");
                                                                                        }
                                                                                        else{

                                                                                            FirebaseAuthenticationErrors(task.getException());
                                                                                        }
                                                                                    }
                                                                                });


                                                                        //you have successfully logged in
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        loader.hide();
                                                                        FirebaseFirestoreErrors(e);
                                                                    }
                                                                });

                                                    }
                                                    else {
                                                        FirebaseAuthenticationErrors(task.getException());
                                                    }
                                                }
                                            });
                                }
                                else
                                {
                                    loader.hide();
                                    //error while authenticating your email
                                    //try checking your internet connection or try later
                                    onUserCollisionDialog();
                                }


                            }
                            else{
                                FirebaseAuthenticationErrors(task.getException());
                            }
                        }
                        else
                        {
                            FirebaseAuthenticationErrors(task.getException());
                        }
                    }
                });

    }

//    GOOGLE AUTHENTICATION PROCESS ROUTINES
    public void callingGoogleAuthentication()
    {

        //options object
        GoogleSignInOptions options = new GoogleSignInOptions.Builder()
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        signInClient = GoogleSignIn.getClient(this, options);

        Intent openGoogleActivity = signInClient.getSignInIntent();
        startActivityForResult(openGoogleActivity, RC_SIGNIN);
    }

    public void verifyEmailToForceOnlyOneAccount(GoogleSignInAccount account)
    {

        //crating credention firebase object for user
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        //verifiying that the user did not signed with other email/password provider
        auth.fetchSignInMethodsForEmail(account.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if(task.isSuccessful())
                                {
                                    SignInMethodQueryResult result = task.getResult();
                                    if( result != null && result.getSignInMethods() != null )
                                    {
                                        List<String> all_methods_one_email = result.getSignInMethods();
                                        if( all_methods_one_email.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD) )
                                        {
                                            loader.hide();
                                            //user already registered with email and password
                                            //so don't allow him to login with the google
                                            onUserCollisionDialog();
                                        }
                                        else
                                        {
                                            //proceed to google sign in process with firebase auth
                                            authenticateGoogleAccountWithFirebase(account);
                                            signInClient.revokeAccess();
                                            signInClient.signOut();
                                        }
                                    }
                                    else
                                    {
                                        FirebaseAuthenticationErrors(task.getException());
                                    }
                                }
                                else
                                {
                                    FirebaseAuthenticationErrors(task.getException());
                                }
                            }
                        });

    }

    public void authenticateGoogleAccountWithFirebase(GoogleSignInAccount account)
    {
        //crating credention firebase object for user
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //store the user details
                            FirebaseUser user = auth.getCurrentUser();

                            //firestore reference acquired
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            System.out.println(User.getMap(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), null, null, user.getPhotoUrl().toString()).toString());

                            //first verifying whether the document is previously in the firestore or not if not then write

                            db.collection("users")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //if exists document then don't overwrite it
                                            if( documentSnapshot.exists() ) {
                                                loader.hide();
                                                onSuccessDialog("You have successfully logged in.");
                                                return;
                                            }

                                            //storing user information in firestore collection to later fetch it for the first time
                                            db.collection("users")
                                                    .document(user.getUid())
                                                    .set(User.getMap(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), null, null, user.getPhotoUrl().toString()))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            loader.hide();
                                                            onSuccessDialog("You have successfully logged in.");
                                                            //you have successfully logged in
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loader.hide();
                                                            FirebaseFirestoreErrors(e);
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loader.hide();
                                            FirebaseFirestoreErrors(e);
                                        }
                                    });
                        }
                        else
                        {
                            FirebaseAuthenticationErrors(task.getException());
                        }
                    }
                });
    }

    public void freshIntentActivity ( AppCompatActivity source, Class destination )
    {
        Intent new_session  = new Intent(source, destination);
        new_session.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(new_session);
        finish();
    }

    public void onUserCollisionDialog( )
    {
        DialogsManager.basicAlert(Authentication_Page.this, "error",
                "An account already registered with this email. Try another email.",
                "Authentication Failed",
                "Try Again",
                null);
    }
    public void onSuccessDialog( String message )
    {
//        DialogsManager.basicAlert(Authentication_Page.this, "success",
//                message,
//                "Authentication Successful",
//                "Go to dashboard",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        auth.getCurrentUser().delete();
                        freshIntentActivity(Authentication_Page.this,App_Dashboard.class);
//                    }
//                });
    }

    /*
    When using the addOnFailureListener in Firestore methods, you can handle different types of exceptions that may occur. Here are some common errors you can consider displaying in the onFailureListener:

    Network Error:

    Exception: FirebaseFirestoreException with code as FirebaseFirestoreException.Code.UNAVAILABLE
    Error Message: "Network not available. Please check your internet connection."
    This error occurs when there is a network issue, and the Firestore operation couldn't be completed due to the unavailability of the network connection.

    Permission Denied:

    Exception: FirebaseFirestoreException with code as FirebaseFirestoreException.Code.PERMISSION_DENIED
    Error Message: "Permission denied. You don't have access to perform this operation."
    This error occurs when the user does not have the necessary permissions to perform the requested Firestore operation. Make sure the user has the appropriate permissions in your Firestore security rules.

    Document Not Found:

    Exception: FirebaseFirestoreException with code as FirebaseFirestoreException.Code.NOT_FOUND
    Error Message: "Document not found. The requested document does not exist."
    This error occurs when the document you're trying to access or manipulate does not exist in Firestore.

    Unknown Error:

    Exception: FirebaseFirestoreException with other error codes or general exceptions.
    Error Message: "An unknown error occurred. Please try again later."
    This error message can be used as a general catch-all for any unknown errors that may occur during Firestore operations.
     */
    public Object FirebaseFirestoreErrors(Exception exception)
    {
        //if the provided email and password are not correct or valid
        if( exception instanceof FirebaseFirestoreException )
        {
            FirebaseFirestoreException fireEx = (FirebaseFirestoreException) exception;
            FirebaseFirestoreException.Code errorCode = fireEx.getCode();

            if( errorCode == FirebaseFirestoreException.Code.UNAVAILABLE )
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Network not available, failed to proceed your request. Please check your internet connection or try later.",
                    "Authentication Failed",
                    "Try Again",
                    null);
            //permission denied while accessing the unaccessible content
            else if( errorCode == FirebaseFirestoreException.Code.PERMISSION_DENIED )
                return "PERMISSION DENIED";
            //document accessing is not found
            else if( errorCode == FirebaseFirestoreException.Code.NOT_FOUND )
                return "DOCUMENT NOT FOUND";
            else
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                        "Unknown error occurred. Please try again.",
                        "Authentication Failed",
                        "Try Again",
                        null);
        }
        else{
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Unknown error occurred. Please try again.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }

        return false;
    }

    public void FirebaseAuthenticationErrors(Exception task)
    {
        loader.hide();
        //if the provided email and password are not correct or valid
        if( task instanceof FirebaseAuthInvalidCredentialsException)
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Email or password is incorrect or invalid. Please try again.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }
        //if the user not found with the email address provided
        else if( task instanceof FirebaseAuthInvalidUserException)
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "User not found. Please check the email address.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }
        //when the user already exists
        else if ( task instanceof FirebaseAuthUserCollisionException)
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Already user exists with this email. Please change the email.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }
        //wEmail verification is required. Please check your inbox for further instructions."
        else if ( task instanceof FirebaseAuthActionCodeException)
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Email verification is required. Please check your inbox for further instructions.",
                    "Authentication Failed",
                    "Go Back",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
        }
        else if( task instanceof FirebaseAuthEmailException )
        {
            String errorCode = ((FirebaseAuthEmailException) task).getErrorCode();
            if( errorCode.equals("ERROR_INVALID_EMAIL") )
            {
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                        "Email is invalid. Please enter correct email.",
                        "Failed To Sent Password Reset Email",
                        "Try Again",
                        null);
            }
            else if( errorCode.equals("ERROR_USER_NOT_FOUND") )
            {
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                        "User not found. Please check your email address.",
                        "Failed To Sent Password Reset Email",
                        "Try Again",
                        null);
            }
            else if( errorCode.equals("ERROR_USER_DISABLED") )
            {
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                        "Your account is disabled. Please contact in customer support.",
                        "Failed To Sent Password Reset Email",
                        "Go Back",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        });
            }
            else if( errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE") )
            {
                DialogsManager.basicAlert(Authentication_Page.this, "error",
                        "Already user exists with this email. Please change the email.",
                        "Authentication Failed",
                        "Try Again",
                        null);
            }
        }
        //when the network is disabled or not active
        else if ( task instanceof FirebaseNetworkException)
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Error while account authentication. Try checking your internet connection or try later.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }
        //when the unknown exception is occured possibly an auth exception then
        else
        {
            DialogsManager.basicAlert(Authentication_Page.this, "error",
                    "Unknown error occurred. Try checking your internet connection or try later.",
                    "Authentication Failed",
                    "Try Again",
                    null);
        }
    }

    /*
    * FIREBASE AUTHENTICATION POSSIBLE EXCEPTION CAUSED FROM METHODS
    * When working with Firebase Authentication, various types of errors can occur during different operations. Here are some common errors you may encounter along with their corresponding Firebase Authentication methods:
    *Invalid Email or Password:

    Method: signInWithEmailAndPassword(), createUserWithEmailAndPassword()
    Exception: FirebaseAuthInvalidCredentialsException
    Error Message: "Invalid email or password."
    User Not Found:

    Method: signInWithEmailAndPassword(), sendPasswordResetEmail()
    Exception: FirebaseAuthInvalidUserException
    Error Message: "User not found. Please check the email address."
    Account Already Exists:

    Method: createUserWithEmailAndPassword()
    Exception: FirebaseAuthUserCollisionException
    Error Message: "An account with this email already exists. Please sign in instead."
    Email Verification Required:

    Method: sendPasswordResetEmail()
    Exception: FirebaseAuthActionCodeException
    Error Message: "Email verification is required. Please check your inbox for further instructions."
    Network Error:

    Method: All Firebase Authentication methods
    Exception: FirebaseNetworkException
    Error Message: "Network not available. Please check your internet connection."
    Unknown Error:

    Method: All Firebase Authentication methods
    Exception: FirebaseAuthException
    Error Message: "An unknown error occurred. Please try again later."
    *
    *
    *
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //handling the return response from google activity intent
        if( requestCode == 1 )
        {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //fetching the targetted account in result
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);

                //throwing to firebase Local authentication function to verify and proceed the sign in process
                verifyEmailToForceOnlyOneAccount(account);
            }
            catch (ApiException authException)
            {
                FirebaseAuthenticationErrors(new FirebaseNetworkException(authException.toString()));
            }
        }
        else if( requestCode == 2 )
        {

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //pre configuration;
        Intent calledIntent = getIntent();
        intention = calledIntent.getStringExtra("intention");

        //post configuration
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);

        //fetching references
        debug = new Debugger(Authentication_Page.this, Toast.LENGTH_SHORT);
        topNav = findViewById(R.id.topAppBar_Auth);
        auth = FirebaseAuth.getInstance();
        loader = new LoaderActivator(this);

        //setting custom toolbar
        setSupportActionBar(topNav);

        //calling fragment switch
        if( intention != null )
            new Handler().postDelayed(fragmentSwitcher, 100);

        //setting listeners
        topNav.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( getSupportFragmentManager().getBackStackEntryCount() > 0 )
                {
                    getSupportFragmentManager().popBackStack();
                    return;
                }
                onBackPressed();
            }
        });

    }

    Handler.Callback fragmentAuthenticationHandler = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //for handling sign in and sign up procedures
            switch (message.getData().getString("FORM"))
            {
                case "sign_up":
                    loader.show();
                    firebaseSignUpAuthenticationWithEmailAndPassword(message.getData());
                    return true;
                case "sign_in":
                    loader.show();
                    firebaseSignInAuthenticationWithEmailAndPassword(message.getData());
                    return true;
                case "forgot_password":
                    loader.show();
                    firebaseForgotPasswordWithEmail(message.getData());
                    return true;
            }
            return false;
        }
    };

    Runnable fragmentSwitcher = new Runnable() {
        @Override
        public void run() {
            //loading fragments
            debug.show(intention);
            if(intention.equals("Sign Up") || intention.equals("Sign In") || intention.equals("Forgot Password")) {
                handler = new FragmentHandler(supportedFragments.get(intention), Authentication_Page.this, R.id.authContainer);
                handler.loadFragment("replace", false);
            }
            else
            {
                FirebaseUser user = auth.getCurrentUser();

                if( user != null ) //logged in
                {
                    auth.signOut();
                }
                else
                {
                    loader.show();
                    callingGoogleAuthentication();
                }
            }
        }
    };

    Handler.Callback callbackFragmentSwitcher = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            //for switching the fragments
            switch (message.getData().getString("purpose"))
            {
                case "sign_up":
                    intention = "Sign Up";
                    new Handler().postDelayed(fragmentSwitcher, 100);
                    return true;
                case "sign_in":
                    intention = "Sign In";
                    new Handler().postDelayed(fragmentSwitcher, 100);
                    return true;
                case "forgot_password":
                    intention = "Forgot Password";
                    new Handler().postDelayed(fragmentSwitcher, 100);
                    return true;
            }

            return false;
        }
    };
}
