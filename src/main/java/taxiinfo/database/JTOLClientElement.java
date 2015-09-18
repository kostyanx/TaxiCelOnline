/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import java.io.IOException;
import java.io.Writer;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseRecord2;
import ru.kostyanx.json.jco;
import taxiinfo.TaxiInfo;

/**
 *
 * @author kostyanx
 */
public class JTOLClientElement extends JDatabaseRecord2<JTOLClientElement> implements JSONAware, JSONStreamAware {

	public JTOLClientElement(JDatabaseAbstract database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "TOLCID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"TOLCID", "TOLCPHONE", "TOLCCONFIRMED", "TOLCNAME", "TOLCCODE"};
		return cols;
	}

	@Override
	public String getTable() {
		return "TOLCLIENTS";
	}

	@Override
    protected void defaults() {
        confirmed(false);
        name("");
        code(TaxiInfo.get().genCode(3));
    }

    public Integer id() {
        return rs.getInt(keyColumnName);
    }

    public JTOLClientElement id(Integer id) {
        set(keyColumnName, id);
        return this;
    }

    public String phone() {
        return rs.getString("TOLCPHONE");
    }

    public JTOLClientElement phone(String phone) {
        set("TOLCPHONE", phone);
        return this;
    }

    public boolean confirmed() {
        return "1".equals(rs.getString("TOLCCONFIRMED"));
    }

    public JTOLClientElement confirmed(boolean confirmed) {
        set("TOLCCONFIRMED", confirmed ? "1" : "0");
        return this;
    }

    public String name() {
        return rs.getString("TOLCNAME");
    }

    public JTOLClientElement name(String name) {
        set("TOLCNAME", name);
        return this;
    }

    public String code() {
        return rs.getString("TOLCCODE");
    }

    public JTOLClientElement code(String code) {
        set("TOLCCODE", code);
        return this;
    }

    public JSONObject json() {
        return jco.cput("id", id()).put("phone", phone()).put("name", name()).put("code", code()).put("confirmed", confirmed()).get();
    }

    @Override
    public String toJSONString() {
        return json().toJSONString();
    }

    @Override
    public void writeJSONString(Writer writer) throws IOException {
        json().writeJSONString(writer);
    }



}
