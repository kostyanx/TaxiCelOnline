/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import java.util.Arrays;
import java.util.LinkedList;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGetParkings extends JDatabaseQuery<LocalResultSet> {
    private String where;
    private Object[] args;

    public JQGetParkings(String where, Object... args) {
        this.where = where;
        this.args = args;
    }
    
    @Override
    public LocalResultSet execute() throws JDatabaseException {
        StringBuilder sql = new StringBuilder("select * from DRVPLACE where PARKING = ?");
        LinkedList<Object> values = new LinkedList<>();
        values.add("1");
        if (where != null && !where.isEmpty()) {
            sql.append(" and ").append(where);
            values.addAll(Arrays.asList(args));
        }
        return database.executeQuery(sql.toString(), values);
    }
    
}
