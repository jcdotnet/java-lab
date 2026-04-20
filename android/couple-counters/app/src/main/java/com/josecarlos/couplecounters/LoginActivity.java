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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.josecarlos.couplecounters.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {

    private EditText textLogin1, textLogin2;
    private TextView  lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textLogin1 = (EditText) findViewById(R.id.login_partner1);
        textLogin2 = (EditText) findViewById(R.id.login_partner2);

        lblError = (TextView)findViewById(R.id.login_error);
    }

    public void loginUser(View view){
        final String partner1 = Utils.checkParameter(textLogin1.getText().toString());
        final String partner2 = Utils.checkParameter(textLogin2.getText().toString());

        if(Utils.isNotNull(partner1) && Utils.isNotNull(partner2)){

            RequestParams params = new RequestParams();
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://josecarlosroman.com/counters/" + partner1 + "/" + partner2, params ,new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                    Log.i("CC", "Login successful, navigating to MainActivity");

                    String name1 = null, name2 = null;
                    String[] countersName = new String [response.length()];
                    String[] countersType = new String [response.length()];
                    for (int i=0; i < response.length(); i++) {
                        try {
                            JSONObject jObject = response.getJSONObject(i);
                            name1 = jObject.getString("partner1");
                            name2 = jObject.getString("partner2");
                            countersName[i] = jObject.getString("counter");
                            countersType[i] = jObject.getString("common");
                        } catch (JSONException e) {
                            Log.e("CC", e.getMessage());
                        }
                    }

                    Intent data = new Intent();
                    data.putExtra("partner1", partner1);
                    data.putExtra("partner2", partner2);
                    data.putExtra("name1", name1);
                    data.putExtra("name2", name2);
                    data.putExtra("countersName", countersName);
                    data.putExtra("countersType", countersType);
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    Log.i("CC", "Login successful, navigating to MainActivity");

                    String name1 = null, name2 = null;
                    String[] countersName = new String[1];
                    String[] countersType = new String[1];
                    try {
                        name1 = response.getString("partner1");
                        name2 = response.getString("partner2");
                        countersName[0] = response.getString("counter");
                        countersType[0] = response.getString("common");
                    } catch (JSONException e) {
                        Log.e("CC", e.getMessage());
                    }

                    Intent data = new Intent();
                    data.putExtra("partner1", partner1);
                    data.putExtra("partner2", partner2);
                    data.putExtra("name1", name1);
                    data.putExtra("name2", name2);
                    data.putExtra("countersName", countersName);
                    data.putExtra("countersType", countersType);
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getApplicationContext(), statusCode + " Try again!!!", Toast.LENGTH_LONG).show();
                }

            });

        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }



    public void navigatetoRegisterActivity(View view){
        Intent loginIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

}
