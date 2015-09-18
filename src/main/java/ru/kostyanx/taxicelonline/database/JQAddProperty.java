/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;

/**
 *
 * @author kostyanx
 */
public class JQAddProperty extends JDatabaseQuery<Object> {
    private Integer orderId;
    private Integer propId;

    public JQAddProperty(Integer orderId, Integer propId) {
        this.orderId = orderId;
        this.propId = propId;
    }
    
    @Override
    public Object execute() throws JDatabaseException {
        String sql = "insert into CARPROPORD(CPORDID, CPPROPID) values(?, ?)";
        database.executeInsert2(sql, orderId, propId);
        return null;
    }
    
}
