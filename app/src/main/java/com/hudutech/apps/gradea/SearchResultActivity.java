package com.hudutech.apps.gradea;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
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

public class SearchResultActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<RecyclerItem> listItems = new ArrayList<>();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.searchResultRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog = new ProgressDialog(this);

        requestQueue = VolleyRequestSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            final String URL = getApplicationContext().getResources().getString(R.string.base_url) + "/purchases.php?query=" + query;
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

                                        RecyclerItem item = new RecyclerItem();
                                        item.setId(object.getInt("id"));
                                        item.setReceiptNo(object.getString("receipt_no"));
                                        item.setVatNo(object.getString("vat_no"));
                                        item.setKraPin(object.getString("kra_pin_no"));
                                        item.setPayeeName(object.getString("payee_name"));
                                        item.setProducts(object.getString("product_names"));
                                        item.setDescription(object.getString("payment_description"));
                                        item.setPrice(object.getString("amount_paid"));
                                        item.setPhoneNumber(object.getString("phone_number"));
                                        item.setAuthorisedBy(object.getString("authorised_by"));
                                        item.setDate(object.getString("date_paid"));
                                        listItems.add(item);
                                    }
                                    adapter = new MyAdapter(listItems, getApplicationContext());
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
            requestQueue.addRequestFinishedListener(
                    new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });

        }
    }

}
