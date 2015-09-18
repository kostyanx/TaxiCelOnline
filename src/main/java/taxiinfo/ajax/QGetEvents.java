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
import taxiinfo.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class QGetEvents implements JSONQuery {

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getSession(true).getId();
        JSONArray events = TaxiInfo.get().getEvents(sid);
        return jco.cput("result", "ok").put("data", events).get();
    }
    
}
