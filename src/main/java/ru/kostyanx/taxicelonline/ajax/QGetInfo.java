/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import static ru.kostyanx.json.jco.JO;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JTOLClientElement;
import static ru.kostyanx.utils.KostyanxUtil.i;

/**
 *
 * @author kostyanx
 */
public class QGetInfo implements JSONQuery{
    private static final Logger logger = Logger.getLogger(QGetInfo.class);

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TaxiInfo ti = TaxiInfo.get();
        Integer clientId = i(request.getParameter("client_id"));
        if (clientId == null) { return JO("result", "error",    "error", "invalid client id"); }
        try {
            JTOLClientElement client = new JTOLClientElement(ti.getTolDb()).getById(clientId);
            int count = new JOrderElement(ti.getDb()).count(
                    "ORDPHONE = ? and (ORDTERMTIME is null or ORDTERMTIME > dateadd(minute, ?, current_timestamp))",
                    client.phone(), -10);
            if (count > 0) { return JO("result", "error",   "client", null,   "msg", "too many requests"); }
            return JO("result", "ok",    "client", client);
        } catch (JDatabaseException e) {
            return JO("result", "error",   "error", "internal server error");
        }
    }

}
