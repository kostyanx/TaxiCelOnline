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
public class JQSendSms extends JDatabaseQuery<Object> {
    private String phone;
    private String text;

    public JQSendSms(String phone, String text) {
        this.phone = phone;
        this.text = text;
    }

    @Override
    public Object execute() throws JDatabaseException {
        String sql = "execute procedure SMSSEND2(?, ?)";
        database.executeUpdateInt(sql, phone, text);
        return null;
    }
    
}
