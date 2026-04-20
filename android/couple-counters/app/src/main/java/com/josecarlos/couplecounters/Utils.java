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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jose Carlos on 13/11/2015.
 */
public class Utils {

    final static int CANVAS_WIDTH = 66;
    final static int CANVAS_HEIGHT = 66;

    public static void setInitial (ImageView image, String text, int letters)
    {

        Paint myPaint = new Paint();
        //myPaint.getTextBounds (text, 0, 1, bounds);
        myPaint.setARGB(255, 255, 191, 223); // myPaint.setARGB(255, 255, 190, 26); //
        myPaint.setTextSize(40);
        // myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setTypeface(Typeface.DEFAULT); // MONOSPACE);
        myPaint.setTextAlign(Paint.Align.CENTER);

        int textWidth = (int)myPaint.measureText(text.substring(0, letters));

        Bitmap img = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(img);

        int x = (canvas.getWidth() / 2);
        int y = (int) ((canvas.getHeight() / 2) - ((myPaint.descent() + myPaint.ascent()) / 2));

        canvas.drawText(text.substring(0, letters).toUpperCase(), x, y, myPaint);
        image.setImageBitmap(img);
    }

    public static void setCounterType(String partner1, String partner2, String counter, String type)
    {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();

        client.put("http://josecarlosroman.com/counters/" + partner1 + "/" + partner2 + "/" + counter + "/" + counter + "/" + type, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("CC", "Entered onSuccess, counter type changed, http status code: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("CC", "Entered onFailure, counter type not created, http status code: " + statusCode);
            }

        });
    }

    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }

    public static String checkParameter(String parameter)
    {
        try {
            parameter = URLEncoder.encode(parameter, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("CC", "encoder exception");
        }
        return parameter;
    }
}
