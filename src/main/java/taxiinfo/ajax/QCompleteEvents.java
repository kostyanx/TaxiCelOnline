/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.json.jco;
import ru.kostyanx.json.jh;
import taxiinfo.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class QCompleteEvents implements JSONQuery {

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray events = jh._arr(request.getParameter("events"));
        String sid = request.getSession(true).getId();
        if (events != null) {
            for(Object o : events) {
                if (o instanceof Number) {
                    TaxiInfo.get().deleteEvent(((Number)o).intValue(), sid);
                }
            }
        }
        return jco.cput("result", "ok").get();
    }
    
}
