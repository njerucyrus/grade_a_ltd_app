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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class RecordPurchase extends AppCompatActivity {
    RequestQueue requestQueue;
    Button btnRecordPurchase;
    EditText txtPayeeName, txtPayeePhoneNumber, txtDescription,
            txtProductNames, txtPrice, txtMpesaID;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_purchase);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);


        txtPayeeName = (EditText) findViewById(R.id.txtPayeeName);
        txtPayeePhoneNumber = (EditText) findViewById(R.id.txtPayeePhoneNumber);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtProductNames = (EditText) findViewById(R.id.txtProductNames);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        txtMpesaID = (EditText) findViewById(R.id.txtMpesaID);
        watchInput();
        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        btnRecordPurchase = (Button) findViewById(R.id.btnRecordPurchase);
        btnRecordPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String payeeName, payeePhoneNumber, description, mpesaID, productNames, price;

                payeeName = txtPayeeName.getText().toString().trim();
                payeePhoneNumber = txtPayeePhoneNumber.getText().toString().trim();
                description = txtDescription.getText().toString().trim();

                productNames = txtProductNames.getText().toString().trim();
                price = txtPrice.getText().toString().trim();
                mpesaID = txtMpesaID.getText().toString().trim();

                if (validate()) {
                    //do post
                    try {

                        SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                                Context.MODE_PRIVATE);
                        final String username = settings.getString("username", "Default User");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("payee_name", payeeName);
                        jsonObject.put("phone_number", payeePhoneNumber);
                        jsonObject.put("payment_description", description);
                        jsonObject.put("authorised_by", username);
                        jsonObject.put("receipt_no", generateReceipt());
                        jsonObject.put("vat_no", "#VAT_AUTO_123");
                        jsonObject.put("kra_pin_no", "P051617414C");
                        jsonObject.put("product_names", productNames);
                        jsonObject.put("amount_paid", Float.parseFloat(price));
                        jsonObject.put("amount_paid", Float.parseFloat(price));
                        jsonObject.put("mpesa_code", mpesaID);

                        progressDialog.setTitle("Submitting");
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.show();

                        final String URL = "http://grade.hudutech.com/api_backend/api/purchases.php";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            if (response.getInt("status_code") == 201) {


                                                JSONObject data = response.getJSONObject("data");

                                                SharedPreferences settings = getApplicationContext().getSharedPreferences("PRINT_DATA",
                                                        Context.MODE_PRIVATE);

                                                SharedPreferences.Editor editor = settings.edit();

                                                editor.putString("receipt_no", "Receipt No: " +data.getString("receipt_no"));
                                                editor.putString("phone_number", "Phone Number: " + data.getString("phone_number"));
                                                editor.putString("authorised_by", "Authorised By: " +data.getString("authorised_by"));
                                                editor.putString("vat_no", "V.A.T NO: " +data.getString("vat_no"));
                                                editor.putString("kra_pin", "K.R.A PIN NO: " + data.getString("kra_pin_no"));
                                                editor.putString("payee_name", "Payee Name: " + data.getString("payee_name"));
                                                editor.putString("product_names", "Products : " + data.getString("product_names"));
                                                editor.putString("description", "Description: " + data.getString("payment_description"));
                                                editor.putString("total_price", "Total Price: KES" + data.getString("amount_paid"));
                                                editor.putString("date", "Date Paid: " +data.getString("date_paid"));
                                                editor.putString("mpesa", "Mpesa ID: " + data.getString("mpesa_code"));
                                                editor.apply();
                                                editor.commit();

                                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), PrintPreviewActivity.class));



                                            }
                                            else if (response.getInt("status_code") == 500) {
                                                Toast.makeText(getApplicationContext(), "Error " + response.getString("message"), Toast.LENGTH_LONG).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.e("Error: ", error.getMessage());
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
                                });

                        requestQueue.add(jsonObjectRequest);
                        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                            @Override
                            public void onRequestFinished(Request<Object> request) {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Fix the errors above", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isLoggedIn()){
            startActivity(new Intent(this, WelcomeActivity.class));
        }

    }

    public boolean isLoggedIn(){
        SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                Context.MODE_PRIVATE);
        String username = settings.getString("username", "");
        if(!username.equals("")){
            return true;
        }else{
            return false;
        }
    }



    public boolean validate(){
        boolean valid = true;
        if(txtPayeeName.getText().toString().trim().isEmpty()){
            txtPayeeName.setError("This field is required");
            valid = false;
        }else{
            txtPayeeName.setError(null);
        }

        if (txtPayeePhoneNumber.getText().toString().trim().isEmpty()){
            txtPayeePhoneNumber.setError("This field is required");
            valid = false;
        }else{
            txtPayeePhoneNumber.setError(null);
        }

        if(txtDescription.getText().toString().trim().isEmpty()){
            txtDescription.setError("This field is required");
            valid = false;
        }else{
            txtDescription.setError(null);
        }



        if (txtMpesaID.getText().toString().trim().isEmpty()){
            txtMpesaID.setError("This field is required");
            valid = false;
        }else{
            txtMpesaID.setError(null);
        }

        if (txtProductNames.getText().toString().isEmpty()){
            txtProductNames.setError("This field is required");
            valid = false;
        }else{
            txtProductNames.setError(null);
        }

        if (txtPrice.getText().toString().isEmpty()){
            txtPrice.setError("This field is required");
            valid = false;
        }else{
            txtPrice.setError(null);
        }

        return valid;

    }

    public void watchInput(){
        txtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.setError(null);
            }
        });
        txtPayeeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPayeeName.setError(null);
            }
        });

        txtProductNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtProductNames.setError(null);
            }
        });

        txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDescription.setError(null);
            }
        });

        txtMpesaID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtMpesaID.setError(null);
            }
        });
    }
    public  String generateReceipt(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000000)) + "-");
        for (int i = 0; i < 3; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }

        return sb.toString();
    }


}