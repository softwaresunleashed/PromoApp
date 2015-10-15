package com.unleashed.android.promoapp.ui.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.unleashed.android.promoapp.R;
import com.unleashed.android.promoapp.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;

public class LoginPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;


    // Facebook Login Related Stuff
    private LoginButton FbLoginButton;
    private Button fbSharebutton;
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

        FbLoginButton.setReadPermissions(Constants.PERMISSION_ACCESS_TO_PUBLICPROFILE);

//        //Array.asList(PERMISSION_ACCESS_TO_PUBLICPROFILE, PERMISSION_ACCESS_TO_POST)
//        ArrayList<String> permissions = new ArrayList<String>();
//        permissions.add(PERMISSION_ACCESS_TO_PUBLICPROFILE);
//        permissions.add(PERMISSION_ACCESS_TO_POST);

        //FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_POST);

        //FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_EMAIL);
        //FbLoginButton.setReadPermissions(PERMISSION_ACCESS_TO_EMAIL, PERMISSION_ACCESS_TO_PUBLICPROFILE);


        // If using in a fragment
        FbLoginButton.setFragment(this);
        // Other app specific specialization

        // Login Callback registration
        FbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //fbSharebutton.setVisibility(View.VISIBLE);

                //updateFBProfileInfo(rootView);
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
        fbSharebutton = (Button) rootView.findViewById(R.id.btnShare);
        fbSharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Code to mode to another tab.
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(Constants.Tabs.PROMOTE_TAB, true);

            }
        });


        //ShareDialog shareDialog = new ShareDialog(this);
        final Profile profile = Profile.getCurrentProfile();
//        if(profile == null){
//            // User is Not Logged In. No Need to show  Sharing button.
//            fbSharebutton.setVisibility(View.GONE);
//        }

        // Update FB login info.
        updateFBProfileInfo(rootView, profile);

        // Track Login / Logout events.
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                updateFBProfileInfo(rootView, profile);
//
//                if (currentAccessToken == null){
//                    //User logged out
//                    fbSharebutton.setVisibility(View.GONE);
//
//                }else{
//                    // User logged in
//                    fbSharebutton.setVisibility(View.VISIBLE);
//                }

            }
        };



        Button testBtn = (Button)rootView.findViewById(R.id.btnTest);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findFriends();

                //findGroups();


                if(false){
                    postMessagesToMyself();

                }


            }
        });

        ////////////////////


        return rootView;

    }

    private void updateFBProfileInfo(View rootView, Profile profile) {

        final ImageView iv_user_profile = (ImageView)rootView.findViewById(R.id.fbUserImage);
        TextView tv_user_name = (TextView)rootView.findViewById(R.id.fbUserName);

        //Profile profile  = Profile.getCurrentProfile();

        if(profile == null){
            // No User logged in.
            fbSharebutton.setVisibility(View.GONE);

            iv_user_profile.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

            tv_user_name.setText(R.string.no_user_logged_in);

        }else{
            // User is logged in.
            fbSharebutton.setVisibility(View.VISIBLE);

            final Uri imgUri = profile.getProfilePictureUri(R.dimen.user_image_width, R.dimen.user_image_height);
            new LoadProfileImage(iv_user_profile).execute(imgUri.toString());

            tv_user_name.setText(profile.getName());
        }





    }


    private void postMessagesToMyself(){

        /*
          POST graph.facebook.com
          /{user-id}/feed?
            message={message}&
            access_token={access-token}
        * */

        // You can publish posts by
        // using the
        //           /{user-id}/feed,
        //           /{page-id}/feed,
        //           /{event-id}/feed, or
        //           /{group-id}/feed edges.


        // To post on my own timeline
        final String msg = "www.softwaresunleashed.com";
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/128432974174374/feed?message="+msg,
                null,
                HttpMethod.POST,
                new GraphRequest.Callback(){
                    @Override
                    public void onCompleted(GraphResponse response) {
                        response.toString();
                        JSONObject ja = response.getJSONObject();
                    }
                }

        ).executeAsync();
    }

                /* make the API call */
//                new GraphRequest(
//                        AccessToken.getCurrentAccessToken(),
//                        "/me",
//                        null,
//                        HttpMethod.GET,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                /* handle the result */
//                                response.toString();
//                            }
//                        }
//                ).executeAsync();


    private void findGroups(){
        String myUserId = "128432974174374";


        // To get list of all groups a user belongs to.
                /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + myUserId + "/groups",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                /* handle the result */
                        //response.toString();
                       // JSONObject ja = response.getJSONObject();



//                                final JSONObject obj = new JSONObject(response);
//                                final JSONArray data = response.getJSONArray("data");
//                                final int n = data.length();
//                                for (int i = 0; i < n; ++i) {
//                                    final JSONObject person = data.getJSONObject(i);
//                                    System.out.println(person.getInt("id"));
//                                    System.out.println(person.getString("name"));
//                                    System.out.println(person.getString("gender"));
//                                    System.out.println(person.getDouble("latitude"));
//                                    System.out.println(person.getDouble("longitude"));
//                                    System.out.println(person.getDouble("longitude"));

                    }
                }
        ).executeAsync();

    }

    private void requestUserFriendsPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.EVERYONE)
                .logInWithPublishPermissions(this, Arrays.asList(Constants.PERMISSION_ACCESS_TO_USER_FRIENDS));
    }

    private boolean hasUserFriendsPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("user_friends");
    }
    private void findFriends(){

        String myUserId = "128432974174374";



        // Needs "user_friends" permission
        if(!hasUserFriendsPermission()){
            requestUserFriendsPermissions();
        }

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + myUserId + "/friends",     //"/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        JSONArray jsonArray = response.getJSONArray();

                        final JSONObject obj;
                        try {
                            obj = new JSONObject(response.getRawResponse());
                            final JSONArray data = obj.getJSONArray("data");
                            final int n = data.length();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
        ).executeAsync();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If you have reached here, the FB login activity has been returned with result = success / failure.
        // Forward the Login details (Success / Failure) to FB CallbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

//    // Private method to handle Facebook login and callback
//    private void onFblogin()
//    {
//
//        // Set permissions
//        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//
//                        Toast.makeText(rootView.getContext().getApplicationContext(), "Result:" + loginResult, Toast.LENGTH_LONG).show();
//
//
//                        GraphRequest.newMeRequest(
//                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(JSONObject json, GraphResponse response) {
//                                        if (response.getError() != null) {
//                                            // handle error
//                                            System.out.println("ERROR");
//                                        } else {
//                                            System.out.println("Success");
//                                            try {
//
//                                                String jsonresult = String.valueOf(json);
//                                                System.out.println("JSON Result" + jsonresult);
//
//                                                String str_email = json.getString("email");
//                                                String str_id = json.getString("id");
//                                                String str_firstname = json.getString("first_name");
//                                                String str_lastname = json.getString("last_name");
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//
//                                }).executeAsync();
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d(Constants.APP_NAME_TAG, "On cancel");
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Log.d(Constants.APP_NAME_TAG, error.toString());
//                    }
//                });
//    }


}

