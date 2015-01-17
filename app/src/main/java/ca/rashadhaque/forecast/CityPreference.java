package ca.rashadhaque.forecast;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Rashad on 1/16/2015.
 */
public class CityPreference {
    SharedPreferences pref;

    public CityPreference(Activity activity) {
        pref = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // default city is Toronto
    String getCity() {
        return pref.getString("city", "Toronto");
    }

    void setCity(String city) {
        pref.edit().putString("city", city).commit();
    }
}
