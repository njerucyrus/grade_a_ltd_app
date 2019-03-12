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

public class LoginActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    Button btnAuthLogin;
    TextView txtDontHaveAcc, txtForgotPassword;
    EditText txtAuthUsername, txtAuthPassword;
    ProgressDialog progressDialog;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtAuthUsername = (EditText) findViewById(R.id.txtAuthUsername);
        txtAuthPassword = (EditText) findViewById(R.id.txtAuthPassword);
        watchInput();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        URL = this.getResources().getString(R.string.base_url)+"/users.php?action=login";


        btnAuthLogin = (Button) findViewById(R.id.btnAuthLogin);
        txtDontHaveAcc = (TextView) findViewById(R.id.txtDontHaveAcc);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting");

        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        btnAuthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        txtDontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void doLogin() {


        String authUsername, authPassword;

        authUsername = txtAuthUsername.getText().toString().trim();
        authPassword = txtAuthPassword.getText().toString().trim();
        if (validate()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", authUsername);
                jsonObject.put("password", authPassword);
                progressDialog.setMessage("Authenticating...");
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

                                        String username;
                                        if (data.getString("email").equals("")) {
                                            username = data.getString("phone_number");
                                        } else {
                                            username = data.getString("email");
                                        }


                                        if (data.getInt("status") == 1) {

                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putInt("user_level", data.getInt("user_level"));
                                            editor.putInt("userId", data.getInt("id"));
                                            editor.putInt("status", data.getInt("status"));
                                            editor.putString("username", username);
                                            editor.putString("authorised_by", data.getString("fullname"));
                                            editor.apply();
                                            editor.commit();

                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();

                                        }else {
                                            Toast.makeText(getApplicationContext(), "Your account is NOT ACTIVE please contact admin for activation", Toast.LENGTH_LONG).show();
                                        }

                                    } else if (response.getInt("status_code") == 500) {

                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

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
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fix the errors above", Toast.LENGTH_LONG).show();
        }
    }



    public boolean validate() {
        boolean valid = true;
        if (txtAuthUsername.getText().toString().trim().isEmpty()) {
            txtAuthUsername.setError("This field is required");
            valid = false;
        } else {
            txtAuthUsername.setError(null);

        }
        if (txtAuthPassword.getText().toString().trim().isEmpty()) {
            txtAuthPassword.setError("This field is required");
            valid = false;
        } else {
            txtAuthPassword.setError(null);

        }
        return valid;
    }

    public void watchInput() {
        txtAuthUsername.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtAuthUsername.setError(null);
            }
        });

        txtAuthPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtAuthPassword.setError(null);
            }
        });
    }
}