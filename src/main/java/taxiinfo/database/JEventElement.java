/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import java.sql.Timestamp;
import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JEventElement extends JDatabaseRecord2<JEventElement> {

	public JEventElement(JDatabaseAbstract database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "ID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"ID", "SID", "CREATED", "DATA"};
		return cols;
	}

	@Override
	public String getTable() {
		return "EVENTS";
	}

	@Override
    protected void defaults() {
        created(new Timestamp(System.currentTimeMillis()));
    }

    public Integer id() {
        return rs.getInt(keyColumnName);
    }

    public JEventElement id(Integer id) {
        set(keyColumnName, id);
        return this;
    }

    public String sid() {
        return rs.getString("SID");
    }

    public JEventElement sid(String sid) {
        set("SID", sid);
        return this;
    }

    public Timestamp created() {
        return rs.getTimestamp("CREATED");
    }

    public JEventElement created(Timestamp sid) {
        set("CREATED", sid);
        return this;
    }

    public String data() {
        return rs.getString("DATA");
    }

    public JEventElement data(String data) {
        set("DATA", data);
        return this;
    }


}
