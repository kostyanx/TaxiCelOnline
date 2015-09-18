/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import ru.kostyanx.json.jh;
import ru.kostyanx.utils.KostyanxUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.taxi.database.JConstElement;
import taxiinfo.TaxiInfo;
import taxiinfo.data.Point;
import taxiinfo.data.TaxiDrvPlace;
import taxiinfo.data.TaxiPlaceResolver;
import taxiinfo.data.YandexPoint;
import taxiinfo.database.JTOLAirportElement;

/**
 *
 * @author kostyanx
 */
public class QCalcTariffAirport implements JSONQuery {
    private static Logger logger = Logger.getLogger(QCalcTariffAirport.class);
    private KostyanxUtil u = KostyanxUtil.get();
    private TaxiPlaceResolver plres = null;

    public QCalcTariffAirport() {
    }

    private String getConst(String name) throws JDatabaseException {
        JConstElement c = new JConstElement();
        c.getWhere(TaxiInfo.get().getDb(), "CNAME = ? and CCHAN = ?", name, "");
        if (c.getRs().size() > 0) {
            c.getRs().nextrow();
            return c.value();
        }
        return null;
    }

    private String getAirPortZone(String text, TaxiDrvPlace place) {
        text = text.toLowerCase();
        if (text.contains("аэропорт домодедово") || text.contains("аэропорт быково")) {
            return "Домодедово, Быково";
        }
        if (text.contains("аэропорт внуково")) {
            return "Внуково";
        }
        if (text.contains("аэропорт шереметьево")) {
            return "Шереметьево 1,2";
        }
        if (place.getName().contains(" ")) {
            return place.getName().split(" ")[0];
        }
        return null;
    }

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized(TaxiPlaceResolver.get()) {
            if (plres == null) {
                try {
                    TaxiPlaceResolver.get().init(TaxiInfo.get().getDb());
                    plres = TaxiPlaceResolver.get();
                } catch (JDatabaseException e) {}
            }
        }
        JSONObject order = jh._obj(request.getParameter("order"));
        // fields: address_src, address_dst, time, client_id, comment
        logger.info(order);
        String srcPoint = jh.path(order, "address_src/GeoObject/Point/pos").s();
        String dstPoint = jh.path(order, "address_dst/GeoObject/Point/pos").s();
        String srcName = jh.path(order, "address_src/GeoObject/metaDataProperty/GeocoderMetaData/text").s();
        String dstName = jh.path(order, "address_dst/GeoObject/metaDataProperty/GeocoderMetaData/text").s();
        YandexPoint src = new YandexPoint(srcPoint);
        YandexPoint dst = new YandexPoint(dstPoint);
        TaxiDrvPlace srcPl = plres.getPlace(new Point(src.lat(), src.lon()));
        TaxiDrvPlace dstPl = plres.getPlace(new Point(dst.lat(), dst.lon()));
        if (srcPl == null || dstPl == null) {
            return jco.cput("result", "error").put("error", "не удалось определить стоянку для одного из адресов").get();
        }
        String yaSrc = getAirPortZone(srcName, srcPl);
        String yaDst = getAirPortZone(dstName, dstPl);
        if (yaSrc == null || yaDst == null) {
            return jco.cput("result", "error").put("error", "не удалось определить зону для одного из адресов").get();
        }
        String options = request.getParameter("options");
        Float nacenka = TaxiInfo.get().calcNacenki(options);
        try {
            JTOLAirportElement tolae = new JTOLAirportElement(TaxiInfo.get().getTolDb());
            tolae.getWhere("YASRC = ? and YADST = ?", yaSrc, yaDst);
            if (tolae.getRs().size() > 0) {
                tolae.getRs().nextrow();
                JSONArray res = new JSONArray();
                res.add(jco.cput("name", "Тариф аэропорт").put("day", tolae.cost()+nacenka).put("night", tolae.cost()+nacenka).get());
                String dayStart = null, nightStart = null;
                try {
                    dayStart = getConst("TERMDAYSTARTTIME");
                    nightStart = getConst("TERMNIGHTSTARTTIME");
                } catch (JDatabaseException e) {}
                return jco.cput("result", "ok").put("data", res).put("nacenki", TaxiInfo.get().getNacenki(options))
                    .put("daystart", dayStart).put("nightstart", nightStart).get();
            }
            return jco.cput("result", "error").put("error", "невозможно определить стоимость").get();
        } catch (JDatabaseException e) {
            return jco.cput("result", "error").put("error", "internal server error").put("exception", e.toString()).get();
        }


    }

}
