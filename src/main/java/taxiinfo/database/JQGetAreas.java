/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.database;

import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGetAreas extends JDatabaseQuery<LocalResultSet> {
    
    @Override
    public LocalResultSet execute() throws JDatabaseException {
        String sql = "select * from AREA";
        return database.executeQuery(sql);
    }
    
}
