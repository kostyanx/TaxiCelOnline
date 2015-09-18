/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import taxiinfo.TaxiInfo;
import taxiinfo.TaxiInfoException;
import taxiinfo.database.JTOLClientElement;

/**
 *
 * @author kostyanx
 */
public class QConfirmPhone implements JSONQuery{
    private static Logger logger = Logger.getLogger(QConfirmPhone.class);

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TaxiInfo ti = TaxiInfo.get();
        String phone = ti.digits(request.getParameter("phone"));
        String code = request.getParameter("code");
        if (phone == null || phone.length() < 10 || code == null) {
            return jco.cput("result", "error").put("error", "invalid input parametes").get();
        }
        phone = "8"+phone.substring(phone.length() - 10, phone.length());
        JTOLClientElement client = null;
        try {
            client = new JTOLClientElement(ti.getTolDb());
            client.getWhere("TOLCPHONE = ?", phone);
            if (!client.getRs().nextrow()) { throw new TaxiInfoException("не найдено запроса на подтверждение этого номера"); }
            if (!client.code().equals(code)) { throw new TaxiInfoException("неверный код подтверждения"); }
            client.confirmed(true);
            client.save(ti.getTolDb());
            Cookie c = new Cookie("client_id", client.id().toString());
            c.setMaxAge(30 * 24 * 3600); // 30 дней
            c.setPath(request.getContextPath()+"/");
            response.addCookie(c);
        } catch (TaxiInfoException e) {
            return jco.cput("result", "error").put("error", e.getMessage()).get();
        } catch (JDatabaseException e) {
            return jco.cput("result", "error").put("error", "внутренняя ошибка сервера").get();
        }
        return jco.cput("result", "ok").put("client", client).get();
    }

}
