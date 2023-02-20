package com.hollysmart.jsxdemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hollysmart.jsxtools.Mlog;

public class NewMainActivity extends AppCompatActivity {


    private DsbridgeWebFragment dsbridgeWebFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        dsbridgeWebFragment = new DsbridgeWebFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, dsbridgeWebFragment, "dsbrige")
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (dsbridgeWebFragment != null) {
            if (!dsbridgeWebFragment.onPresssBack()) {
                moveTaskToBack(true);
            }
        } else {
            moveTaskToBack(true);
        }
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
