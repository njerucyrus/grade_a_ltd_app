package com.me.njerucyrus.gradea;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class PrintPreviewActivity extends AppCompatActivity implements Runnable {

    TextView mPayeeName, mPhoneNumber, mDescription, mAuthorisedBy,
            mReceiptNo, mProducts, mPrice, mDate, mPesa;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint;
    RecyclerItem item;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        item = new RecyclerItem();

        mPayeeName = (TextView) findViewById(R.id.mPayeeName);
        mPhoneNumber = (TextView) findViewById(R.id.mPhoneNumber);
        mDescription = (TextView) findViewById(R.id.mDescription);
        mAuthorisedBy = (TextView) findViewById(R.id.mAuthorisedBy);
        mReceiptNo = (TextView) findViewById(R.id.mReceiptNo);
        mProducts = (TextView) findViewById(R.id.mProducts);
        mPrice = (TextView) findViewById(R.id.mPrice);
        mDate = (TextView) findViewById(R.id.mDate);
        mPesa = (TextView) findViewById(R.id.mPesa);
        SharedPreferences settings = getSharedPreferences("PRINT_DATA",
                Context.MODE_PRIVATE);


        mPayeeName.setText(settings.getString("payee_name", ""));
        mPhoneNumber.setText(settings.getString("phone_number", ""));
        mDescription.setText(settings.getString("description", ""));
        mAuthorisedBy.setText(settings.getString("authorised_by", ""));
        mReceiptNo.setText(settings.getString("receipt_no", ""));
        mProducts.setText(settings.getString("product_names", ""));
        mPrice.setText(settings.getString("total_price", ""));
        mDate.setText(settings.getString("date", ""));
        mPesa.setText(settings.getString("mpesa", ""));

        item.setPayeeName(settings.getString("payee_name", ""));
        item.setPhoneNumber(settings.getString("phone_number", ""));
        item.setDescription(settings.getString("description", ""));
        item.setAuthorisedBy(settings.getString("authorised_by", ""));
        item.setReceiptNo(settings.getString("receipt_no", ""));
        item.setProducts(settings.getString("product_names", "").trim().replace(",","\n"));
        item.setPrice(settings.getString("total_price", ""));
        item.setDate(settings.getString("date", ""));
        item.setmPesa(settings.getString("mpesa", ""));





        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PrintPreviewActivity.this, "No printer connected", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintPreviewActivity.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });


    }// onCreate

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isLoggedIn()){
            startActivity(new Intent(this, WelcomeActivity.class));
        }

    }

    public boolean isLoggedIn(){
        SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                Context.MODE_PRIVATE);
        String username = settings.getString("username", "");
        if(!username.equals("")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == AppCompatActivity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
//                    pairToDevice(mBluetoothDevice); //This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == AppCompatActivity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintPreviewActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintPreviewActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrintPreviewActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
            doPrint();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }
    // Enable the Up button

    public void doPrint(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    String BILL = "";

                    BILL =  "        GRADE (A) KENYA LIMITED\n"+
                            "        P.O BOX 1349-00502\n" +
                            "        Karen, Nairobi\n" +
                            "        RECEIPT\n" +
                            "----------------------------------------------------------\n"+
                            "----------------------------------------------------------\n"+
                            "   " +item.getReceiptNo()+ "      \n" +
                            "   " +item.getmPesa()+ "   \n" +
                            "   " +item.getPayeeName()+ "   \n" +
                            "   " +item.getPhoneNumber()+ "   \n" +
                            "   " +item.getAuthorisedBy()+ "   \n" +
                            "   " +item.getDate()+ "   \n" +
                            "                        \n";
                    BILL = BILL + "----------------------------------------------------------";


                    BILL = BILL + String.format("%1$-10s ", "Products" );
                    BILL = BILL + "\n";
                    BILL = BILL + "---------------------------------------------------------";
                    BILL = BILL + "\n" + String.format("%1$-10s  ",item.getProducts().substring(11) );
                    BILL = BILL + "\n";
                    BILL = BILL + "---------------------------------------------------------";
                    BILL = BILL +" \n"+ String.format("%1$-10s ", "Descriptions" );
                    BILL = BILL + "\n";
                    BILL = BILL + "---------------------------------------------------------";
                    BILL = BILL + " \n" + String.format("%1$-10s",  item.getDescription().substring(13));
                    BILL = BILL + "\n-------------------------------------------------------";
                    BILL = BILL + "\n ";

                    BILL = BILL + "    " +  String.format("%1$-10s",  item.getPrice());
                    BILL = BILL + "\n-------------------------------------------------------";
                    BILL = BILL + "\n\n ";
                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));



                } catch (Exception e) {
                    Log.e("PrintPreviewActivity", "Exe ", e);
                }
            }
        };
        Toast toast = Toast.makeText(getApplicationContext(), "Printing...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        t.start();
    }
}


