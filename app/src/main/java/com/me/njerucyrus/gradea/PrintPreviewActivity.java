package com.me.njerucyrus.gradea;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PrintPreviewActivity extends AppCompatActivity {

    Button btnPrint;
    private int itemId;
    private TextView mPayeeName, mPhoneNumber, mDescription, mAuthorisedBy,
            mReceiptNo, mProducts, mPrice, mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mPayeeName = (TextView) findViewById(R.id.mPayeeName);
        mPhoneNumber = (TextView) findViewById(R.id.mPhoneNumber);
        mDescription = (TextView) findViewById(R.id.mDescription);
        mAuthorisedBy = (TextView) findViewById(R.id.mAuthorisedBy);
        mReceiptNo = (TextView) findViewById(R.id.mReceiptNo);
        mProducts = (TextView) findViewById(R.id.mProducts);
        mPrice = (TextView) findViewById(R.id.mPrice);
        mDate = (TextView) findViewById(R.id.mDate);

        SharedPreferences settings = getSharedPreferences("PRINT_DATA",
                Context.MODE_PRIVATE);

        itemId = settings.getInt("id", 0);

        mPayeeName.setText(settings.getString("payee_name", ""));
        mPhoneNumber.setText(settings.getString("phone_number", ""));
        mDescription.setText(settings.getString("description", ""));
        mAuthorisedBy.setText(settings.getString("authorised_by", ""));
        mReceiptNo.setText(settings.getString("receipt_no", ""));
        mProducts.setText(settings.getString("product_names", ""));
        mPrice.setText(settings.getString("total_price", ""));
        mDate.setText(settings.getString("date", ""));


        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemId > 0) {
                    Toast.makeText(getApplicationContext(), "Comming soon", Toast.LENGTH_LONG).show();
                }

            }
        });
        // Enable the Up button

    }

}
