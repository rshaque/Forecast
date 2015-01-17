package ca.rashadhaque.forecast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Weather extends Fragment {
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTempField;
    TextView weatherIcon;

    FrameLayout screen;
    Handler handler;

    public Weather() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTempField = (TextView)rootView.findViewById(R.id.current_temp_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        screen = (FrameLayout)rootView.findViewById(R.id.container);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTempField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ "℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch(Exception e) {
            Log.e("Forecast", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        String colour = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
                colour = "#FFCC00";
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
                colour = "#4747A3";
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    setBackground("#005CB8");
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    colour = "#005CB8";
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    colour = "#808080";
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    colour = "#808080";
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    colour = "#B3B3B3";
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    colour = "#005CB8";
                    break;
            }
        }
        setBackground(colour);
        weatherIcon.setText(icon);
    }

    public void setBackground(String colour) {
        getView().setBackgroundColor(Color.parseColor(colour));
    }

    public void changeCity(String city) {
        updateWeatherData(city);
    }
}
