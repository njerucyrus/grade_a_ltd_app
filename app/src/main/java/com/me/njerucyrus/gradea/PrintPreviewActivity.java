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
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private RecyclerItem item;
    private TextView mPayeeName, mPhoneNumber, mDescription, mAuthorisedBy,
            mReceiptNo, mProducts, mPrice, mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        SharedPreferences settings = getSharedPreferences("PRINT_DATA",
                Context.MODE_PRIVATE);

        itemId = settings.getInt("id", 0);

        requestQueue = VolleyRequestSingleton.getInstance(this).getRequestQueue();


        mPayeeName = (TextView) findViewById(R.id.mPayeeName);
        mPhoneNumber = (TextView) findViewById(R.id.mPhoneNumber);
        mDescription = (TextView) findViewById(R.id.mDescription);
        mAuthorisedBy = (TextView) findViewById(R.id.mAuthorisedBy);
        mReceiptNo = (TextView) findViewById(R.id.mReceiptNo);
        mProducts = (TextView) findViewById(R.id.mProducts);
        mPrice = (TextView) findViewById(R.id.mPrice);
        mDate = (TextView) findViewById(R.id.mDate);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        item = new RecyclerItem();
        final String URL = "http://grade.hudutech.com/api_backend/api/purchases.php?filter=non&id=" + itemId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getInt("status_code") == 200) {
                                JSONObject object = response.getJSONObject("data");
                                item.setId(object.getInt("id"));
                                item.setReceiptNo("Receipt No: "+object.getString("receipt_no"));
                                item.setPhoneNumber("Phone Number: "+object.getString("phone_number"));
                                item.setAuthorisedBy("Authorised By: "+object.getString("authorised_by"));
                                item.setVatNo("V.A.T NO: "+object.getString("vat_no"));
                                item.setKraPin("K.R.A PIN NO: "+object.getString("kra_pin_no"));
                                item.setPayeeName("Payee Name: " +object.getString("payee_name"));
                                item.setProducts("Products : "+object.getString("product_names"));
                                item.setDescription("Description: "+object.getString("payment_description"));
                                item.setPrice("Total Price: "+object.getString("amount_paid"));
                                item.setDate("Date Paid: "+object.getString("date_paid"));


                                mPayeeName.setText(item.getPayeeName());
                                mPhoneNumber.setText(item.getPhoneNumber());
                                mDescription.setText(item.getDescription());
                                mAuthorisedBy.setText(item.getAuthorisedBy());
                                mReceiptNo.setText(item.getReceiptNo());
                                mProducts.setText(item.getProducts());
                                mPrice.setText(item.getPrice());
                                mDate.setText(item.getDate());

                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to fetch details try again", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof NoConnectionError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();


                    }
                }
        );
        requestQueue.add(req);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });


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
