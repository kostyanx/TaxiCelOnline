/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import java.sql.Timestamp;
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
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JTOLOrderElement;

/**
 *
 * @author kostyanx
 */
public class QCancelOrder implements JSONQuery {
    private static Logger logger = Logger.getLogger(QCancelOrder.class);
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
        try { corder: {
            if (orderId != null) {
                JTOLOrderElement tolorder = new JTOLOrderElement(TaxiInfo.get().getTolDb());
                tolorder.getById(orderId);
                if (tolorder.sid().equals(sid) || tolorder.clientId().equals(clientId)) {
                    JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
                    order.getById(tolorder.taxiId());
                    order.termId(10021); // сняли заказ
                    order.termTime(new Timestamp(System.currentTimeMillis()));
                    order.save(TaxiInfo.get().getDb());
                    tolorder.state(-1);
                    tolorder.save(TaxiInfo.get().getTolDb());
                    break corder;
                } else {
                    return jco.cput("result", "error")
                            .put("error", "Ошибка идентификации - Вам запрещено редактировать этот заказ").get();
                }
            }
            if (clientId != null) {
                JTOLOrderElement tolorder = new JTOLOrderElement(TaxiInfo.get().getTolDb());
                tolorder.getWhere("TOLSTATE != ? and TOLTOLCID = ?", 6, clientId);
                if (tolorder.getRs().size() > 0) {
                    tolorder.getRs().nextrow();
                    JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
                    order.getById(tolorder.taxiId());
                    order.termId(10021); // сняли заказ
                    order.termTime(new Timestamp(System.currentTimeMillis()));
                    order.save(TaxiInfo.get().getDb());
                    tolorder.state(-1);
                    tolorder.save(TaxiInfo.get().getTolDb());
                    break corder;
                } else {
                    return jco.cput("result", "error")
                            .put("error", "Не найдено подходящего заказа для отмены").get();
                }
            }
            if (sid != null) {
                JTOLOrderElement tolorder = new JTOLOrderElement(TaxiInfo.get().getTolDb());
                tolorder.getWhere("TOLSTATE != ? and TOLSID = ?", 6, sid);
                if (tolorder.getRs().size() > 0) {
                    tolorder.getRs().nextrow();
                    JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
                    order.getById(tolorder.taxiId());
                    order.termId(10021); // сняли заказ
                    order.termTime(new Timestamp(System.currentTimeMillis()));
                    order.save(TaxiInfo.get().getDb());
                    tolorder.state(-1);
                    tolorder.save(TaxiInfo.get().getTolDb());
                    break corder;
                } else {
                    return jco.cput("result", "error")
                            .put("error", "Не найдено подходящего заказа для отмены").get();
                }
            }
        }} catch (JDatabaseException e) {
            logger.error("ошибка при отмене заказа", e);
        }
        return jco.cput("result", "ok").get();
    }

}
