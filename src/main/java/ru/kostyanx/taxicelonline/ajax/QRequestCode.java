/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.taxicelonline.database.JQSendSms;
import ru.kostyanx.taxicelonline.database.JTOLClientElement;

/**
 *
 * @author kostyanx
 */
public class QRequestCode implements JSONQuery{

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TaxiInfo ti = TaxiInfo.get();
        String phone = ti.digits(request.getParameter("phone"));
        String name = request.getParameter("name");
        if (name == null) { name = ""; }
        if (phone == null || phone.length() < 10) {
            return jco.cput("result", "error").put("error", "Неверно указан номер телефона, укажите номер в формате 8-код-номер").get();
        }
        phone = "8"+phone.substring(phone.length() - 10, phone.length());
        try {
            JTOLClientElement client = new JTOLClientElement(ti.getTolDb());
            client.getWhere("TOLCPHONE = ?", phone);
            if (client.getRs().size() > 0) { client.getRs().nextrow(); }
            client.phone(phone);
            client.confirmed(false);
            client.name(name);
            client.save(ti.getTolDb());
            ti.getDb().execute(new JQSendSms(phone, String.format("Код подтверждения: %s", client.code())));
        } catch (JDatabaseException e) {
            return jco.cput("result", "error").put("error", "внутренняя ошибка сервера").get();
        }
        return jco.cput("result", "ok").get();
    }

}
