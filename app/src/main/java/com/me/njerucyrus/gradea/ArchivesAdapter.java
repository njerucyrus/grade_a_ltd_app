package com.me.njerucyrus.gradea;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class ArchivesAdapter extends RecyclerView.Adapter<ArchivesAdapter.ViewHolder> {
    private List<RecyclerItem> listItems;
    private Context mContext;


    public ArchivesAdapter(List<RecyclerItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archives_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RecyclerItem itemList = listItems.get(position);
        String title = "RECEIPT #" + itemList.getReceiptNo();
        String description = "Payee: " + itemList.getPayeeName() + ", Ksh " + itemList.getPrice() + ".00" +
                "\nDate " + itemList.getDate();
        holder.txtTitle.setText(title);
        holder.txtItemDescription.setText(description);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtItemDescription;
        public Button btnRecyclerPrintPreview;
        public Button btnRemoveArchive;
        ProgressDialog progressDialog;
        RequestQueue requestQueue;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtArchiveTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemArchiveDescription);

            btnRemoveArchive = (Button) itemView.findViewById(R.id.recycler_btn_remove_archive);
            requestQueue = VolleyRequestSingleton.getInstance(itemView.getContext()).getRequestQueue();
            progressDialog = new ProgressDialog(itemView.getContext());


            btnRemoveArchive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        final RecyclerItem item = listItems.get(pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Restore this record?");
                        builder.setMessage("This record will be restored.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Submitting");
                                progressDialog.show();
                                Config config = new Config();
                                final String URL = config.getBASE_URL() +"/purchases.php?action=remove_archive&id="+item.getId();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getInt("status_code") == 201){
                                                        Toast.makeText(mContext, "Record was restored.", Toast.LENGTH_LONG).show();
                                                        view.getContext().startActivity(new Intent(mContext, MainActivity.class));
                                                    }else{
                                                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
                                                    }

                                                }catch (JSONException e){
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
                                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();


                                            }
                                        }
                                );
                                requestQueue.add(jsonObjectRequest);
                                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                    @Override
                                    public void onRequestFinished(Request<Object> request) {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });


                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing just let dispose the dialog
                            }
                        });

                        builder.show();
                    }
                }
            });

        }

    }
}
