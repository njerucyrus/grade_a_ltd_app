package com.me.njerucyrus.gradea;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    UsersAdapter adapter;
    List<User> usersList =new ArrayList<>();
    ProgressDialog progressDialog;
    String URL;
    private int USER_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        URL = this.getResources().getString(R.string.base_url)+"/users.php";

        recyclerView = (RecyclerView) findViewById(R.id.users_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SharedPreferences settings = this.getSharedPreferences("AUTH_DATA",
                Context.MODE_PRIVATE);
        USER_ID = settings.getInt("userId", 0);

        progressDialog = new ProgressDialog(this);

        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();


        progressDialog.setMessage("Loading...");
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status_code") == 200) {
                                JSONArray data = response.optJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.optJSONObject(i);

                                    User user = new User();
                                    user.setFullName(object.getString("fullname"));
                                    user.setEmail(object.getString("email"));
                                    user.setPhoneNumber(object.getString("phone_number"));
                                    user.setDateJoined(object.getString("date_joined"));
                                    user.setUserLevel(object.getInt("user_level"));
                                    user.setUserStatus(object.getInt("status"));
                                    user.setUserId(object.getInt("id"));

                                    //DO NOT INCLUDE CURRENT LOGGED IN USER
                                    if (object.getInt("id") != USER_ID) {
                                        usersList.add(user);
                                    }


                                }
                                adapter = new UsersAdapter(usersList, getApplicationContext());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);


                            } else {
                                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
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
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            requestQueue.getCache().invalidate(URL, true);
        }
    }
}
