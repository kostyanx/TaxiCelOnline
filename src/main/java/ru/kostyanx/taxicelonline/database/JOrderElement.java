/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.database;

import java.sql.Timestamp;
import ru.kostyanx.database.JDatabaseAbstract;
import ru.kostyanx.database.JDatabaseRecord2;

/**
 *
 * @author kostyanx
 */
public class JOrderElement extends JDatabaseRecord2<JOrderElement> {

	public JOrderElement(JDatabaseAbstract database) {
		super(database);
	}

	@Override
	public String getKeyColumn() {
		return "ORDID";
	}

	@Override
	public String[] getColumns() {
		String[] cols = {"ORDID", "ORDPHONE", "ORDSETTLEMENT", "ORDADDRESS",
			"ORDCLIENT", "ORDPRETYPE", "ORDPRETIME", "ORDCARID", "ORDDRVID", "ORDCARPLID",
			"ORDPLACE", "ORDDRVTERMID", "ORDTERMTIME", "ORDWANCARID", "ORDANYGRP",
			"ORDINET", "ORDCOMMENT", "ORDLATITUDE", "ORDLONGITUDE", "ORDCHAN",
			"ORDREACHTIME", "ORDAMNT"};
		return cols;
	}

	@Override
	public String getTable() {
		return "TORDER";
	}

	@Override
    protected void defaults() {
        chan("1");
        settlement("МОСКВА");
        inet(true);
        preTime(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));
        preOrder(false);
        anyGrp(false);
    }

    public Integer id() {
        return rs.getInt("ORDID");
    }

    public JOrderElement id(Integer id) {
        set("ORDID", id);
        return this;
    }

    public String phone() {
        String res = rs.getString("ORDPHONE");
        if (res != null) { res = res.trim(); }
        return res;
    }

    public JOrderElement phone(String phone) {
        set("ORDPHONE", phone);
        return this;
    }

    public String address() {
        return rs.getString("ORDADDRESS");
    }

    public JOrderElement address(String address) {
        set("ORDADDRESS", address);
        return this;
    }

    public String client() {
        return rs.getString("ORDCLIENT");
    }

    public JOrderElement client(String client) {
        set("ORDCLIENT", client);
        return this;
    }

    public Timestamp preTime() {
        return rs.getTimestamp("ORDPRETIME");
    }

    public JOrderElement preTime(Timestamp preTime) {
        set("ORDPRETIME", preTime);
        return this;
    }

    public boolean preOrder() {
        return "1".equals(rs.getString("ORDPRETYPE"));
    }

    public JOrderElement preOrder(boolean preOrder) {
        set("ORDPRETYPE", preOrder?"1":"0");
        return this;
    }

    public Integer carId() {
        return rs.getInt("ORDCARID");
    }

    public JOrderElement carId(Integer carId) {
        set("ORDCARID", carId);
        return this;
    }

    public Integer driverId() {
        return rs.getInt("ORDDRVID");
    }

    public JOrderElement driverId(Integer driverId) {
        set("ORDDRVID", driverId);
        return this;
    }

    public Integer drvPlace() {
        return rs.getInt("ORDCARPLID");
    }

    public JOrderElement drvPlace(Integer drvPlace) {
        set("ORDCARPLID", drvPlace);
        return this;
    }

    public String place() {
        return rs.getString("ORDPLACE");
    }

    public JOrderElement place(String place) {
        set("ORDPLACE", place);
        return this;
    }

    public Integer termId() {
        return rs.getInt("ORDDRVTERMID");
    }

    public JOrderElement termId(Integer termId) {
        set("ORDDRVTERMID", termId);
        return this;
    }

    public Timestamp termTime() {
        return rs.getTimestamp("ORDTERMTIME");
    }

    public JOrderElement termTime(Timestamp termTime) {
        set("ORDTERMTIME", termTime);
        return this;
    }

    public Integer group() {
        return rs.getInt("ORDWANCARID");
    }

    public JOrderElement group(Integer group) {
        set("ORDWANCARID", group);
        return this;
    }

    public boolean anyGrp() {
        return "1".equals(rs.getString("ORDANYGRP"));
    }

    public JOrderElement anyGrp(boolean anyGrp) {
        set("ORDANYGRP", anyGrp?"1":"0");
        return this;
    }

    public boolean inet() {
        return "1".equals(rs.getString("ORDINET"));
    }

    public JOrderElement inet(boolean anyGrp) {
        set("ORDINET", anyGrp?"1":"0");
        return this;
    }

    public String comment() {
        return rs.getString("ORDCOMMENT");
    }

    public JOrderElement comment(String comment) {
        set("ORDCOMMENT", comment);
        return this;
    }

    public String commentPerm() {
        String comment = rs.getString("ORDCOMMENT");
        if (comment != null && comment.contains("|")) {
            return comment.split("\\|")[0];
        }
        return comment;
    }

    public JOrderElement commentPerm(String comment) {
        String oldComment = rs.getString("ORDCOMMENT");
        if (oldComment != null && oldComment.contains("|")) {
            String[] commentArr = oldComment.split("\\|");
            commentArr[0] = comment;
            set("ORDCOMMENT", u.implode(commentArr, "|"));
        } else {
            set("ORDCOMMENT", comment);
        }
        return this;
    }

    public String commentTemp() {
        String comment = rs.getString("ORDCOMMENT");
        if (comment != null && comment.contains("|")) {
            return comment.split("\\|")[1];
        }
        return null;
    }

    public JOrderElement commentTemp(String comment) {
        String oldComment = rs.getString("ORDCOMMENT");
        if (oldComment != null && oldComment.contains("|")) {
            String[] commentArr = oldComment.split("\\|");
            commentArr[1] = comment;
            set("ORDCOMMENT", u.implode(commentArr, "|"));
        } else {
            set("ORDCOMMENT", u.coalesce(oldComment, "")+"|"+comment);
        }
        return this;
    }

    public JOrderElement appendCommentTemp(String text) {
        if (text == null || text.trim().equals("")) { return this; }
        String comment = commentTemp();
        if (comment == null || comment.trim().equals("")) { comment = text; }
        else { comment += ", "+text; }
        commentTemp(comment);
        return this;
    }

    public Double lat() {
        return rs.getDouble("ORDLATITUDE");
    }

    public JOrderElement lat(Double lat) {
        set("ORDLATITUDE", lat);
        return this;
    }

    public Double lon() {
        return rs.getDouble("ORDLONGITUDE");
    }

    public JOrderElement lon(Double lon) {
        set("ORDLONGITUDE", lon);
        return this;
    }

    public String settlement() {
        return rs.getString("ORDSETTLEMENT");
    }

    public JOrderElement settlement(String settlement) {
        set("ORDSETTLEMENT", settlement);
        return this;
    }

    public String chan() {
        return rs.getString("ORDCHAN");
    }

    public JOrderElement chan(String chan) {
        set("ORDCHAN", chan);
        return this;
    }

    public Timestamp reachTime() {
        return rs.getTimestamp("ORDREACHTIME");
    }

    public JOrderElement reachTime(Timestamp reachTime) {
        set("ORDREACHTIME", reachTime);
        return this;
    }

    public Float amount() {
        return rs.getFloat("ORDAMNT");
    }

    public JOrderElement amount(Float lat) {
        set("ORDAMNT", lat);
        return this;
    }

    public String infoString() {
        StringBuilder sb = new StringBuilder("order(");
        boolean first = true;
        for(String col : columns) {
            if (first) {
                sb.append(col).append("=%s");
                first = false;
            } else {
                sb.append(", ").append(col).append("=%s");
            }
        }
        sb.append(")");
        return String.format(sb.toString(), rs.row());
    }


}
