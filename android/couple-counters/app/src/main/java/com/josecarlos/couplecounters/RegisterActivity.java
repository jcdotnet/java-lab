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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.josecarlos.couplecounters.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends Activity {


    private EditText textPartner11, textPartner12, textPartner21, textPartner22;
    private TextView lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textPartner11 = (EditText) findViewById(R.id.register_partner11);
        textPartner12 = (EditText) findViewById(R.id.register_partner12);

        textPartner21 = (EditText) findViewById(R.id.register_partner21);
        textPartner22 = (EditText) findViewById(R.id.register_partner22);

        lblError = (TextView)findViewById(R.id.login_error);
    }

    public void registerUser(View view){
        String p1UserName = textPartner11.getText().toString();
        String p2UserName = textPartner21.getText().toString();
        RequestParams params = new RequestParams();

        if(isNotNull(p1UserName) && isNotNull(p2UserName)){
            insertUser1(params);
        } else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    public void insertUser1(RequestParams params){
        final String p1UserName = Utils.checkParameter(textPartner11.getText().toString());
        String p1DisplayName = Utils.checkParameter(textPartner12.getText().toString());

        AsyncHttpClient client = new AsyncHttpClient();

        client.post("http://josecarlosroman.com/counters/" + p1UserName + "/" + p1DisplayName + "/user", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                insertUser2();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 400)
                    Toast.makeText(getApplicationContext(), statusCode + " user name contains not valid characters!!!", Toast.LENGTH_LONG).show();
                else if (statusCode == 406)
                    Toast.makeText(getApplicationContext(), statusCode + " user name or display name contains too many characters!!!", Toast.LENGTH_LONG).show();
                else
                    insertUser2();

            }

        });

    }

    public void insertUser2() {

        final String p2UserName = Utils.checkParameter(textPartner21.getText().toString());
        String p2DisplayName = Utils.checkParameter(textPartner22.getText().toString());

        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://josecarlosroman.com/counters/" + p2UserName + "/" + p2DisplayName + "/user", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                insertCouple();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 400)
                    Toast.makeText(getApplicationContext(), statusCode + " user name contains not valid characters!!!", Toast.LENGTH_LONG).show();
                else if (statusCode == 406)
                    Toast.makeText(getApplicationContext(), statusCode + " user name or display name contains too many characters!!!", Toast.LENGTH_LONG).show();
                else
                    insertCouple();
            }


        });
    }


    public void insertCouple() {
        final String p1UserName = Utils.checkParameter(textPartner11.getText().toString());
        final String p2UserName = Utils.checkParameter(textPartner21.getText().toString());

        RequestParams params = new RequestParams();
        AsyncHttpClient clientCouple = new AsyncHttpClient();
        clientCouple.post("http://josecarlosroman.com/counters/" + p1UserName + "/" + p2UserName + "/couple", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(), statusCode + " Created!!!", Toast.LENGTH_LONG).show();
                    navigatetoLoginActivity();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // if status code == 403
                    Toast.makeText(getApplicationContext(), statusCode + " Couple already exists!!!", Toast.LENGTH_LONG).show();
                }

        });

    }

    public void navigatetoLoginActivity(){
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);

        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    private boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }


}
