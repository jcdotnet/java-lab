package com.josecarlos.couplecounters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.josecarlos.couplecounters.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class CounterActivity extends Activity {

    private String oldCounterName;
    private boolean rename, isCommon;
    private EditText counterName;
    private ImageView image1, image2;
    private TextView value1, value2, textName1, textName2;
    private String partner1, partner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        isCommon = getIntent().getExtras().getBoolean("common");

        counterName = (EditText) findViewById(R.id.textCounter);
        value1 = (TextView) findViewById(R.id.textValue1);
        value2 = (TextView) findViewById(R.id.textValue2);
        textName1 = (TextView) findViewById(R.id.textName1);
        textName2 = (TextView) findViewById(R.id.textName2);
        image1 = (ImageView) findViewById(R.id.imagePartner1);
        image2 = (ImageView) findViewById(R.id.imagePartner2);

        partner1 = getIntent().getExtras().getString("partner1");
        partner2 = getIntent().getExtras().getString("partner2");
        textName1.setText(getIntent().getExtras().getString("name1"));
        textName2.setText(getIntent().getExtras().getString("name2"));

        counterName.setText(getIntent().getExtras().getString("counter"));
        oldCounterName = counterName.getText().toString();

        Utils.setInitial(image1, textName1.getText().toString(), 1);
        Utils.setInitial(image2, textName2.getText().toString(), 1);

        final String counterParam = Utils.checkParameter(counterName.getText().toString());

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get("http://josecarlosroman.com/counters/" + partner1 + "/" + partner2 + "/" + counterParam, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                Log.i("CC", "Entered onSuccess!!! Retrieving counter values");

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jObject = response.getJSONObject(i);;
                        value1.setText(jObject.getString("value_partner1"));
                        value2.setText(jObject.getString("value_partner2"));
                    } catch (JSONException e) {
                        Log.e("CC", e.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.i("CC", "Entered onSuccess!!! Retrieving counter values");

                try {
                    value1.setText(response.getString("value_partner1"));
                    value2.setText(response.getString("value_partner2"));
                } catch (JSONException e) {
                    Log.e("CC", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i("CC", "Entered onFailure :( http status code: " + statusCode);
                Toast.makeText(getApplicationContext(), statusCode + " Counter not loaded!", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void buttonInc1Click(View view) {
        int value = Integer.parseInt(value1.getText().toString());
        value1.setText(Integer.toString(++value));
        if (isCommon)
            value2.setText(Integer.toString(value));
        rename = false;
        updateCounter();
    }

    public void buttonDec1Click(View view) {
        int value = Integer.parseInt(value1.getText().toString());
        if (value > 0)
        {
            value1.setText(Integer.toString(--value));
            if (isCommon)
                value2.setText(Integer.toString(value));
            rename = false;
            updateCounter();
        }
    }

    public void buttonInc2Click(View view) {
        int value = Integer.parseInt(value2.getText().toString());
        value2.setText(Integer.toString(++value));
        if (isCommon)
            value1.setText(Integer.toString(value));
        rename = false;
        updateCounter();
    }

    public void buttonDec2Click(View view) {
        int value = Integer.parseInt(value2.getText().toString());
        if (value > 0)
        {
            value2.setText(Integer.toString(--value));
            if (isCommon)
                value1.setText(Integer.toString(value));
            rename = false;
            updateCounter();
        }
    }

    public void buttonRenameClick(View view)
    {
        rename = true;
        updateCounter();
    }

    private void updateCounter() {
        Log.i("CC", "entered updateCounter()");
        final String oc = Utils.checkParameter(oldCounterName);
        final String c = Utils.checkParameter(counterName.getText().toString());
        final String v1 = Utils.checkParameter(value1.getText().toString());
        final String v2 = Utils.checkParameter(value2.getText().toString());

        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = null;
        if (rename) {
            if (Utils.isNotNull(c))
                url = "http://josecarlosroman.com/counters/" + partner1 + "/" + partner2 + "/" + oc + "/" + c;
            else {
                Toast.makeText(getApplicationContext(), "Please enter a counter name", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            url = "http://josecarlosroman.com/counters/" + partner1 + "/" + partner2 + "/" + oc + "/" + oc + "/" + v1 + "/" + v2;
        }

        client.put(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("CC", "Entered onSuccess, counter updated");
                if (rename) {
                    oldCounterName = counterName.getText().toString();
                    Intent data = new Intent();
                    data.putExtra("counter", oldCounterName);
                    setResult(RESULT_OK, data);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("CC", "counter not updated");
            }

        });
    }

}
