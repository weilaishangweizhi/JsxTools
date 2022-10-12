package com.hollysmart.jsxdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        DsbridgeWebFragment dsbridgeWebFragment = new DsbridgeWebFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, dsbridgeWebFragment, "dsbrige")
                .commit();
    }

}
