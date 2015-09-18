/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.data;

/**
 *
 * @author kostyanx
 */
public class TaxiArea implements ContainsPoint {
    Integer id;
    Integer placeId;
    String name;
    Boolean isOut;
    Polygon polygon;

    public TaxiArea(Integer id, Integer placeId, String name, Boolean isOut, Polygon polygon) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.isOut = isOut;
        this.polygon = polygon;
    }
    

    public Integer getId() {
        return id;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }
    
    public Boolean isOut() {
        return isOut;
    }

    public Polygon getPolygon() {
        return polygon;
    }
    
    @Override
    public boolean containsPoint(Point p) {
        return polygon.containsPoint(p);
    }
    
}
