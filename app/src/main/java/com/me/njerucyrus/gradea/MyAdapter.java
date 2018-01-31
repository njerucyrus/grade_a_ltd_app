package com.me.njerucyrus.gradea;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
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

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<RecyclerItem> listItems;
    private Context mContext;

    public MyAdapter(List<RecyclerItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
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
        public TextView txtOptionDigit;

        ProgressDialog progressDialog;
        RequestQueue requestQueue;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            requestQueue = VolleyRequestSingleton.getInstance(itemView.getContext()).getRequestQueue();
            progressDialog = new ProgressDialog(itemView.getContext());

            txtOptionDigit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, txtOptionDigit);
                    popupMenu.inflate(R.menu.option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {

                                case R.id.menu_item_archive:
                                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                        final RecyclerItem item = listItems.get(getAdapterPosition());
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setTitle("Archive this record?");
                                        builder.setMessage("This record will be moved to archives.");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressDialog.setMessage("Submitting");
                                                progressDialog.show();
                                                final String URL = "http://grade.hudutech.com/api_backend/api/purchases.php?action=archive&id=" + item.getId();
                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    if (response.getInt("status_code") == 201) {
                                                                        Toast.makeText(mContext, "Record was archived.", Toast.LENGTH_LONG).show();
                                                                        view.getContext().startActivity(new Intent(mContext, MainActivity.class));
                                                                    } else {
                                                                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
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

                                    break;
                                case R.id.menu_item_preview:
                                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                        SharedPreferences settings = mContext.getSharedPreferences("PRINT_DATA",
                                                Context.MODE_PRIVATE);

                                        SharedPreferences.Editor editor = settings.edit();

                                        RecyclerItem item = listItems.get(getAdapterPosition());
                                        editor.putInt("id", item.getId());

                                        editor.putString("receipt_no", "Receipt No: " + item.getReceiptNo());
                                        editor.putString("phone_number", "Phone Number: " + item.getPhoneNumber());
                                        editor.putString("authorised_by", "Authorised By: " + item.getAuthorisedBy());
                                        editor.putString("vat_no", "V.A.T NO: " + item.getVatNo());
                                        editor.putString("kra_pin", "K.R.A PIN NO: " + item.getKraPin());
                                        editor.putString("payee_name", "Payee Name: " + item.getPayeeName());
                                        editor.putString("product_names", "Products : " + item.getProducts());
                                        editor.putString("description", "Description: " + item.getDescription());
                                        editor.putString("total_price", "Total Price: " + item.getPrice());
                                        editor.putString("date", "Date Paid: " + item.getDate());
                                        editor.apply();
                                        editor.commit();

                                        view.getContext().startActivity(new Intent(mContext, PrintPreviewActivity.class));
                                    }
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }

                    });
                    popupMenu.show();
                }
            });


        }

    }
}
