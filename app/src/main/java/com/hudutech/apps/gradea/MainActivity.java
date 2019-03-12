package com.hudutech.apps.gradea;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Boolean debug = true;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<RecyclerItem> listItems =new ArrayList<>();
    ProgressDialog progressDialog;
    private String URL;

    private int USER_LEVEL;
    private static final int ADMIN_LEVEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        URL = this.getResources().getString(R.string.base_url)+"/purchases.php?filter=non";

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = this.getSharedPreferences("AUTH_DATA",
                Context.MODE_PRIVATE);
        USER_LEVEL = settings.getInt("user_level", 0);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                                    item.setIsArchived(object.getInt("is_archived"));
                                    item.setmPesa(object.getString("mpesa_code"));
                                    //add only items which are not archived
                                    if (object.getInt("is_archived") == 0) {
                                        listItems.add(item);
                                    }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconified(false);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id==R.id.search){
            onSearchRequested();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_new_record) {
            startActivity(new Intent(getApplicationContext(), RecordPurchase.class));
        } else if (id == R.id.nav_view_records) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else if (id == R.id.nav_archives) {

           startActivity(new Intent(getApplicationContext(), ArchivesActivity.class));

        }
        else if (id == R.id.nav_manage_users){
            if (USER_LEVEL == ADMIN_LEVEL) {
                startActivity(new Intent(MainActivity.this, ManageUsersActivity.class));
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "You are not authorised to view this page", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_logout) {
            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isLoggedIn()){
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

    }


    public boolean isLoggedIn(){
        SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                Context.MODE_PRIVATE);
        String username = settings.getString("username", "");
        int status = settings.getInt("status", 0);
        if(!username.equals("") && status == 1){
            return true;
        }else{
            return false;
        }
    }



    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to logout?");
        builder.setMessage("You will be logged out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.apply();
                editor.commit();
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });

        builder.show();
    }


}
