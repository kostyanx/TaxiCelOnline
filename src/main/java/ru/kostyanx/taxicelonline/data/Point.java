/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.data;

import java.io.IOException;
import java.io.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONStreamAware;
import ru.kostyanx.json.jca;

/**
 *
 * @author kostyanx
 */
public class Point implements JSONAware, JSONStreamAware {    
    public double lat;
    public double lon;

    public Point() {
    }
    
    public Point(Point p) {
        this(p.lat, p.lon);
    }
    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
    public Point(double[] poi) {
        this(poi[0], poi[1]);
    }
    public boolean inside(Polygon p) {
        return p.containsPoint(this);
    }
    public void valueOf(Point p) {
        this.lat = p.lat;
        this.lon = p.lon;
    }

    @Override
    public String toString() {
        return "["+lat+","+lon+"]";
    }
    
    public JSONArray json() {
        return jca.cadd(lat).add(lon).get();
    }

    @Override
    public String toJSONString() {
        return json().toJSONString();
    }

    @Override
    public void writeJSONString(Writer writer) throws IOException {
        json().writeJSONString(writer);
    }
    
    
}
