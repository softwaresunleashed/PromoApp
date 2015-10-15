package com.unleashed.android.promoapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.unleashed.android.promoapp.R;
import com.unleashed.android.promoapp.constants.Constants;
import com.unleashed.android.promoapp.databases.TemplatesDB;
import com.unleashed.android.promoapp.expandablelistview.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectTemplate extends AppCompatActivity {

    // DB to store Templates.
    private TemplatesDB templatesDB;
    private Context mContext;


    // Expandable List View components
    private ExpandableListAdapter listAdapter;
    private ExpandableListView exLV;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int refresh_list_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_template);

        // Keep application context handy.
        mContext = getApplicationContext();

        //  Initialize the DB with Application context.
        initializeDB(mContext);

        exLV = (ExpandableListView)findViewById(R.id.expandableListView_templates);
        exLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });

        // populate the expandable list
        refresh_templates_list();

    }

    private void initializeDB(Context context) {
        // Create Database during
        templatesDB = new TemplatesDB(context);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_template, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int refresh_templates_list(){

        int index =0;       // Keep track of number of records

        try {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();



            // Get all templates
            Cursor cur = templatesDB.retrieveAllRecords();
            if(cur == null){
                Toast.makeText(mContext, R.string.no_records_found, Toast.LENGTH_LONG).show();
                return 0;
            }
            if (cur.moveToFirst()) {


                do {
                    String nickName = cur.getString(1);                 // Nick Name
                    String promoTitle = cur.getString(2);               // Title
                    String promoDescription = cur.getString(3);         // Product Description
                    String promoLink = cur.getString(4);                // Product Link

                    // preparing list data
                    prepareListData(index, nickName, promoTitle, promoDescription, promoLink);

                    index++;


                }
                while (cur.moveToNext());

                // Create the list adapter
                listAdapter = new ExpandableListAdapter(SelectTemplate.this, exLV, listDataHeader, listDataChild);

                // setting list adapter
                exLV.setAdapter(listAdapter);
            }


        } catch (Exception ex) {
            Log.e(Constants.APP_NAME_TAG, "SelectTemplate.java:refresh_list() caught exception");
            ex.printStackTrace();
        }

        return index;

    }

    private void prepareListData(int index, String templateNickName, String promoTitle, String promoDescription, String promoLink) {



        // Adding child data
        listDataHeader.add(templateNickName);

        // Adding sub-child data
        List<String> sub_child = new ArrayList<String>();
        sub_child.add(promoTitle);
        sub_child.add(promoDescription);
        sub_child.add(promoLink);


        listDataChild.put(listDataHeader.get(index), sub_child);

    }
}
