package com.uzair.smarttravelmanagement.WeatherModule.Common;

import android.location.Location;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String OWAPI_KEY = "da5cd084c8ceff0d0f9471d78f41bceb";
    public static Location current_location = null;

    public static String cityName = "Abbottabad";

    public static String convertUnixToDate(int dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(int sunrise) {
        Date date = new Date(sunrise*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
