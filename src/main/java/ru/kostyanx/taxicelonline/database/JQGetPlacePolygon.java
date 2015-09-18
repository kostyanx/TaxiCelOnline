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
public class JQGetPlacePolygon extends JDatabaseQuery<LocalResultSet> {
    private Integer placeId;

    public JQGetPlacePolygon(Integer placeId) {
        this.placeId = placeId;
    }

    @Override
    public LocalResultSet execute() throws JDatabaseException {
        String sql = "select PLORDER, PLLAT, PLLON from DRVPLACEPOINT"
                + " where PLID = ?"
                + " order by PLORDER";
        return database.executeQuery(sql, JP.ca(placeId));
    }
    
}
