/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.LocalResultSet;
import ru.kostyanx.json.jca;
import ru.kostyanx.json.jco;
import ru.kostyanx.utils.KostyanxUtil;
import taxiinfo.TaxiInfo;
import taxiinfo.database.JQGetOpenSmens;
import taxiinfo.database.JQGetOperatorsMap;

/**
 *
 * @author kostyanx
 */
public class QOperators implements JSONQuery {
    private static Logger logger = Logger.getLogger(QOperators.class);
    private KostyanxUtil u = KostyanxUtil.get();
    private TaxiInfo ti = TaxiInfo.get();

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date from = u.dp(Ajax.df, request.getParameter("from"));
        Date to = u.dp(Ajax.df, request.getParameter("to"));
        if (from == null || to == null) {
            return jco.cput("error", 1).put("error_text", "invalid input parameters").get();
        }
        Timestamp fromT = new Timestamp(from.getTime());
        Timestamp toT = new Timestamp(to.getTime());
        try {
            HashMap<Integer, String> operators = ti.getDb().execute(new JQGetOperatorsMap());
            HashMap<Integer, Integer> smens = ti.getDb().execute(new JQGetOpenSmens(fromT, toT));
            String sql = "select O.ORDOPID, COUNT(*) as ORDTOTAL, COUNT(nullif(TT.TERMISOK, '0')) as ORDSUCCESS from TORDER O"
                    +" left join TERMTYPE TT on (O.ORDDRVTERMID = TT.TERMID)"
                    + " where O.ORDTERMTIME >= ? and O.ORDTERMTIME < ? and O.ORDOPID is not null"
                    + " group by O.ORDOPID";
            LocalResultSet rs = ti.getDb().executeQuery(sql, fromT, toT);
            JSONArray result = new JSONArray();
            Integer opId;
            String operator;
            Integer smen;
            while(rs.nextrow()) {
                opId = rs.getInt("ORDOPID");
                operator = operators.get(opId);
                smen = smens.get(opId);
                result.add(jca.cadd(operator).add(rs.getInt("ORDTOTAL")).add(rs.getInt("ORDSUCCESS")).add(u.coalesce(smen, 0)).get());
            }
            return jco.cput("result", "ok").put("data", result).get();
        } catch (JDatabaseException e) {
            logger.info("error:", e);
        }
        return jco.cput("result", "ok").get();
    }

}
