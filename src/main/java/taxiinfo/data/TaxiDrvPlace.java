/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.data;

/**
 *
 * @author kostyanx
 */
public class TaxiDrvPlace implements ContainsPoint {
    private Integer id;
    private String name;
    private Polygon polygon;

    public TaxiDrvPlace(Integer id, Polygon polygon) {
        this.id = id;
        this.polygon = polygon;
    }

    public Integer getId() {
        return id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public TaxiDrvPlace setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("place(id=%d,name=%s)", id, name);
    }

    @Override
    public boolean containsPoint(Point p) {
        return polygon.containsPoint(p);
    }
    
    
}
