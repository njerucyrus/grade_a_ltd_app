package com.hudutech.apps.gradea;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    Button btnAuthRegister;
    TextView txtHaveAcc;
    RequestQueue requestQueue;
    EditText txtFullname,
            txtPhoneNumber,
            txtEmail,
            txtPassword,
            txtConfirmPassword;
    ProgressDialog progressDialog;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        txtFullname = (EditText)findViewById(R.id.txtFullname);
        txtPhoneNumber = (EditText)findViewById(R.id.txtPhoneNumber);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);

        watchInput();


        btnAuthRegister = (Button)findViewById(R.id.btnAuthRegister);
        txtHaveAcc = (TextView)findViewById(R.id.txtHaveAcc);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait...");

        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();


        txtHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });



        btnAuthRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                      doCreateAccount();

                }
            }
        );

    }


    public void doCreateAccount(){
        final String fullName, phoneNumber, email, password, confirmPassword;

        fullName = txtFullname.getText().toString().trim();
        phoneNumber = txtPhoneNumber.getText().toString().trim();
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        confirmPassword = txtConfirmPassword.getText().toString().trim();
        if (validate()) {
            if (password.equals(confirmPassword)) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fullname", fullName);
                    jsonObject.put("email", email);
                    jsonObject.put("phone_number", phoneNumber);
                    jsonObject.put("password", password);
                    jsonObject.put("status", 0);
                    jsonObject.put("user_level", 0);
                    jsonObject.put("date_joined", new Date().toString());
                    final String URL = getApplicationContext().getResources().getString(R.string.base_url)+"/users.php?action=create_account";
                    progressDialog.show();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getInt("status_code") == 201) {

                                            SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                                                    Context.MODE_PRIVATE);

                                            JSONObject data = response.getJSONObject("data");

                                            SharedPreferences.Editor editor = settings.edit();

                                            String username;

                                            if (!email.equals("")) {
                                                username = email;
                                            } else {
                                                username = phoneNumber;
                                            }

                                            editor.putString("username", username);
                                            editor.putString("user", fullName);
                                            editor.putString("authorised_by", data.getString("fullname"));
                                            editor.apply();
                                            editor.commit();


                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                                        }
                                        if (response.getInt("status_code") == 500) {
                                            Toast.makeText(getApplicationContext(), "Error " + response.getString("message"), Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
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
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();


                }
            } else {
                Toast.makeText(getApplicationContext(), "Password Do not match", Toast.LENGTH_LONG).show();


            }
        }else{
            Toast.makeText(getApplicationContext(), "Fix the errors above", Toast.LENGTH_LONG).show();
        }

    }

    public boolean validate(){
        boolean valid = true;
        if (txtFullname.getText().toString().trim().isEmpty()){
            txtFullname.setError("This field is required");
            valid = false;
        }else{
            txtFullname.setError(null);
        }

        if (txtPhoneNumber.getText().toString().trim().isEmpty()){
            txtPhoneNumber.setError("This field is required");
            valid = false;
        }else{
            txtPhoneNumber.setError(null);
        }

        if (txtPassword.getText().toString().trim().isEmpty()){
            txtPassword.setError("This field is required");
            valid = false;
        }else{
            txtPassword.setError(null);
        }

        if (txtConfirmPassword.getText().toString().trim().isEmpty()){
            txtConfirmPassword.setError("This field is required");
            valid = false;
        }else{
            txtConfirmPassword.setError(null);
        }
        return valid;
    }

    public void watchInput(){

        txtFullname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtFullname.setError(null);
            }
        });

        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtPhoneNumber.setError(null);
            }
        });
        txtPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtPassword.setError(null);
            }
        });
        txtConfirmPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtConfirmPassword.setError(null);
            }
        });
    }

}
