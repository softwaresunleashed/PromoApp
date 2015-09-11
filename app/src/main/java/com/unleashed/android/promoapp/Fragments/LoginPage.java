package com.unleashed.android.promoapp.Fragments;


import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.unleashed.android.promoapp.R;
import com.facebook.*;

public class LoginPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

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

        View rootView = inflater.inflate(R.layout.fragment_login_page, container, false);


        /////////////////////////
        FacebookSdk.sdkInitialize(rootView.getContext().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        FbLoginButton = (LoginButton)rootView.findViewById(R.id.login_button);
        FbLoginButton.setReadPermissions("user_friends");
        // If using in a fragment
        FbLoginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        FbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        ////////////////////////


        return rootView;

    }


}
