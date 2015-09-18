/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGenOrdId extends JDatabaseQuery<Integer> {

    @Override
    public Integer execute() throws JDatabaseException {
        String sql = "select gen_id(GENORDID,1) as ORDID from RDB$DATABASE";
        LocalResultSet rs = database.executeQuery(sql);
        if (rs.nextrow()) {
            return rs.getInt("ORDID");
        }
        throw new JDatabaseException("не удалось сгенерировать id заказа");
    }

}
