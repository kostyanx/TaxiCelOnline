/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JDatabaseQuery;
import ru.kostyanx.database.JP;
import ru.kostyanx.database.LocalResultSet;

/**
 *
 * @author kostyanx
 */
public class JQGetAreaPolygon extends JDatabaseQuery<LocalResultSet> {
    private Integer areaId;

    public JQGetAreaPolygon(Integer areaId) {
        this.areaId = areaId;
    }

    @Override
    public LocalResultSet execute() throws JDatabaseException {
        String sql = "select APORDER, APLAT, APLON from AREAPOINT"
                + " where APID = ?"
                + " order by APORDER";
        return database.executeQuery(sql, JP.ca(areaId));
    }
    
}
