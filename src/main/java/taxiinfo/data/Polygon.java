/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.data;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author kostyanx
 */
public class Polygon extends LinkedList<Point> implements ContainsPoint {
    private Point min = new Point(0, 0);
    private Point max = new Point(0, 0);
    /**
     * Создать пустой полигон.
     */
    public Polygon() {
    }
    
    /**
     * Создать полигон, состоящи из точек переданной коллекции.
     * Точки будут располагаться в том порядке, в котором они
     * располагались в переданной коллекции
     * @param c Набор точек
     */
    public Polygon(Collection<? extends Point> c) {
        super(c);
    }
    
    /**
     * Создать полигон, состоящи из точек переданной коллекции.
     * Точки будут располагаться в том порядке, в котором они
     * располагались в переданной коллекции
     * @param points Набор точек
     */
    public Polygon(double[][] points) {
        for(double[] poi : points) {
            this.add(new Point(poi));
        }
    }
    
    /**
     * Находится ли точка внутри полигона. Вычисляется по методу учёта числа пересечений
     * @param poi интересующая нас точка в виде массива из 2х чисел: lat, lon
     * @return true, если содержит, false если не содержит
     */
    public boolean containsPoint(double[] poi) {
        return containsPoint(new Point(poi));
    }
    
    /**
     * Находится ли точка внутри полигона. Вычисляется по методу учёта числа пересечений
     * @param lat - широта
     * @param lon - долгота
     * @return true, если содержит, false если не содержит
     */
    public boolean containsPoint(double lat, double lon) {
        return containsPoint(new Point(lat, lon));
    }
    
    /**
     * Находится ли точка внутри полигона. Вычисляется по методу учёта числа пересечений
     * @param p интересующая нас точка
     * @return true, если содержит, false если не содержит
     */
    @Override
    public boolean containsPoint(Point p) {
        if (this.isEmpty()) { return false; }
        Point prev = this.getLast();
        if (this.size() == 1) {
            return p.lon == prev.lon && p.lat == prev.lat;
        }
        int intersects = 0;
        double x, minx, maxx;
        Point tmp = prev;
        for(Point curr: this) {
            prev = tmp; tmp = curr;
            minx = Math.min(prev.lat, curr.lat);
            maxx = Math.max(prev.lat, curr.lat);
            if (p.lat < minx || p.lat > maxx) { continue; }
            if (p.lon == curr.lon && p.lat == curr.lat) { return true; } //точка лежит в углу многоугольника
            if (curr.lat == prev.lat && p.lat != curr.lat) { continue; }
            if (curr.lat == prev.lat)
                { x = p.lon; }
            else
                { x = (curr.lon - prev.lon)*((p.lat - prev.lat)/(curr.lat - prev.lat)) + prev.lon; }
            if ((x > prev.lon && x < curr.lon) || (x > curr.lon && x < prev.lon) || x == curr.lon ) {
                if (x == p.lon) {
                    return true; // точка лежит на стороне многоугольника
                } else if (x > p.lon) {
                    intersects++; // нашли пересечение луча проведённого из точки параллельно оси X в большую сторону (вправо)
                }
            }
        }
        return (intersects & 1) != 0; // проверяем на нечётность. если нечётно - true
    }
    
    public Polygon calcMaxMin() {
        if (this.isEmpty()) { return this; }
        Point first = this.getFirst();
        min.valueOf(first);
        max.valueOf(first);
        for(Point p : this) {
            if (p.lat < min.lat) { min.lat = p.lat; }
            if (p.lon < min.lon) { min.lon = p.lon; }
            if (p.lat > max.lat) { max.lat = p.lat; }
            if (p.lon > max.lon) { max.lon = p.lon; }
        }
        return this;
    }
    
    public Point min() {
        return min;
    }
    
    public Point max() {
        return max;
    }
}
