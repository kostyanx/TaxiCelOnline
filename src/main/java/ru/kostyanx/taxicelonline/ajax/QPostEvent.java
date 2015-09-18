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
import ru.kostyanx.json.jco;
import ru.kostyanx.json.jh;
import ru.kostyanx.taxicelonline.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class QPostEvent implements JSONQuery{

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject event = jh._obj(request.getParameter("event"));
        if (event != null) {
            TaxiInfo.get().putEvent(request.getSession(true).getId(), event);
            return jco.cput("result", "ok").get();
        }
        return jco.cput("result", "error").get();
    }
    
}
