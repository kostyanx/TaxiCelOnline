/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.taxicelonline.TaxiInfoException;
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JTOLOrderElement;
import ru.kostyanx.utils.K;

/**
 *
 * @author kostyanx
 */
public class QCallOnAssign implements JSONQuery {
    
    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer orderId = K.i(request.getParameter("order_id"));
        String sid = request.getSession(true).getId();
        Integer clientId = null;
        for(Cookie c : request.getCookies()) {
            if ("client_id".equals(c.getName())) {
                clientId = K.i(c.getValue());
            }
        }
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
                tolorder.getWhere("TOLSTATE in (0, 1, 2, 3, 4) and TOLTOLCID = ?", clientId);
                if (tolorder.getRs().size() == 0) {
                    throw new TaxiInfoException("не найден запрошенный заказ");
                }
                tolorder.getRs().nextrow();
            }
            if (tolorder.getRs().size() == 0 && sid != null) {
                tolorder.getWhere("TOLSTATE in (0, 1, 2, 3, 4) and TOLSID = ?", sid);
                if (tolorder.getRs().size() == 0) {
                    throw new TaxiInfoException("не найден запрошенный заказ");
                }
                tolorder.getRs().nextrow();
            }
            JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
            order.getById(tolorder.taxiId());
            order.inet(false);
            order.save(TaxiInfo.get().getDb());
            tolorder.state(5);
            tolorder.save(TaxiInfo.get().getTolDb());
        } catch (JDatabaseException e) {
            return jco.cput("result", "error").put("error", "внутренняя ошибка сервера").get();
        } catch (TaxiInfoException e) {
            return jco.cput("result", "error").put("error", e.getMessage()).get();
        }
        return jco.cput("result", "ok").get();
    }

}
