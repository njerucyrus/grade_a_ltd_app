package com.hudutech.apps.gradea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

   Button btnSignIn, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btnSignIn = (Button)findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
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
}
