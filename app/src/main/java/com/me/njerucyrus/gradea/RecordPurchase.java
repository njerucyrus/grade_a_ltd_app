package com.me.njerucyrus.gradea;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordPurchase extends AppCompatActivity {
    RequestQueue requestQueue;
    Button btnRecordPurchase;
    EditText txtPayeeName, txtPayeePhoneNumber, txtDescription, txtReceiptNo,
            txtVatNo, txtKraPinNo, txtProductNames, txtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_purchase);
        ActionBar ab = getSupportActionBar();

        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        btnRecordPurchase = (Button) findViewById(R.id.btnRecordPurchase);
        btnRecordPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPayeeName = (EditText) findViewById(R.id.txtPayeeName);
                txtPayeePhoneNumber = (EditText) findViewById(R.id.txtPayeePhoneNumber);
                txtDescription = (EditText) findViewById(R.id.txtDescription);
                txtReceiptNo = (EditText) findViewById(R.id.txtReceiptNo);
                txtVatNo = (EditText) findViewById(R.id.txtVatNo);
                txtKraPinNo = (EditText) findViewById(R.id.txtKraPinNo);
                txtProductNames = (EditText) findViewById(R.id.txtProductNames);
                txtPrice = (EditText) findViewById(R.id.txtPrice);

                String payeeName, payeePhoneNumber, description, receiptNo, vatNo, kraPinNo, productNames, price;

                payeeName = txtPayeeName.getText().toString();
                payeePhoneNumber = txtPayeePhoneNumber.getText().toString();
                description = txtDescription.getText().toString();
                vatNo = txtVatNo.getText().toString();
                receiptNo = txtReceiptNo.getText().toString();
                kraPinNo = txtKraPinNo.getText().toString();
                productNames = txtProductNames.getText().toString();
                price = txtPrice.getText().toString();

                if (!payeeName.equals("") && !payeePhoneNumber.equals("") && !description.equals("")
                        && !vatNo.equals("") && !kraPinNo.equals("") && !productNames.equals("") &&
                        !price.equals("") && !receiptNo.equals("")) {
                    //do post
                    try {
                        Toast.makeText(getApplicationContext(), "Submitting...", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPref = RecordPurchase.this.getPreferences(Context.MODE_PRIVATE);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("payee_name", payeeName);
                        jsonObject.put("phone_number", payeePhoneNumber);
                        jsonObject.put("payment_description", description);
                        jsonObject.put("authorised_by", sharedPref.getString("username", "Default User"));
                        jsonObject.put("receipt_no", receiptNo);
                        jsonObject.put("vat_no", vatNo);
                        jsonObject.put("kra_pin_no", kraPinNo);
                        jsonObject.put("product_names", productNames);
                        jsonObject.put("amount_paid", Float.parseFloat(price));

                        final String URL = "https://b75b369e.ngrok.io/api_backend/api/purchases.php";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            if (response.getInt("status_code") == 201) {


                                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), PrintPreviewActivity.class));


                                            }
                                            if (response.getInt("status_code") == 500) {
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
                                        Toast.makeText(getApplicationContext(), "VolleyError " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_LONG).show();

                }

            }
        });

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

}