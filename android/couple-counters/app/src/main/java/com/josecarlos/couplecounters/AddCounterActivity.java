package com.josecarlos.couplecounters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class AddCounterActivity extends Activity {

    private EditText textCounter, textValue1, textValue2;
    private TextView textPartner1, textPartner2;
    private CheckBox isCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_counter);

        textCounter = (EditText) findViewById(R.id.editCounterName);
        textPartner1 = (TextView) findViewById(R.id.textPartner1);
        textValue1 = (EditText) findViewById(R.id.editPartner1);
        textPartner2 = (TextView) findViewById(R.id.textPartner2);
        textValue2 = (EditText) findViewById(R.id.editPartner2);
        isCommon = (CheckBox) findViewById(R.id.checkCommon);

        isCommon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged (CompoundButton buttonView,boolean isChecked)
            {
                if (isChecked) {
                    textPartner1.setVisibility(View.INVISIBLE);
                    textValue2.setVisibility(View.INVISIBLE);
                    textPartner2.setVisibility(View.INVISIBLE);
                }
                else
                {
                    textValue2.setVisibility(View.VISIBLE);
                    textPartner2.setVisibility(View.VISIBLE);
                    textPartner1.setVisibility(View.VISIBLE);
                }

            }
        });
    }



    public void registerCounter(View view) {
        Bundle extras = getIntent().getExtras();
        final String counter = Utils.checkParameter(textCounter.getText().toString());
        if (extras != null && Utils.isNotNull(counter)) {
            final String partner1 = extras.getString("partner1");
            final String partner2 = extras.getString("partner2");
            Log.i("CC", "we're about to create a counter to: " + partner1 + " and " + partner2);
            final String v1 = Utils.checkParameter(textValue1.getText().toString());
            final String v2 = isCommon.isChecked()? v1 : Utils.checkParameter(textValue2.getText().toString());

            RequestParams params = new RequestParams();
            AsyncHttpClient client = new AsyncHttpClient();

            client.post("http://josecarlosroman.com/counters/" + partner1 + "/" + partner2 + "/" + counter + "/" + v1 + "/" + v2, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (isCommon.isChecked())
                        Utils.setCounterType(partner1, partner2, counter, "1");
                    Log.i("CC", "Entered onSuccess, counter created, http status code: " + statusCode);
                    Intent data = new Intent();
                    data.putExtra("counter", textCounter.getText().toString());
                    data.putExtra("common", isCommon.isChecked() ? "1" : "0");
                    setResult(RESULT_OK, data);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("CC", "Entered onFailure, counter not created, http status code: " + statusCode);
                    // if status code == 403
                    Toast.makeText(getApplicationContext(), statusCode + " Counter already exists!!!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }

            });

        }
        else
            Toast.makeText(getApplicationContext(), "Please enter a counter name", Toast.LENGTH_LONG).show();
    }

}
