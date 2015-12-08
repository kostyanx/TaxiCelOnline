/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import java.sql.Timestamp;
import ru.kostyanx.database.JDatabase;
import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JTOLOrderElement extends JDatabaseRecord2<JTOLOrderElement> {

	public JTOLOrderElement(JDatabase database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "TOLOID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"TOLOID", "TOLTAXIID", "TOLSID", "TOLSTATE", "TOLDATA", "TOLCREATED", "TOLCID"};
		return cols;
	}

	@Override
	public String getTable() {
		return "TOLORDERS";
	}

	@Override
    protected void defaults() {
        created(new Timestamp(System.currentTimeMillis()));
        state(0);
        data("{}");
    }

    public Integer id() {
        return rs.getInt(keyColumnName);
    }

    public JTOLOrderElement id(Integer id) {
        set(keyColumnName, id);
        return this;
    }

    public Integer taxiId() {
        return rs.getInt("TOLTAXIID");
    }

    public JTOLOrderElement taxiId(Integer taxiId) {
        set("TOLTAXIID", taxiId);
        return this;
    }

    public String sid() {
        return rs.getString("TOLSID");
    }

    public JTOLOrderElement sid(String sid) {
        set("TOLSID", sid);
        return this;
    }

    public Integer state() {
        return rs.getInt("TOLSTATE");
    }

    public JTOLOrderElement state(Integer state) {
        set("TOLSTATE", state);
        return this;
    }

    public Timestamp created() {
        return rs.getTimestamp("TOLCREATED");
    }

    public JTOLOrderElement created(Timestamp sid) {
        set("TOLCREATED", sid);
        return this;
    }

    public String data() {
        return rs.getString("TOLDATA");
    }

    public JTOLOrderElement data(String data) {
        set("TOLDATA", data);
        return this;
    }

    public Integer clientId() {
        return rs.getInt("TOLCID");
    }

    public JTOLOrderElement clientId(Integer clientId) {
        set("TOLCID", clientId);
        return this;
    }


}
