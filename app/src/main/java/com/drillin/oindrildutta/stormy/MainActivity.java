package com.drillin.oindrildutta.stormy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "f53d47883f8a6d604248667fb3d0bb2b";
    //private final String TAG = MainActivity.class.getSimpleName();
    private final String MAIN_DATA = "JSON";
    private final String LAT_DATA = "LAT";
    private final String LONG_DATA = "LONG";
    private final String COLOR_DATA = "COLOR";

    private SharedPreferences.Editor prefEditor;
    private double longitude = -122.0333247;
    private double latitude = 37.3092040;
    private String url = "https://api.forecast.io/forecast/" + API_KEY + "/" + latitude + "," + longitude;
    private Random gen = new Random();
    private WeatherModel weatherData;
    private String jsonWeather;
    private int color;

    //https://api.forecast.io/forecast/f53d47883f8a6d604248667fb3d0bb2b/37.3092040,-122.0333247
    //https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=image&foreground.space.trim=1&foreground.space.pad=0.2&foreColor=fff%2C0&crop=0&backgroundShape=circle&backColor=3c506b%2C100&effects=shadow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        color = Color.argb(255, gen.nextInt(256), gen.nextInt(256), gen.nextInt(256));
        loadUIPrefs();
        getSharedPrefs();
        loadWeatherData();
    }

    private void alertUserAboutError(String title, String message, String button) {
        AlertDialogFragment dialog = new AlertDialogFragment(title, message, button);
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private boolean getIsLocationUpdated() {
        //Have a way to get the Latitude and Longitude
        boolean locChanged = false;
        double[] loc = new double[]{37.3092040, -122.0333247}; //TODO: IMPLEMENT ACTUAL GET LOCATION METHOD LOL!
        if(Math.abs(loc[0] - latitude) > 0.1 || Math.abs(loc[1] - longitude) > 0.1) {
            locChanged = true;
            latitude = loc[0];
            longitude = loc[1];
        }
        //Toast.makeText(getApplicationContext(), "Lat: "+latitude, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Long: "+longitude, Toast.LENGTH_SHORT).show();
        return locChanged;
    }

    private boolean getIsTimeUpdated(String jsonData, int check) {
        int time;
        try {
            JSONObject timing = new JSONObject(jsonData);
            time = timing.getJSONObject("currently").getInt("time");
        } catch (JSONException e) {
            e.printStackTrace();
            time = 0;
        }
        return (System.currentTimeMillis()/1000 - time) > check;
    }

    private boolean getNetworkAvailability() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private String getForecast() {
        final String[] data = {""};
        OkHttpClient weatherClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = weatherClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError("Oops!", "Something went horribly wrong... Restart the app.", "Ok, I got it!");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        data[0] = response.body().string();
                    } else
                        alertUserAboutError("Oops!", "You need to turn your location/wifi on!", "Ok, I got it!");
                } catch (Exception e) {
                    e.printStackTrace();
                    alertUserAboutError("Oops!", "You need to turn your location/wifi on!", "Ok, I got it!");
                }
            }
        });
        return data[0];
    }

    private void loadUIPrefs() { //TODO: ADD BUTTONS AND MORE VIEWS TO CUSTOMIZE DYNAMICALLY
        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        background.setBackgroundColor(color);
    }

    private void getSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.drillin.stormyapp.preferences", Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
        prefEditor.apply();
        jsonWeather = sharedPreferences.getString(MAIN_DATA, "");
        latitude = Double.parseDouble(sharedPreferences.getString(LAT_DATA, "37.3092040"));
        longitude = Double.parseDouble(sharedPreferences.getString(LONG_DATA, "-122.0333247"));
    }

    private void loadWeatherData() { //TODO: IMPLEMENT USAGE OF WEATHER DATA INTO UI
        if((getIsLocationUpdated() || getIsTimeUpdated(jsonWeather, 1800)) && getNetworkAvailability())
            jsonWeather = getForecast();
        else if(!getNetworkAvailability())
            alertUserAboutError("Oops!", "You need to turn your location/wifi on!", "Ok, I got it!");
        if(getIsTimeUpdated(jsonWeather, 586083))
            alertUserAboutError("Oops!", "Turn your location/wifi on! Your cached weather data is very old!", "Ok, I got it!");
        else
            weatherData = parseWeatherData(jsonWeather);
    }

    private WeatherModel parseWeatherData(String jsonData) { //TODO: IMPLEMENT ACTUAL JSON PARSE OF WEATHER MODEL + IMPLEMENT ALL WEATHER CLASSES
        return new WeatherModel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefEditor.putString(MAIN_DATA, jsonWeather);
        prefEditor.putString(LAT_DATA, "" + latitude);
        prefEditor.putString(LONG_DATA, "" + longitude);
        prefEditor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUIPrefs();
        getSharedPrefs();
        loadWeatherData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MAIN_DATA, jsonWeather);
        outState.putInt(COLOR_DATA, color);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        jsonWeather = savedInstanceState.getString(MAIN_DATA, "");
        color = savedInstanceState.getInt(COLOR_DATA, Color.argb(255, gen.nextInt(256), gen.nextInt(256), gen.nextInt(256)));
        loadUIPrefs();
        loadWeatherData();
    }
}