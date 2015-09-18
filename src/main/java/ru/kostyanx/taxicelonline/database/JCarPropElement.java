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
public class JCarPropElement extends JDatabaseRecord2<JCarPropElement> {

	public JCarPropElement(JDatabaseAbstract database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "CPID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"CPID", "CPNAME", "CPPUBLIC"};
		return cols;
	}

	@Override
	public String getTable() {
		return "CARPROP";
	}

    @Override
	protected void defaults() {

    }

    public Integer id() {
        return rs.getInt(keyColumnName);
    }

    public JCarPropElement id(Integer id) {
        set(keyColumnName, id);
        return this;
    }

    public String name() {
        return rs.getString("CPNAME");
    }

    public JCarPropElement name(String name) {
        set("CPNAME", name);
        return this;
    }

    public boolean publicc() {
        return "1".equals(rs.getString("CPPUBLIC"));
    }

    public JCarPropElement publicc(boolean publicc) {
        set("CPPUBLIC", publicc ? "1" : "0");
        return this;
    }




}
