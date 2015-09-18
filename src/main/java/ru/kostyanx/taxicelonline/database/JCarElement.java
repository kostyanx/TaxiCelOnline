/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JCarElement extends JDatabaseRecord2<JCarElement> {

	public JCarElement(JDatabaseAbstract database) {
		super(database);
	}
	
	@Override
	public String getKeyColumn() {
		return "CARID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"CARID", "CARCALLID", "CARCAPTION", "CARMARK", "CARGOSNUM"};
		return cols;
	}

	@Override
	public String getTable() {
		return "CAR";
	}

	@Override
    protected void defaults() {

    }

    public Integer id() {
        return rs.getInt("CARID");
    }

    public String callId() {
        return rs.getString("CARCALLID");
    }

    public String mark() {
        String res = rs.getString("CARMARK");
        return (res == null ? null : res.trim());
    }

    public String color() {
        String res = rs.getString("CARCAPTION");
        return (res == null ? null : res.trim());
    }

    public String gosNum() {
        String res = rs.getString("CARGOSNUM");
        return (res == null ? null : res.trim());
    }

}
