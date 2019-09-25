package com.dso30bt.project2019.engineerdashboard.utils;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/05/30.
 */
public class NavUtil {
    /**
     * Do not create object of this util class
     */
    private NavUtil() {
    }

    public static void moveToNextActivity(Context packageContext, Class<? extends AppCompatActivity> destinationPackageContext, String... intentExtras) {
        if (intentExtras.length > 0 ) {
            Intent intentWithExtras = new Intent(packageContext, destinationPackageContext);
            intentWithExtras.putExtra("userType", Integer.valueOf(intentExtras[0])); // make type safe for strings.
            packageContext.startActivity(intentWithExtras);
        } else {
            packageContext.startActivity(new Intent(packageContext, destinationPackageContext));
        }
    }
}