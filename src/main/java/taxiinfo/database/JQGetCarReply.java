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
public class JQGetCarReply extends JDatabaseQuery<LocalResultSet> {
    private Integer orderId;
    private Integer carId;

    public JQGetCarReply(Integer orderId, Integer carId) {
        this.orderId = orderId;
        this.carId = carId;
    }

    @Override
    public LocalResultSet execute() throws JDatabaseException {
        String sql = "select * from GETCARREPLY(?, ?)";
        return database.executeQuery(sql, orderId, carId);
    }
    
}
