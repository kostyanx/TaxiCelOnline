/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.data;

import java.util.Locale;
import ru.kostyanx.utils.KostyanxUtil;

/**
 *
 * @author kostyanx
 */
public class YandexPoint {
    private KostyanxUtil u = KostyanxUtil.get();
    private double lat;
    private double lon;

    public YandexPoint(String coord) {
        if (coord == null) { lat = 0.0D; lon = 0.0D; }
        String[] c = coord.split(" ");
        lon = u.coalesce(u.d(c[0]), 0D);
        lat = u.coalesce(u.d(c[1]), 0D);
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "[%8.6f,%8.6f]", lat, lon);
    }
    
}
