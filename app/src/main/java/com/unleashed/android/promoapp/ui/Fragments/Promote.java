package com.unleashed.android.promoapp.ui.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.unleashed.android.promoapp.R;
import com.unleashed.android.promoapp.constants.Constants;
import com.unleashed.android.promoapp.databases.TemplatesDB;
import com.unleashed.android.promoapp.ui.SelectTemplate;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class Promote extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;

    private Context mContext;

    private TemplatesDB templatesDB;    // DB handle to store Templates

    private EditText editText_newFileInput;

    private String mesg;
    private String title;
    private String prodLink;


    private final int RC_OPEN_TEMPLATES_ACTIVITY = 10;

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {

            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
//            if (result.getPostId() != null) {
//                String title = getString(R.string.success);
//                String id = result.getPostId();
//                String alertMessage = getString(R.string.successfully_posted_post, id);
//                showResult(title, alertMessage);
//            }


                showStoreTemplateMessageBox(rootView, "Save This Promotional Activity As : ");

        }

        private void showResult(String title, String alertMessage) {

            //Toast.makeText(rootView.getContext().getApplicationContext(), title + "\n" + alertMessage, Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(rootView.getContext())
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private void showStoreTemplateMessageBox(View rootView, String message) {




        // When creating a Dialog inside a fragment, take the context of Fragment.
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());  //getActivity().getApplicationContext()  // MainActivity.this
        builder.setIcon(R.drawable.ic_launcher);
        builder.setCancelable(true);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);

        // Adding a text box to Dialog Box
        editText_newFileInput = new EditText(rootView.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText_newFileInput.setLayoutParams(lp);
        editText_newFileInput.setText("Enter Template Name");
        builder.setView(editText_newFileInput);

//        // Do Not Show Again button
//        builder.setPositiveButton(R.string.open_recordings, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//
////                File file = new File(completeFilePath);
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////
////                intent.setDataAndType(Uri.fromFile(file), "video/*");
////                startActivity(intent);
//            }
//        });

        // Skip File Rename Dialog box
        builder.setNeutralButton(R.string.skip_always, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // Save Template
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Get the new filename from the text box
                String templateNickName = editText_newFileInput.getText().toString();

                Cursor cur = templatesDB.retrieveRecord(templateNickName);
                if(cur == null){
                    // This means we dont have a record with the present nick name.
                    // So, we can add this record to DB.
                    templatesDB.insertRecord(templateNickName, title, mesg, prodLink);
                }



                // Stay in the app, dont have to do anything
                dialogInterface.dismiss();

            }
        });

        try{

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            Log.e(Constants.APP_NAME_TAG, "Promote.java: StoreTemplateMessageBox() caught exception.");
            ex.printStackTrace();
        }
    }


    public static Promote newInstance(int section) {
        Promote fragment = new Promote();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, section);
        fragment.setArguments(args);
        return fragment;
    }

    public Promote() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_promote, container, false);

        // Keep Application context handy, useful to DB creation.
        mContext = rootView.getContext().getApplicationContext();

        // Initialize the DB
        initializeDB(mContext);

        Button btnWriteOnMyWall = (Button)rootView.findViewById(R.id.btnWriteOnMyTimeline);
        btnWriteOnMyWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStatusUpdate(rootView);
            }
        });


        Button btnLoadFromTemplate = (Button)rootView.findViewById(R.id.btnLoadTemplate);
        btnLoadFromTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadTemplateActivity = new Intent(rootView.getContext(), SelectTemplate.class);
                startActivityForResult(intentLoadTemplateActivity, RC_OPEN_TEMPLATES_ACTIVITY);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RC_OPEN_TEMPLATES_ACTIVITY:


                break;
        }

    }

    private void initializeDB(Context context) {
        // Create Database during
        templatesDB = new TemplatesDB(context);
    }

    private void postStatusUpdate(View rootView) {

        EditText etTitle = (EditText)rootView.findViewById(R.id.et_title);
        EditText etMessage = (EditText)rootView.findViewById(R.id.et_message);
        EditText etProductLink = (EditText)rootView.findViewById(R.id.et_product_link);


        mesg = etMessage.getText().toString();           //"Watch Out the Products section. \n www.softwaresunleashed.com";
        title = etTitle.getText().toString();            //"Softwares Unleashed Promotion via PromoApp (Android App)";
        prodLink = etProductLink.getText().toString();   // Link of page that is being shared on time line



        if(mesg.isEmpty() || title.isEmpty() || prodLink.isEmpty()){
            Toast.makeText(rootView.getContext().getApplicationContext(), "Fill-in the fields before you post.", Toast.LENGTH_LONG).show();
            return;
        }

        //ShareDialog shareDialog = new ShareDialog(this);
        Profile profile = Profile.getCurrentProfile();
        if(profile == null){
            // Not Logged in.
            Toast.makeText(rootView.getContext().getApplicationContext(), "No User Logged In. Please Login Before You Post.", Toast.LENGTH_LONG).show();

            // Code to move back to login tab..
            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
            viewPager.setCurrentItem(Constants.Tabs.LOGIN_TAB, true);
            return;
        }


        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(mesg)
                .setContentUrl(Uri.parse(prodLink))
                //.setPeopleIds()
                .build();

        //Check if App has Publish Permission, if not, try to request Publish Permissions from user
        if(!hasPublishPermission()){
            try{
                requestPublishPermissions();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        if (profile != null && hasPublishPermission()) {
            // Finally Share the Post if u have th required permissions (obtained above)
            ShareApi.share(linkContent, shareCallback);
        }


    }


    private void requestPublishPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.EVERYONE)
                .logInWithPublishPermissions(this, Arrays.asList(Constants.PERMISSION_ACCESS_TO_POST));
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

}
