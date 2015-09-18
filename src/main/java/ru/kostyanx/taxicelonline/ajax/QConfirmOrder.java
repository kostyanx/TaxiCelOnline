/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import ru.kostyanx.utils.KostyanxUtil;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.taxicelonline.TaxiInfoException;
import ru.kostyanx.taxicelonline.data.TOrderMonitoring;
import ru.kostyanx.taxicelonline.database.JCarElement;
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JTOLOrderElement;

/**
 *
 * @author kostyanx
 */
public class QConfirmOrder implements JSONQuery {
    private static Logger logger = Logger.getLogger(QConfirmOrder.class);
    private KostyanxUtil u = KostyanxUtil.get();

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer orderId = u.i(request.getParameter("order_id"));
        String sid = request.getSession(true).getId();
        Integer clientId = null;
        for(Cookie c : request.getCookies()) {
            if ("client_id".equals(c.getName())) {
                clientId = u.i(c.getValue());
            }
        }
        JSONObject carInfo = null;
        try {
            JTOLOrderElement tolorder = new JTOLOrderElement(TaxiInfo.get().getTolDb());
            if (orderId != null) {
                tolorder.getWhere(tolorder.getKeyColumn()+" = ?", orderId);
                if (tolorder.getRs().size() == 0) {
                    throw new TaxiInfoException("не найден запрошенный заказ");
                }
                tolorder.getRs().nextrow();
                if (!tolorder.sid().equals(sid) && !tolorder.clientId().equals(clientId)) {
                    throw new TaxiInfoException("Ошибка доступа - Вам запрещено редактировать этот заказ");
                }
            }
            if (tolorder.getRs().size() == 0 && clientId != null) {
                tolorder.getWhere("TOLSTATE in (0, 1, 2, 3, 4, 5) and TOLTOLCID = ?", clientId);
                if (tolorder.getRs().size() == 0) {
                    throw new TaxiInfoException("не найден запрошенный заказ");
                }
                tolorder.getRs().nextrow();
            }
            if (tolorder.getRs().size() == 0 && sid != null) {
                tolorder.getWhere(TaxiInfo.get().getTolDb(), "TOLSTATE in (0, 1, 2, 3, 4, 5) and TOLSID = ?", sid);
                if (tolorder.getRs().size() == 0) {
                    throw new TaxiInfoException("не найден запрошенный заказ");
                }
                tolorder.getRs().nextrow();
            }
            JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
            order.getById(tolorder.taxiId());
            order.termId(1).termTime(new Timestamp(System.currentTimeMillis()));
            order.save(TaxiInfo.get().getDb());
            tolorder.state(3);
            tolorder.save(TaxiInfo.get().getTolDb());
            JCarElement car = new JCarElement(TaxiInfo.get().getDb());
            car.getById(order.carId());
            int time = TOrderMonitoring.getAnswerTime(order);
            carInfo = new JSONObject();
            carInfo.put("mark", car.mark());
            carInfo.put("color", car.color());
            carInfo.put("number", u.digits(car.gosNum()));
            carInfo.put("time", String.format("%tR", new Date(order.termTime().getTime()+time * 1000)));
            TOrderMonitoring.sendTrackEvent(tolorder, order, true);
        } catch (JDatabaseException e) {
            return jco.cput("result", "error").put("error", "внутренняя ошибка сервера").get();
        } catch (TaxiInfoException e) {
            return jco.cput("result", "error").put("error", e.getMessage()).get();
        }
        return jco.cput("result", "ok").put("car", carInfo).get();
    }

}
