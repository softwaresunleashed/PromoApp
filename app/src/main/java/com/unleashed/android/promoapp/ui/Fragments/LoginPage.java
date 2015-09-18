package com.unleashed.android.promoapp.ui.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.unleashed.android.promoapp.R;
import com.unleashed.android.promoapp.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";


    private static final String PERMISSION_ACCESS_TO_POST = "publish_actions";
    private static final String PERMISSION_ACCESS_TO_EMAIL = "email";
    private static final String PERMISSION_ACCESS_TO_PUBLICPROFILE = "public_profile";
    private static final String PERMISSION_ACCESS_TO_USER_FRIENDS = "user_friends";
    private static final String PERMISSION_ACCESS_TO_USER_STATUS = "user_status";


    private View rootView;

    private int mParam1;

    // Facebook Login Related Stuff
    private LoginButton FbLoginButton;
    private CallbackManager callbackManager;
    ////




    public static LoginPage newInstance(int section) {
        LoginPage fragment = new LoginPage();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, section);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_login_page, container, false);


        /////////////////////////
        FacebookSdk.sdkInitialize(rootView.getContext().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        ///////////////////////////
        FbLoginButton = (LoginButton)rootView.findViewById(R.id.login_button);
        FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_PUBLICPROFILE);
        //FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_EMAIL);
        //FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_EMAIL, PERMISSION_ACCESS_TO_PUBLICPROFILE);

        // If using in a fragment
        FbLoginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        FbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(rootView.getContext().getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(rootView.getContext().getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(rootView.getContext().getApplicationContext(), "Result:" + exception.toString(), Toast.LENGTH_LONG).show();
                exception.printStackTrace();
                // App code
            }
        });

        ////////////////////////

        // Initialize layout button
//        Button fbbutton = (Button) rootView.findViewById(R.id.btnFaceBookLogin);
//
//        fbbutton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Call private method
//                onFblogin();
//            }
//        });


        ////////////////////


        return rootView;

    }

    // Private method to handle Facebook login and callback
    private void onFblogin()
    {

        // Set permissions
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Toast.makeText(rootView.getContext().getApplicationContext(), "Result:" + loginResult, Toast.LENGTH_LONG).show();


                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d(Constants.APP_NAME_TAG, "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(Constants.APP_NAME_TAG, error.toString());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If you have reached here, the FB login activity has been returned with result = success / failure.
        // Forward the Login details (Success / Failure) to FB CallbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }
}
