/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import ru.kostyanx.taxi.database.JConstElement;
import ru.kostyanx.taxi.database.JTariffElement;
import ru.kostyanx.taxicelonline.TaxiInfo;
import ru.kostyanx.utils.K;

/**
 *
 * @author kostyanx
 */
public class QCalcTariff implements JSONQuery {

    public QCalcTariff() {
    }

    private String getConst(String name) throws JDatabaseException {
        JConstElement c = new JConstElement(TaxiInfo.get().getDb());
        c.getWhere("CNAME = ? and CCHAN = ?", name, "");
        if (c.getRs().size() > 0) {
            c.getRs().nextrow();
            return c.value();
        }
        return null;
    }

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray res = new JSONArray();
        Float dist = K.f(request.getParameter("dist"));
        Float time = K.f(request.getParameter("time"));
        Float jamsTime = K.f(request.getParameter("jamstime"));
        String options = request.getParameter("options");
        if (dist == null || time == null || jamsTime == null) {
            return jco.cput("result", "error").put("error", "invalid input parameters").get();
        }
        Float nacenka = TaxiInfo.get().calcNacenki(options);
        try {
            JTariffElement tariff = new JTariffElement(TaxiInfo.get().getDb());
            tariff.getWhere("TFCHAN = ? and TFACTIVE = ?", "1", "1");
            while(tariff.getRs().nextrow()) {
                if (tariff.up().equals(0F)) { continue; }
                if (tariff.daynight()) {
                    res.add(jco.cput("name", tariff.caption()).put("day", calcCost(tariff.up(), tariff.km(), tariff.stay(), dist, time, jamsTime) + nacenka)
                                                            .put("night", calcCost(tariff.up2(), tariff.km2(), tariff.stay2(), dist, time, jamsTime) + nacenka).get());
                } else {
                    res.add(jco.cput("name", tariff.caption()).put("day", calcCost(tariff.up(), tariff.km(), tariff.stay(), dist, time, jamsTime) + nacenka)
                                                            .put("night", calcCost(tariff.up(), tariff.km(), tariff.stay(), dist, time, jamsTime) + nacenka).get());
                }
            }
        } catch (JDatabaseException e) {}
        String dayStart = null, nightStart = null;
        try {
            dayStart = getConst("TERMDAYSTARTTIME");
            nightStart = getConst("TERMNIGHTSTARTTIME");
        } catch (JDatabaseException e) {}
        return jco.cput("result", "ok").put("data", res).put("nacenki", TaxiInfo.get().getNacenki(options))
            .put("daystart", dayStart).put("nightstart", nightStart).get();
    }

    private Float calcCost(float up, float km, float stay, float dist, float time, float jamstTme) {
//        return up + (dist / 1000.0F * km * 1.15F) + (float)((jamstTme - time) / 3600.0 * stay);
        return up + (dist / 1000.0F * km ) + (float)((jamstTme - time) / 3600.0 * stay);
    }

}
