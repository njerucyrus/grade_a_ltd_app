package com.me.njerucyrus.gradea;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.app.job.JobServiceEngine;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by njerucyrus on 3/1/18.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> userList;
    private Context mContext;
    RequestQueue requestQueue;

    public UsersAdapter(List<User> userList, Context mContext) {
        this.userList = userList;
        this.mContext = mContext;
        requestQueue = VolleyRequestSingleton.getInstance(mContext.getApplicationContext()).getRequestQueue();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int NOT_AUTHORISED = 0;
        final int AUTHORISED = 1;
        final int BLOCKED = 2;
        final User user = userList.get(position);
        holder.txtUserFullName.setText(user.getFullName());
        String userDetail = "PhoneNumber: " + user.getPhoneNumber() + "\nEmail: " + user.getEmail();
        holder.txtUserDetail.setText(userDetail);
        holder.txtDateJoined.setText(user.getDateJoined());
        int userStatus = user.getUserStatus();
        if (userStatus == NOT_AUTHORISED) {
            holder.mBtnBlock.setEnabled(false);
            holder.mBtnBlock.setVisibility(View.GONE);
            holder.mBtnAuthorise.setEnabled(true);
            holder.mBtnAuthorise.setVisibility(View.VISIBLE);
            holder.mBtnAuthorise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext());
                    builder.setTitle("Are you sure you want to AUTHORISE this user?");
                    builder.setMessage("This user will be able to record transactions and print receipts");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                JSONObject data = new JSONObject();
                                data.put("status", AUTHORISED);
                                data.put("id", user.getUserId());
                                holder.mProgressDialog.setTitle("Submitting");
                                holder.mProgressDialog.setMessage("Please wait...");
                                holder.mProgressDialog.setCanceledOnTouchOutside(false);
                                holder.mProgressDialog.show();
                                final String URL = mContext.getApplicationContext().getResources().getString(R.string.base_url) + "/users.php?action=update_access";
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, data,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getInt("status_code") == 201) {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                        holder.mView.getContext().startActivity(new Intent(mContext.getApplicationContext(), ManageUsersActivity.class));

                                                    } else {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                            }
                                        });
                                requestQueue.add(request);
                                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                    @Override
                                    public void onRequestFinished(Request<Object> request) {
                                        if (holder.mProgressDialog.isShowing()) {
                                            holder.mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
            });
        }

        if (userStatus == AUTHORISED) {
            holder.mBtnAuthorise.setEnabled(false);
            holder.mBtnAuthorise.setVisibility(View.GONE);
            holder.mBtnBlock.setVisibility(View.VISIBLE);
            holder.mBtnBlock.setEnabled(true);
            holder.mBtnBlock.setText("Block User");
            holder.mBtnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext());
                    builder.setTitle("Are you sure want to BLOCK this user?");
                    builder.setMessage("This user will not be able to use your system anymore");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONObject data = new JSONObject();
                                data.put("status", BLOCKED);
                                data.put("id", user.getUserId());
                                holder.mProgressDialog.setTitle("Submitting");
                                holder.mProgressDialog.setMessage("Please wait...");
                                holder.mProgressDialog.setCanceledOnTouchOutside(false);
                                holder.mProgressDialog.show();
                                final String URL = mContext.getApplicationContext().getResources().getString(R.string.base_url) + "/users.php?action=update_access";
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, data,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getInt("status_code") == 201) {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                        holder.mView.getContext().startActivity(new Intent(mContext.getApplicationContext(), ManageUsersActivity.class));

                                                    } else {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                            }
                                        });
                                requestQueue.add(request);
                                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                    @Override
                                    public void onRequestFinished(Request<Object> request) {
                                        if (holder.mProgressDialog.isShowing()) {
                                            holder.mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            });
        }

        if (userStatus == BLOCKED) {
            holder.mBtnBlock.setEnabled(true);
            holder.mBtnBlock.setVisibility(View.VISIBLE);
            holder.mBtnBlock.setText("Unblock User");
            holder.mBtnAuthorise.setEnabled(false);
            holder.mBtnAuthorise.setVisibility(View.GONE);
            holder.mBtnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext());
                    builder.setTitle("Are you sure you want to UNBLOCK this user?");
                    builder.setMessage(" This user will be able to record transactions and print receipts");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONObject data = new JSONObject();
                                data.put("status", AUTHORISED);
                                data.put("id", user.getUserId());
                                holder.mProgressDialog.setTitle("Submitting");
                                holder.mProgressDialog.setMessage("Please wait...");
                                holder.mProgressDialog.setCanceledOnTouchOutside(false);
                                holder.mProgressDialog.show();
                                final String URL = mContext.getApplicationContext().getResources().getString(R.string.base_url) + "/users.php?action=update_access";
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, data,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getInt("status_code") == 201) {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                        holder.mView.getContext().startActivity(new Intent(mContext.getApplicationContext(), ManageUsersActivity.class));
                                                    } else {
                                                        String message = response.getString("message");
                                                        Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                            }
                                        });
                                requestQueue.add(request);
                                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                    @Override
                                    public void onRequestFinished(Request<Object> request) {
                                        if (holder.mProgressDialog.isShowing()) {
                                            holder.mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
            });
        }


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView txtUserFullName;
        TextView txtUserDetail;
        TextView txtDateJoined;
        Button mBtnAuthorise;
        Button mBtnBlock;
        ProgressDialog mProgressDialog;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            txtUserFullName = (TextView) itemView.findViewById(R.id.txtUserFullName);
            txtUserDetail = (TextView) itemView.findViewById(R.id.txtUserDetail);
            txtDateJoined = (TextView) itemView.findViewById(R.id.txtDateCreated);
            mBtnAuthorise = (Button) itemView.findViewById(R.id.btn_authorise_user);
            mBtnBlock = (Button) itemView.findViewById(R.id.btn_block_user);
            mProgressDialog = new ProgressDialog(itemView.getContext());

        }


    }
}
