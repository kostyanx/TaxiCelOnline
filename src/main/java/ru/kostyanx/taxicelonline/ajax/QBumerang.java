/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.ajax;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.kostyanx.database.JDatabaseException;
import static ru.kostyanx.json.jco.JO;
import ru.kostyanx.utils.KostyanxUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.taxi.database.JConstElement;
import ru.kostyanx.taxicelonline.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class QBumerang implements JSONQuery {
    private KostyanxUtil u = KostyanxUtil.get();

    public QBumerang() {
    }

    private String getConst(String name) throws JDatabaseException {
        JConstElement c = new JConstElement();
        c.getWhere(TaxiInfo.get().getDb(), "CNAME = ? and CCHAN = ?", name, "");
        if (c.getRs().size() > 0) {
            c.getRs().nextrow();
            return c.value();
        }
        return null;
    }

    @Override
    public JSONObject execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray res = new JSONArray();
        String phone = request.getParameter("phone");
		if (phone == null) { return JO("result", "error"); }
		phone = KostyanxUtil.get().digits(phone);
		if (phone.length() > 10) { phone = phone.substring(phone.length() - 10, phone.length()); }
		String taxiPhone = "8"+phone;
		runProgram("bumerang", taxiPhone, "1");
        return JO("result", "ok");
    }

	private void runProgram(String... command) {
		try {
			new ProcessBuilder(command)
					.directory(new File(command[0]).getParentFile())
					.inheritIO().start();
		} catch (IOException e) {}
	}

    private Float calcCost(float up, float km, float stay, float dist, float time, float jamstTme) {
        return up + (dist / 1000.0F * km * 1.15F) + (float)((jamstTme - time) / 3600.0 * stay);
    }

}
