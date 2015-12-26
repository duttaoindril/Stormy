package com.drillin.oindrildutta.stormy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private double LATITUDE = 37.3092040;
    private double LONGITUDE = -122.0333247;
    private final String API_KEY = "f53d47883f8a6d604248667fb3d0bb2b";
    private String URL = "https://api.forecast.io/forecast/" + API_KEY + "/" + LATITUDE + "," + LONGITUDE;

    //https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=image&foreground.space.trim=1&foreground.space.pad=0.2&foreColor=fff%2C0&crop=0&backgroundShape=circle&backColor=3c506b%2C100&effects=shadow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient weatherClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();

        Call call = weatherClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful())
                Log.v(TAG, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "FAILED to get weather: ", e);
        }

        Toast.makeText(getApplicationContext(), "Lat: "+LATITUDE, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Long: "+LONGITUDE, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString("KEY_FACT", fact);
        //outState.putInt("KEY_COLOR", color);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //fact = savedInstanceState.getString("KEY_FACT");
        //color = savedInstanceState.getInt("KEY_COLOR");
    }
}