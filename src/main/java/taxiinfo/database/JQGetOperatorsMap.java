/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import java.util.HashMap;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGetOperatorsMap extends JDatabaseQuery<HashMap<Integer, String>>{

    @Override
    public HashMap<Integer, String> execute() throws JDatabaseException {
        String sql = "select OPID, OPCAPTION from OPERATOR";
        LocalResultSet rs = database.executeQuery(sql);
        HashMap<Integer, String> result = new HashMap<>();
        while(rs.nextrow()) {
            result.put(rs.getInt("OPID"), rs.getString("OPCAPTION"));
        }
        return result;
    }


}
