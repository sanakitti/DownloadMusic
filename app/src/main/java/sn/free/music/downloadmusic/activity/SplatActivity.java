package sn.free.music.downloadmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import sn.free.music.downloadmusic.R;

public class SplatActivity extends AppCompatActivity {
    Handler mHandler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splat);
        setFullScreenActivity(this);
        mHandler = new Handler();
        hideActionBar(this);

        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplatActivity.this, MainActivity.class));
                finish();
            }
        };
        mHandler.postDelayed(runnable, 3000);

    }

    public static void setFullScreenActivity(AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().getDecorView().setSystemUiVisibility(1280);
        if (Build.VERSION.SDK_INT >= 21) {
            appCompatActivity.getWindow().setStatusBarColor(1140850688);
        } else if (Build.VERSION.SDK_INT >= 19) {
            appCompatActivity.getWindow().setFlags(67108864, 67108864);
        }
    }

    public static void hideActionBar(AppCompatActivity appCompatActivity) {
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().hide();
        }
    }
}
