/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.utils.K;

/**
 *
 * @author kostyanx
 */
public class JQDialNumber2 extends JDatabaseQuery<Object> {
    private Integer line;
    private String phone;
    private Integer orderId;
    private Integer state;
    private Integer operation;

    public JQDialNumber2(Integer line, String phone, Integer orderId, Integer state, Integer operation) {
        this.line = line;
        this.phone = phone;
        this.orderId = orderId;
        this.state = state;
        this.operation = operation;
    }
    
    @Override
    public Object execute() throws JDatabaseException {
        String sql = "execute procedure DIALNUMBER2(?, ?, ?, ?, ?)";
        database.executeUpdateInt(sql, line, phone, orderId, K.s(state), K.s(operation));
        return null;
    }
    
}
