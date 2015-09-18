/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.LocalResultSet;
import ru.kostyanx.json.jco;
import taxiinfo.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class QShowSid implements JSONQuery {
    private Logger logger = Logger.getLogger(QShowSid.class);
    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String sql = "select * from EVENTS";
            logger.info(sql);
            LocalResultSet rs = TaxiInfo.get().getIntDb().executeQuery(sql);
            while(rs.nextrow()) {
                logger.info(rs.getInt("ID")+":"+rs.getString("SID"));
            }
        } catch (JDatabaseException e) {}
        return jco.cput("sid", request.getSession(true).getId()).get();
    }

}
