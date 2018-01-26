package com.me.njerucyrus.gradea;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    Button btnAuthRegister;
    TextView txtHaveAcc;
    RequestQueue requestQueue;
    EditText txtFullname,
            txtPhoneNumber,
            txtEmail,
            txtPassword,
            txtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        btnAuthRegister = (Button)findViewById(R.id.btnAuthRegister);
        txtHaveAcc = (TextView)findViewById(R.id.txtHaveAcc);

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
                final String fullName, phoneNumber, email, password, confirmPassword;

                txtFullname = (EditText)findViewById(R.id.txtFullname);
                txtPhoneNumber = (EditText)findViewById(R.id.txtPhoneNumber);
                txtEmail = (EditText)findViewById(R.id.txtEmail);
                txtPassword = (EditText)findViewById(R.id.txtPassword);
                txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);

                fullName = txtFullname.getText().toString();
                phoneNumber = txtPhoneNumber.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();
                if(fullName.isEmpty() && phoneNumber.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_LONG).show();
                }else {
                    if (password.equals(confirmPassword)) {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("fullname", fullName);
                            jsonObject.put("email", email);
                            jsonObject.put("phone_number", phoneNumber);
                            jsonObject.put("password", password);
                            final String URL = "http://grade.hudutech.com/api_backend/api/users.php?action=create_account";

                            Toast.makeText(getApplicationContext(), "Creating account ... ", Toast.LENGTH_LONG).show();

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if (response.getInt("status_code") == 201) {

                                                    SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                                                            Context.MODE_PRIVATE);

                                                    SharedPreferences.Editor editor = settings.edit();

                                                    String username;

                                                    if (!email.equals("")) {
                                                        username = email;
                                                    } else {
                                                        username = phoneNumber;
                                                    }

                                                    editor.putString("username", username);
                                                    editor.putString("user", fullName);
                                                    editor.commit();


                                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password Do not match", Toast.LENGTH_LONG).show();


                    }
                }


            }
        });


    }
}
