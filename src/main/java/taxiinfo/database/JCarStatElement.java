/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JCarStatElement extends JDatabaseRecord2<JCarStatElement> {

	@Override
	public String getKeyColumn() {
		return "CSCARID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"CSCARID", "CARGPRSSESSION", "CARTAXON",
			"CARFREEZED", "CARFREEZETIME", "CARORDID", "CARPLCID", "CARPARKTIME",
			"CARDRVID", "CARSTARTTIME", "CARPHONECONF", "CARRECOV"};
		return cols;
	}

	@Override
	public String getTable() {
		return "CARSTAT";
	}

	@Override
	protected void defaults() {

    }

    public Integer id() {
        return rs.getInt("CSCARID");
    }

    public boolean taxOn() {
        return "1".equals(rs.getString("CARTAXON"));
    }

    public Integer orderId() {
        return rs.getInt("CARORDID");
    }


}
