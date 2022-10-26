package com.hollysmart.jsxdemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hollysmart.jsxtools.Mlog;

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


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
