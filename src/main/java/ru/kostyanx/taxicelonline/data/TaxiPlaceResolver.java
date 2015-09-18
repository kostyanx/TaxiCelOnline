/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.data;

import java.util.Collection;
import java.util.LinkedList;
import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.LocalResultSet;
import org.apache.log4j.Logger;
import ru.kostyanx.taxicelonline.database.JQGetAreaPolygon;
import ru.kostyanx.taxicelonline.database.JQGetAreas;
import ru.kostyanx.taxicelonline.database.JQGetParkings;
import ru.kostyanx.taxicelonline.database.JQGetPlacePolygon;

/**
 *
 * @author kostyanx
 */
public class TaxiPlaceResolver {
    private static Logger logger = Logger.getLogger(TaxiPlaceResolver.class);
    private static class Holder {
        public static final TaxiPlaceResolver instance = new TaxiPlaceResolver();
    }

    public static TaxiPlaceResolver get() {
        return Holder.instance;
    }

    private LinkedList<TaxiDrvPlace> places;
    private LinkedList<TaxiArea> areas;

    public void init(JDatabaseAbstract db) throws JDatabaseException {
        loadPlaces(db);
        loadAreas(db);
    }

    // ищем связанную стоянку в списке стоянок по id простым перебором
    public TaxiDrvPlace getRelatedPlace(TaxiArea ta) {
        if (ta == null) { return null; }
        Integer placeId = ta.getPlaceId();
        for(TaxiDrvPlace place : places) {
            if (place.getId().equals(placeId)) {
                // если нашли - то больше не ищем, выходим из цикла
                return place;
            }
        }
        return null;
    }

    public TaxiDrvPlace getPlace(Point p) {
        TaxiDrvPlace pl = (TaxiDrvPlace)search(places, p);
        if (pl != null) { return pl; }
        // если не нашли стоянку - ищем подрайон
        TaxiArea ta = (TaxiArea)search(areas, p);
        // если нашли подрайон, то результат - связанная с ним стоянка
        if (ta != null) { return getRelatedPlace(ta); }
        return null;
    }

    private ContainsPoint search(Collection<? extends ContainsPoint> polygon, Point p) {
		for(ContainsPoint cp : polygon) {
			if (cp == null) { continue; }
			if (cp.containsPoint(p)) {
				return cp;
			}
		}
		return null;
	}

    public Polygon createPolygonPlace(LocalResultSet rs) {
        Polygon p = new Polygon();
        while(rs.nextrow()) {
            p.add(new Point(rs.getDouble("PLLAT"), rs.getDouble("PLLON")));
        }
        return p;
    }

    public Polygon createPolygonArea(LocalResultSet rs) {
        Polygon p = new Polygon();
        while(rs.nextrow()) {
            p.add(new Point(rs.getDouble("APLAT"), rs.getDouble("APLON")));
        }
        return p;
    }

    private void loadPlaces(JDatabaseAbstract db) throws JDatabaseException {
        places = new LinkedList<>();
        // TODO исправить условие на универсальное
        LocalResultSet pl = db.execute(new JQGetParkings("PLCHANS like '%1%'"));
        Polygon polygon;
        Integer plcid;
        while(pl.nextrow()) {
            plcid = pl.getInt("PLCID");
            polygon = createPolygonPlace(db.execute(new JQGetPlacePolygon(plcid)));
            places.add(new TaxiDrvPlace(plcid, polygon).setName(pl.getString("PLCCAPTION")));
        }
        logger.info("places loaded");
    }

    private void loadAreas(JDatabaseAbstract db) throws JDatabaseException {
        areas = new LinkedList<>();
        // TODO исправить условие на универсальное
        LocalResultSet area = db.execute(new JQGetAreas());
        Polygon polygon;
        Integer areaId, placeId;
        String areaName;
        boolean isOut;
        while(area.nextrow()) {
            areaId = area.getInt("ARID");
            areaName = area.getString("ARNAME");
            isOut = "1".equals(area.getString("ARISOUT"));
            placeId = area.getInt("ARPARKID");
            polygon = createPolygonArea(db.execute(new JQGetAreaPolygon(areaId)));
            areas.add(new TaxiArea(areaId, placeId, areaName, isOut, polygon));
        }
        logger.info("areas loaded");
    }
}
