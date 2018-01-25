package com.me.njerucyrus.gradea;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PrintPreviewActivity extends AppCompatActivity {

    private Button btnPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ActionBar ab = getSupportActionBar();

        btnPrint = (Button)findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Not Yet implemented", Toast.LENGTH_LONG).show();
            }
        });
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
}