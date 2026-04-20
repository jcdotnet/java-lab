
/*
 * Copyright 2015 Jose Carlos Román Rubio (jcdotnet@hotmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.josecarlos.couplecounters;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.josecarlos.couplecounters.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends ListActivity {

    private static final int LOGIN_REQUEST = 0;
    private static final int COUNTER_REQUEST = 1;
    private static final int EDIT_REQUEST = 2;
    private static final String FILE_NAME = "MainActivityData.txt";

    private ListAdapter listAdapter;
    private int currentCounter;
    private String currentPartner1, currentPartner2, namePartner1, namePartner2;

    /*
     * Created by Jose Carlos on 30/10/2015.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter = new ListAdapter(getApplicationContext());
        setListAdapter(listAdapter);

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentCounter = position;
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                intent.putExtra("partner1", currentPartner1);
                intent.putExtra("partner2", currentPartner2);
                intent.putExtra("name1", namePartner1);
                intent.putExtra("name2", namePartner2);
                intent.putExtra("counter", listAdapter.getItem(position).getCounterName());
                intent.putExtra("common", listAdapter.getItem(position).isCommon());
                startActivityForResult(intent, EDIT_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("CC", "Entered onResume()");
        if (listAdapter.getCount() == 0) {
            loadCounters();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("CC", "Entered onPause()");
        if (currentPartner1 != null)
            saveCounters();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("CC", "Entered onActivityResult()");

        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            currentPartner1 = data.getStringExtra("partner1");
            currentPartner2 = data.getStringExtra("partner2");
            namePartner1 = data.getStringExtra("name1");
            namePartner2 = data.getStringExtra("name2");
            Log.i("CC", currentPartner1 + " " + currentPartner2);
            String[] countersName = data.getStringArrayExtra("countersName");
            String[] countersType = data.getStringArrayExtra("countersType");
            for (int i=0; i< countersName.length; i++)
            {
                Log.i("CC", countersName[i]);
                listAdapter.add(new CounterItem(countersName[i], "1".equals(countersType[i])));
            }
        }
        else if (requestCode == COUNTER_REQUEST && resultCode == RESULT_OK) {
            String newCounter = data.getStringExtra("counter");
            String commonCounter= data.getStringExtra("common");
            listAdapter.add(new CounterItem(newCounter, "1".equals(commonCounter)));
        }

        else if (requestCode == EDIT_REQUEST && resultCode == RESULT_OK) {
            CounterItem item = listAdapter.getItem(currentCounter);
            item.setCounterName(data.getStringExtra("counter"));
            listAdapter.set(currentCounter, item);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_counter) {
            Intent intentCounter = new Intent(MainActivity.this, AddCounterActivity.class);
            intentCounter.putExtra("partner1", currentPartner1);
            intentCounter.putExtra("partner2", currentPartner2);
            startActivityForResult(intentCounter, COUNTER_REQUEST);
        }
        else if (id == R.id.action_refresh) {
            RequestParams params = new RequestParams();
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://josecarlosroman.com/counters/" + currentPartner1 + "/" + currentPartner2, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    listAdapter.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jObject = response.getJSONObject(i);
                            listAdapter.add(new CounterItem(jObject.getString("counter"),"1".equals(jObject.getString("common"))) );
                        } catch (JSONException e) {
                            Log.e("CC", e.getMessage());
                        }
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    listAdapter.clear();

                    try {
                        listAdapter.add(new CounterItem(response.getString("counter"), "1".equals(response.getString("common"))) );
                    } catch (JSONException e) {
                        Log.e("CC", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);

                }
            });

        } else if (id == R.id.action_log_out) {
            File dir = getFilesDir();
            File file = new File(dir, FILE_NAME);
            file.delete();
            listAdapter.clear();
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intentLogin, LOGIN_REQUEST);

        } else if (id == R.id.action_about) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://josecarlosroman.com"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_counter, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.counter_change);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (listAdapter.getItem(info.position).isCommon())
        {
            item.setTitle("make individual counter");
        }
        else
            item.setTitle("make common counter");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.counter_delete){
            Log.i("CC", "Entered onContextItemSelected - delete");
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String counter = Utils.checkParameter(listAdapter.getItem(info.position).getCounterName());

            RequestParams params = new RequestParams();
            AsyncHttpClient client = new AsyncHttpClient();
            client.delete("http://josecarlosroman.com/counters/" + currentPartner1 + "/" + currentPartner2 + "/" + counter, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("CC", "Entered onSuccess, counter deleted");
                    listAdapter.remove(listAdapter.getItem(info.position));

                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("CC", "counter not deleted");
                }

            });
        }
        else if (id == R.id.counter_change) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            CounterItem counterItem= listAdapter.getItem(info.position);
            if (counterItem.isCommon())
            {
                Utils.setCounterType(currentPartner1, currentPartner2, Utils.checkParameter(counterItem.getCounterName()),"0");
                counterItem.setCommon(false);
            }
            else
            {
                Utils.setCounterType(currentPartner1, currentPartner2, Utils.checkParameter(counterItem.getCounterName()),"1");
                counterItem.setCommon(true);
            }
            listAdapter.set(info.position, counterItem);
        }
        return super.onContextItemSelected(item);
    }

    // load stored counters
    private void loadCounters() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String counterName = null;
            boolean counterType = false;

            String partner1 = reader.readLine();

            if ( partner1== null)
            {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intentLogin, LOGIN_REQUEST);
            }

            else //if (partner1 == currentPartner1)
            {
                currentPartner1 = partner1;
                currentPartner2 = reader.readLine();
                namePartner1 = reader.readLine();
                namePartner2 = reader.readLine();
                while (null != (counterName = reader.readLine())) {
                    counterType = Boolean.parseBoolean(reader.readLine());
                    listAdapter.add(new CounterItem(counterName, counterType));
                }
            }
            Log.i("loadCounters", currentPartner1 + " "+ currentPartner2);

        } catch (IOException e) {
            Log.e("CC", "Can not read file: " + e.getMessage());
            if (currentPartner1==null) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intentLogin, LOGIN_REQUEST);
            }

        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Save couple and counters to file
    private void saveCounters() {

        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            writer.println(currentPartner1);
            writer.println(currentPartner2);
            writer.println(namePartner1);
            writer.println(namePartner2);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                writer.println(listAdapter.getItem(i));
            }
        } catch (IOException e) {
            Log.e("CC", "Can not write to file: " + e.getMessage());
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
