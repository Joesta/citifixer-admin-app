package com.dso30bt.project2019.engineerdashboard.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Joesta on 2019/06/01.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    private int coordinateId;
    private double latitude;
    private double longitude;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            if (this.latitude == ((Coordinates) obj).getLatitude() && this.longitude == ((Coordinates) obj).getLongitude()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}