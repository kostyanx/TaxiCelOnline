/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JTOLAirportElement extends JDatabaseRecord2<JTOLAirportElement> {

	public JTOLAirportElement(JDatabaseAbstract database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "YAID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"YAID", "YASRC", "YADST", "YACOST", "YASRCTYPE", "YADSTTYPE"};
		return cols;
	}

	@Override
	public String getTable() {
		return "TOLAIRPORTS";
	}

	@Override
    protected void defaults() {

    }

    public Integer id() {
        return rs.getInt(keyColumnName);
    }

    public JTOLAirportElement id(Integer id) {
        set(keyColumnName, id);
        return this;
    }

    public String src() {
        return rs.getString("YASRC");
    }

    public JTOLAirportElement src(String src) {
        set("YASRC", src);
        return this;
    }

    public String dst() {
        return rs.getString("YADST");
    }

    public JTOLAirportElement dst(String dst) {
        set("YADST", dst);
        return this;
    }

    public Float cost() {
        return rs.getFloat("YACOST");
    }

    public JTOLAirportElement cost(Float cost) {
        set("YACOST", cost);
        return this;
    }

    public String srcType() {
        return rs.getString("YASRCTYPE");
    }

    public JTOLAirportElement srcType(String srcType) {
        set("YASRCTYPE", srcType);
        return this;
    }

    public String dstType() {
        return rs.getString("YADSTTYPE");
    }

    public JTOLAirportElement dstType(String dstType) {
        set("YADSTTYPE", dstType);
        return this;
    }


}
