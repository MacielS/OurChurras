package com.projeto.ourchurras.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.projeto.ourchurras.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAutenticacao();
            }
        }, 2000);
    }

    private void abrirAutenticacao() {
        Intent i = new Intent(MainActivity.this, AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }
}
