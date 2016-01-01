package com.drillin.oindrildutta.stormy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String[] COLORS = {"#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e", "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50", "#f1c40f", "#e67e22", "#e74c3c", "#f39c12", "#d35400", "#c0392b", "#fc970b"};
    private final String API_KEY = "f53d47883f8a6d604248667fb3d0bb2b";
    //private final String TAG = MainActivity.class.getSimpleName();
    private final String COLORA_DATA = "A";
    private final String COLORB_DATA = "B";
    private final String MAIN_DATA = "JSON";
    private final String LAT_DATA = "LAT";
    private final String LONG_DATA = "LONG";

    @Bind(R.id.background) RelativeLayout background;
    @Bind(R.id.refresh) ImageView refresh;
    @Bind(R.id.loc) TextView loc;
    @Bind(R.id.alert) TextView alert;
    @Bind(R.id.currentIcon) ImageView currentIcon;
    @Bind(R.id.currentSummary) TextView currentSummary;
    @Bind(R.id.time) TextView time;
    @Bind(R.id.temp) TextView temp;
    @Bind(R.id.degree) ImageView degree;
    @Bind(R.id.humidity) TextView humidity;
    @Bind(R.id.humidityVal) TextView humidityVal;
    @Bind(R.id.precip) TextView precip;
    @Bind(R.id.precipVal) TextView precipVal;
    @Bind(R.id.clouds) TextView clouds;
    @Bind(R.id.cloudsVal) TextView cloudsVal;
    @Bind(R.id.wind) TextView wind;
    @Bind(R.id.windVal) TextView windVal;
    @Bind(R.id.minIcon) ImageView minIcon;
    @Bind(R.id.minSummary) TextView minSummary;
    @Bind(R.id.hourIcon) ImageView hourIcon;
    @Bind(R.id.hourSummary) TextView hourSummary;
    @Bind(R.id.dailySummary) TextView dailySummary;
    //TODO: ADD THE REST OF THE VIEWS TO SHOW DAILY AND HOURLY DATA

    private SharedPreferences.Editor prefEditor;
    private double longitude = -122.0333247;
    private double latitude = 37.3092040;
    private String url = "https://api.forecast.io/forecast/" + API_KEY + "/" + latitude + "," + longitude;
    private Random gen = new Random();
    private String jsonWeather;
    private int colorA;
    private int colorB;

    //https://api.forecast.io/forecast/f53d47883f8a6d604248667fb3d0bb2b/37.3092040,-122.0333247
    //https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=image&foreground.space.trim=1&foreground.space.pad=0.2&foreColor=fff%2C0&crop=0&backgroundShape=circle&backColor=3c506b%2C100&effects=shadow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("com.drillin.stormyapp.preferences", Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
        prefEditor.apply();
        jsonWeather = sharedPreferences.getString(MAIN_DATA, "");
        latitude = Double.parseDouble(sharedPreferences.getString(LAT_DATA, "37.3092040"));
        longitude = Double.parseDouble(sharedPreferences.getString(LONG_DATA, "-122.0333247"));
        colorA = Color.parseColor(COLORS[gen.nextInt(COLORS.length)]);
        do {
            colorB = Color.parseColor(COLORS[gen.nextInt(COLORS.length)]);
        } while(colorB == colorA);
        if(savedInstanceState != null) {
            colorA = savedInstanceState.getInt(COLORA_DATA, colorA);
            colorB = savedInstanceState.getInt(COLORB_DATA, colorB);
            jsonWeather = savedInstanceState.getString(MAIN_DATA, jsonWeather);
        }
        boom();
    }

    private void boom() {
        setUpViews();
        if((getIsLocationUpdated() || getIsTimeUpdated(jsonWeather, 1800)) && getNetworkAvailability())
            getForecast();
        else
            loadWeatherData(jsonWeather);
    }

    private void setUpViews() {
        ButterKnife.bind(this);

        //Set up On Click Listeners
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boom();
            }
        });

        //Set Colors
        //background.setBackgroundColor(color);
        background.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorA, colorB}));

        //Hide Everything
        background.setVisibility(View.INVISIBLE);
        loc.setVisibility(View.INVISIBLE);
        alert.setVisibility(View.INVISIBLE);
        currentIcon.setVisibility(View.INVISIBLE);
        currentSummary.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        temp.setVisibility(View.INVISIBLE);
        degree.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        humidityVal.setVisibility(View.INVISIBLE);
        precip.setVisibility(View.INVISIBLE);
        precipVal.setVisibility(View.INVISIBLE);
        clouds.setVisibility(View.INVISIBLE);
        cloudsVal.setVisibility(View.INVISIBLE);
        wind.setVisibility(View.INVISIBLE);
        windVal.setVisibility(View.INVISIBLE);
        minIcon.setVisibility(View.INVISIBLE);
        minSummary.setVisibility(View.INVISIBLE);
        hourIcon.setVisibility(View.INVISIBLE);
        hourSummary.setVisibility(View.INVISIBLE);
        dailySummary.setVisibility(View.INVISIBLE);

        //Animation of Refresh
        RotateAnimation anim = new RotateAnimation(0f, 350f, 18.4f, 18.4f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        refresh.startAnimation(anim);
    }

    @SuppressLint("SetTextI18n")
    private void setUpViewsPostData(WeatherModel weatherData) {
        ButterKnife.bind(this);
        AlphaAnimation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setDuration(850);
        fadeIn.setFillAfter(true);

        //Fill in all the Weather Model Data
        if(weatherData != null) {
            loc.setText(weatherData.getCity());
            alert.setText(weatherData.getAlert());
            currentIcon.setImageResource(weatherData.getIconId(weatherData.getCurrentIcon()));
            currentSummary.setText(weatherData.getCurrentSummary());
            time.setText("At " + weatherData.getFormattedTime() + " it is:");
            temp.setText(""+weatherData.getApparentTemperature());
            humidityVal.setText(weatherData.getHumidity()+"%");
            precipVal.setText(weatherData.getPrecipProbability()+"%");
            cloudsVal.setText(weatherData.getCloudCover()+"%");
            windVal.setText(weatherData.getWindSpeed()+"m/s");
            minIcon.setImageResource(weatherData.getIconId(weatherData.getMinIcon()));
            minSummary.setText(weatherData.getMinSummary());
            hourIcon.setImageResource(weatherData.getIconId(weatherData.getHourIcon()));
            hourSummary.setText(weatherData.getHourSummary());
            dailySummary.setText(weatherData.getDailySummary());
        } else
            alertUserAboutError("Oops!", "Something went horribly wrong... Restart the app.", "Ok, I got it!");

        //Show & Fade in Everything
        background.setVisibility(View.VISIBLE);
        background.startAnimation(fadeIn);
        loc.setVisibility(View.VISIBLE);
        loc.startAnimation(fadeIn);
        alert.setVisibility(View.VISIBLE);
        alert.startAnimation(fadeIn);
        currentIcon.setVisibility(View.VISIBLE);
        currentIcon.startAnimation(fadeIn);
        currentSummary.setVisibility(View.VISIBLE);
        currentSummary.startAnimation(fadeIn);
        time.setVisibility(View.VISIBLE);
        time.startAnimation(fadeIn);
        temp.setVisibility(View.VISIBLE);
        temp.startAnimation(fadeIn);
        degree.setVisibility(View.VISIBLE);
        degree.startAnimation(fadeIn);
        humidity.setVisibility(View.VISIBLE);
        humidity.startAnimation(fadeIn);
        humidityVal.setVisibility(View.VISIBLE);
        humidityVal.startAnimation(fadeIn);
        precip.setVisibility(View.VISIBLE);
        precip.startAnimation(fadeIn);
        precipVal.setVisibility(View.VISIBLE);
        precipVal.startAnimation(fadeIn);
        clouds.setVisibility(View.VISIBLE);
        clouds.startAnimation(fadeIn);
        cloudsVal.setVisibility(View.VISIBLE);
        cloudsVal.startAnimation(fadeIn);
        wind.setVisibility(View.VISIBLE);
        wind.startAnimation(fadeIn);
        windVal.setVisibility(View.VISIBLE);
        windVal.startAnimation(fadeIn);
        minIcon.setVisibility(View.VISIBLE);
        minIcon.startAnimation(fadeIn);
        minSummary.setVisibility(View.VISIBLE);
        minSummary.startAnimation(fadeIn);
        hourIcon.setVisibility(View.VISIBLE);
        hourIcon.startAnimation(fadeIn);
        hourSummary.setVisibility(View.VISIBLE);
        hourSummary.startAnimation(fadeIn);
        dailySummary.setVisibility(View.VISIBLE);
        dailySummary.startAnimation(fadeIn);

        //Stop Refresh Spin
        refresh.setAnimation(null);
    }

    private void loadWeatherData(String jsonWeather) {
        if(Objects.equals(jsonWeather, ""))
            alertUserAboutError("Oops!", "You need to turn your location/wifi on, then press the refresh button.", "Ok, I got it!");
        else if(getIsTimeUpdated(jsonWeather, 586083))
            alertUserAboutError("Oops!", "Turn your location/wifi on! Your cached weather data is very old!", "Ok, I got it!");
        WeatherModel weatherData = null;
        try {
            JSONObject data = new JSONObject(jsonWeather);
            JSONArray hourArray = data.getJSONObject("hourly").getJSONArray("data");
            WeatherHour[] hours = new WeatherHour[hourArray.length()];
            for(int i = 0; i < hourArray.length(); i++) {
                if(i < 49)
                    hours[i] = new WeatherHour(
                        (hourArray.getJSONObject(i)).getLong("time"),
                        (hourArray.getJSONObject(i)).getString("summary"),
                        (hourArray.getJSONObject(i)).getString("icon"),
                        (hourArray.getJSONObject(i)).getDouble("precipProbability"),
                        (hourArray.getJSONObject(i)).getDouble("temperature"),
                        (hourArray.getJSONObject(i)).getDouble("apparentTemperature"),
                        (hourArray.getJSONObject(i)).getDouble("humidity"),
                        (hourArray.getJSONObject(i)).getDouble("windSpeed"),
                        (hourArray.getJSONObject(i)).getDouble("cloudCover"));
            }
            JSONArray dayArray = data.getJSONObject("daily").getJSONArray("data");
            WeatherDay[] days = new WeatherDay[dayArray.length()];
            for(int i = 0; i < dayArray.length(); i++) {
                double cloud = 0.0;
                if((dayArray.getJSONObject(i)).has("cloudCover"))
                    cloud = (dayArray.getJSONObject(i)).getDouble("cloudCover");
                if(i < 8)
                    days[i] = new WeatherDay(
                        (dayArray.getJSONObject(i)).getLong("time"),
                        (dayArray.getJSONObject(i)).getString("summary"),
                        (dayArray.getJSONObject(i)).getString("icon"),
                        (dayArray.getJSONObject(i)).getDouble("temperatureMin"),
                        (dayArray.getJSONObject(i)).getLong("temperatureMinTime"),
                        (dayArray.getJSONObject(i)).getDouble("temperatureMax"),
                        (dayArray.getJSONObject(i)).getLong("temperatureMaxTime"),
                        (dayArray.getJSONObject(i)).getDouble("apparentTemperatureMin"),
                        (dayArray.getJSONObject(i)).getLong("apparentTemperatureMinTime"),
                        (dayArray.getJSONObject(i)).getDouble("apparentTemperatureMax"),
                        (dayArray.getJSONObject(i)).getLong("apparentTemperatureMaxTime"),
                        (dayArray.getJSONObject(i)).getLong("sunriseTime"),
                        (dayArray.getJSONObject(i)).getLong("sunsetTime"),
                        (dayArray.getJSONObject(i)).getDouble("precipIntensityMax"),
                        (dayArray.getJSONObject(i)).getDouble("precipProbability"),
                        (dayArray.getJSONObject(i)).getDouble("humidity"),
                        (dayArray.getJSONObject(i)).getDouble("windSpeed"), cloud);
            }
            String alert = "";
            if (data.has("alerts"))
                alert = data.getJSONArray("alerts").getJSONObject(0).getString("title");
            weatherData = new WeatherModel(
                    data.getDouble("latitude"),
                    data.getDouble("longitude"),
                    data.getString("timezone"),
                    alert,
                    data.getJSONObject("currently").getLong("time"),
                    data.getJSONObject("currently").getString("summary"),
                    data.getJSONObject("currently").getString("icon"),
                    data.getJSONObject("minutely").getString("summary"),
                    data.getJSONObject("minutely").getString("icon"),
                    data.getJSONObject("hourly").getString("summary"),
                    data.getJSONObject("hourly").getString("icon"),
                    data.getJSONObject("daily").getString("summary"),
                    data.getJSONObject("currently").getDouble("precipProbability"),
                    data.getJSONObject("currently").getDouble("apparentTemperature"),
                    data.getJSONObject("currently").getDouble("humidity"),
                    data.getJSONObject("currently").getDouble("windSpeed"),
                    data.getJSONObject("currently").getDouble("cloudCover"),
                    hours, days);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpViewsPostData(weatherData);
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
        return !locChanged;
    }

    private boolean getIsTimeUpdated(String jsonData, int check) {
        int time;
        if(Objects.equals("", jsonData))
            return true;
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

    private void getForecast() {
        OkHttpClient weatherClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = weatherClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                alertUserAboutError("Oops!", "Something went horribly wrong...", "Ok, I got it!");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        jsonWeather = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            loadWeatherData(jsonWeather);
                            }
                        });
                    } else
                        alertUserAboutError("Oops!", "Your data is all messed up!?", "Ok, I got it!");
                } catch (Exception e) {
                    e.printStackTrace();
                    alertUserAboutError("Oops!", "You need to turn your location/wifi on!", "Ok, I got it!");
                }
            }
        });
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MAIN_DATA, jsonWeather);
        outState.putInt(COLORA_DATA, colorA);
        outState.putInt(COLORB_DATA, colorB);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {return gestureDetector.onTouchEvent(event);}
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if((e2.getY() - e1.getY()) > 50)
                boom();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);
}