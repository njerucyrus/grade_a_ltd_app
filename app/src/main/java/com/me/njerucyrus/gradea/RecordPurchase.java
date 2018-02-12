package com.me.njerucyrus.gradea;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class RecordPurchase extends AppCompatActivity {
    RequestQueue requestQueue;
    Button btnRecordPurchase;
    EditText txtPayeeName, txtPayeePhoneNumber, txtDescription,
            txtProductNames, txtPrice, txtMpesaID, txtInvoiceRefNo;

    TextView txtDate;
    ProgressDialog progressDialog;

    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;

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
        txtInvoiceRefNo = (EditText)findViewById(R.id.txtInvoiceRefNo);
        watchInput();

        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault());

        txtDate = (TextView)findViewById(R.id.txtDateRecorded);
        txtDate.setOnClickListener(textListener);
        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        btnRecordPurchase = (Button) findViewById(R.id.btnRecordPurchase);
        btnRecordPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String payeeName, payeePhoneNumber, description, mpesaID,
                        productNames, price, datePaid, invoiceNo;

                payeeName = txtPayeeName.getText().toString().trim();
                payeePhoneNumber = txtPayeePhoneNumber.getText().toString().trim();
                description = txtDescription.getText().toString().trim();

                productNames = txtProductNames.getText().toString().trim();
                price = txtPrice.getText().toString().trim();
                mpesaID = txtMpesaID.getText().toString().trim();
                datePaid = txtDate.getText().toString().trim();
                invoiceNo = txtInvoiceRefNo.getText().toString().trim();

                if (validate()) {
                    //do post
                    try {

                        SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                                Context.MODE_PRIVATE);
                        final String authorised_by = settings.getString("authorised_by", "Default User");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("payee_name", payeeName);
                        jsonObject.put("phone_number", payeePhoneNumber);
                        jsonObject.put("payment_description", description);
                        jsonObject.put("authorised_by", authorised_by);
                        jsonObject.put("receipt_no", generateReceipt());
                        jsonObject.put("vat_no", "#VAT_AUTO_123");
                        jsonObject.put("kra_pin_no", "P051617414C");
                        jsonObject.put("product_names", productNames);
                        jsonObject.put("amount_paid", Float.parseFloat(price));
                        jsonObject.put("amount_paid", Float.parseFloat(price));
                        jsonObject.put("mpesa_code", mpesaID);
                        jsonObject.put("date_paid", datePaid);
                        jsonObject.put("invoice_no", invoiceNo);

                        progressDialog.setTitle("Submitting");
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.show();

                        final String URL = getApplicationContext().getResources().getString(R.string.base_url)+"/purchases.php";

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
                                                editor.putString("date", "Date: " +data.getString("date_paid"));
                                                editor.putString("mpesa", "Mpesa ID: " + data.getString("mpesa_code"));
                                                editor.putString("invoice_no", "Invoice Ref No : " + data.getString("invoice_no"));
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

        if (txtInvoiceRefNo.getText().toString().trim().isEmpty()){
            txtInvoiceRefNo.setError("This field is required");
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

        if(txtDate.getText().toString().equals("Select Date")){
            valid = false;
            Toast.makeText(RecordPurchase.this, "Select date to continue", Toast.LENGTH_SHORT).show();
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

        txtInvoiceRefNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInvoiceRefNo.setError(null);
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


    /* Define the onClickListener, and start the DatePickerDialog with users current time */
    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            new DatePickerDialog(RecordPurchase.this, mDateDataSet, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(RecordPurchase.this, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            txtDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };


}