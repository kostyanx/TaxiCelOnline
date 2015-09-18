/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import java.sql.Timestamp;
import java.util.HashMap;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGetOpenSmens extends JDatabaseQuery<HashMap<Integer, Integer>>{
    private Timestamp from;
    private Timestamp to;

    public JQGetOpenSmens(Timestamp from, Timestamp to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public HashMap<Integer, Integer> execute() throws JDatabaseException {
        String sql = "select SHDOPERSTART, COUNT(*) as CNT from DRVSHED"
                + " where SHDDRVSTART >= ? and SHDDRVSTART < ?"
                + " group by SHDOPERSTART";
        LocalResultSet rs = database.executeQuery(sql, from, to);
        HashMap<Integer, Integer> result = new HashMap<>();
        while(rs.nextrow()) {
            result.put(rs.getInt("SHDOPERSTART"), rs.getInt("CNT"));
        }
        return result;
    }


}
