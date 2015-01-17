package ca.rashadhaque.forecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

/**
 * Created by Rashad on 1/16/2015.
 */
public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static JSONObject getJSON(Context context, String city) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String temp = "";
            while ((temp = reader.readLine()) != null) {
                json.append(temp).append("\n");
            }
            reader.close();

            JSONObject data = new JSONObject((json.toString()));

            // 404 if request is unsuccessful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        }
        catch(Exception e) {
            return null;
        }
    }
}
