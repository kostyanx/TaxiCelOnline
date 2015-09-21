/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JFirebirdDatabase;
import static ru.kostyanx.json.jco.JO;
import ru.kostyanx.json.jh;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.taxicelonline.TaxiInfoException;
import ru.kostyanx.taxicelonline.data.Point;
import ru.kostyanx.taxicelonline.data.TaxiDrvPlace;
import ru.kostyanx.taxicelonline.data.TaxiPlaceResolver;
import ru.kostyanx.taxicelonline.data.YandexPoint;
import ru.kostyanx.taxicelonline.database.JCarPropElement;
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JQAddProperty;
import ru.kostyanx.taxicelonline.database.JQDialNumber2;
import ru.kostyanx.taxicelonline.database.JTOLClientElement;
import ru.kostyanx.taxicelonline.database.JTOLOrderElement;
import ru.kostyanx.utils.KostyanxUtil;
import static ru.kostyanx.utils.KostyanxUtil.dp;
import static ru.kostyanx.utils.KostyanxUtil.empty2;
import static ru.kostyanx.utils.KostyanxUtil.fmt;
import static ru.kostyanx.utils.KostyanxUtil.i;
import static ru.kostyanx.utils.KostyanxUtil.implode;

/**
 *
 * @author kostyanx
 */
public class QNewOrder implements JSONQuery {
    private TaxiPlaceResolver plres = null;
    private Logger logger = Logger.getLogger(QNewOrder.class);
    private KostyanxUtil u = KostyanxUtil.get();

    private void processOptions(JOrderElement order, JSONArray options) throws JDatabaseException {
        if (options == null) { return; }
        for(Object el : options) {
            if (!(el instanceof String)) { continue; }
            processOption(order, (String)el, options);
        }
    }

    private void addProperty(JOrderElement order, String name) throws JDatabaseException {
        JCarPropElement carprop;
        carprop = new JCarPropElement(TaxiInfo.get().getDb());
        carprop.getWhere("CPNAME = ?", name);
        if (carprop.getRs().size() > 0) {
            carprop.getRs().nextrow();
            TaxiInfo.get().getDb().execute(new JQAddProperty(order.id(), carprop.id()));
        }
    }

    private void processOption(JOrderElement order, String option, JSONArray options) throws JDatabaseException {

        switch(option) {
            case "universal":
                order.appendCommentTemp("УНИВЕРСАЛ (+200)");
                addProperty(order, "универсал");
                break;
            case "miniven":
                order.appendCommentTemp("МИНИВЕН (+400)");
                addProperty(order, "минивен");
                break;
            case "zhivotnie":
                order.appendCommentTemp("ЖИВОТНЫЕ (+200)");
                break;
            case "lyzhi":
                if (!options.contains("universal")) {
                    order.appendCommentTemp("ЛЫЖИ (+150)");
                }
                break;
            case "airport":
                order.appendCommentTemp("ВСТРЕЧА В АЭРОПОРТУ (+100)");
                break;
            case "tablichka":
                order.appendCommentTemp("ВСТРЕЧА С ТАБЛИЧКОЙ (+100)");
                break;
            default:
                order.appendCommentTemp(option);
                logger.warn("нет обработчика для опции"+option);
                break;
        }
    }

	private double rcoord(double val) {
		return Math.round(val * 100_000F) / 100_000F;
	}

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TaxiInfo ti = TaxiInfo.get();
        JFirebirdDatabase db = ti.getDb();
        JFirebirdDatabase tolDb = ti.getTolDb();
        Properties cfg = ti.getConfig();
        synchronized(TaxiPlaceResolver.get()) {
            if (plres == null) {
                try {
                    TaxiPlaceResolver.get().init(db);
                    plres = TaxiPlaceResolver.get();
                } catch (JDatabaseException e) {}
            }
        }
        
        JSONObject order = jh._obj(request.getParameter("order"));
        // fields: address_src, address_dst, time, client_id, comment
        logger.info(order);
        String srcPoint = jh.path(order, "address_src/GeoObject/Point/pos").s();
//        String dstPoint = jh.path(order, "address_dst/GeoObject/Point/pos").s();
        String srcName = jh.path(order, "address_src/GeoObject/name").s();
        String premiseNumber = jh.search(order, "PremiseNumber").s();
        if (!empty2(premiseNumber)) {
            String thoroughfareName = jh.search(order, "ThoroughfareName").s();
            srcName = fmt("%s %s-", thoroughfareName, premiseNumber);
        } else {
            srcName = "*"+srcName;
        }
        String dstName = jh.path(order, "address_dst/GeoObject/name").s();
        String srcKind = jh.path(order, "address_src/GeoObject/metaDataProperty/GeocoderMetaData/kind").s();
        String dstKind = jh.path(order, "address_src/GeoObject/metaDataProperty/GeocoderMetaData/kind").s();
        String airportComment = null;
        String comment = jh.path(order, "comment").s();
        Boolean nolater = jh.b(order, "nolater", true);
        String time = jh.s(order, "time");
        JSONArray options = jh._arr(order, "options");
        String sid = request.getSession(true).getId();
        Integer clientId = jh.path(order, "client_id").i();
        YandexPoint yp = new YandexPoint(srcPoint);
        TaxiDrvPlace pl = plres.getPlace(new Point(yp.lat(), yp.lon()));

        JTOLOrderElement tolorder = new JTOLOrderElement(tolDb);
        tolorder.clientId(clientId);
        tolorder.sid(sid);
        try {
            if (!nolater && dp(Ajax.df, time) == null) { throw new TaxiInfoException("неверные входные парамеры"); }
            JTOLClientElement client = new JTOLClientElement(tolDb).getById(clientId);
            JOrderElement torder = new JOrderElement(db);
            Timestamp preTime = nolater ? new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000)
                        : new Timestamp(dp(Ajax.df, time).getTime());
            if ("airport".equals(srcKind) || "airport".equals(dstKind)) {
                JSONObject calcAirport = new QCalcTariffAirport().execute(request, response);
                if (calcAirport != null && "ok".equals(jh.s(calcAirport, "result"))) {
                    Float cost = jh.path(calcAirport, "data/0/day").f();
                    if (cost != null) {
                        airportComment = String.format("%1.0f", cost);
                        Float nacenki = ti.calcNacenki(implode(options, ","));
                        if (!nacenki.equals(0.0F)) {
                            airportComment += String.format("+%1.0f", nacenki);
                        }
                    }
                }
            }
            Integer operatorId = i(cfg.getProperty("taxi.user_id"));
            torder.id(db.execute(new JQGenOrdId()))
                    .inet(true)
                    .phone(client.phone())
                    .client(client.name()+" (сайт)")
                    .address(srcName).lat( rcoord(yp.lat()) ).lon( rcoord(yp.lon()) )
                    .drvPlace(pl == null ? u.i(ti.getConfig().getProperty("taxi.other_place_id")) : pl.getId())
                    .preOrder(!nolater)
                    .anyGrp(!nolater)
                    .preTime(preTime)
                    .opId(operatorId)
                    .appendCommentTemp(airportComment)
                    .appendCommentTemp(comment)
                    .appendCommentTemp("едут до: "+dstName)
                    .appendCommentTemp(nolater ? null : "сверить интернет-заказ с клиентом!");
            processOptions(torder, options);
            torder.insert();
            tolorder.taxiId(torder.id());
            tolorder.save();
            // ставим на дозвон для соединения с оператором, если заказ предварительный
            if (!nolater) {
                db.execute(new JQDialNumber2(0, torder.phone(), torder.id(), 0, 1));
            }
            logger.info(String.format("coord=%s, place=%s, src=%s, dst=%s, client_id=%s, sid=%s", yp, pl, srcName, dstName, clientId, sid));
            ti.onOrderCreated(torder);
            return JO("result", "ok", "order_id", tolorder.id());
        } catch (JDatabaseException e) {
            logger.error("ошибка при создании заказа",e);
            return JO("result", "error", "error", "внутренняя ошибка сервера");
        } catch (TaxiInfoException e) {
            return JO("result", "error", "error", e.getMessage());
        }
        
    }
}
