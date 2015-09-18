/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.data;

import java.sql.Timestamp;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.LocalResultSet;
import ru.kostyanx.json.jco;
import ru.kostyanx.taxi.database.JGpsElement;
import ru.kostyanx.utils.KostyanxUtil;
import taxiinfo.TaxiInfo;
import taxiinfo.database.JCarElement;
import taxiinfo.database.JCarStatElement;
import taxiinfo.database.JOrderElement;
import taxiinfo.database.JQGetCarReply;
import taxiinfo.database.JTOLOrderElement;

/**
 *
 * @author kostyanx
 */
public class TOrderMonitoring implements Runnable {
    private static Logger logger = Logger.getLogger(TOrderMonitoring.class);
    private static KostyanxUtil u = KostyanxUtil.get();

    @Override
    public synchronized void run() {
        try {
            unsafeRun();
        } catch (Exception e) {
            logger.error("ошибка в мониторинге заказов", e);
        }
    }

    private void unsafeRun() throws Exception {
        JTOLOrderElement tolorder = new JTOLOrderElement(TaxiInfo.get().getTolDb());
        tolorder.getWhere("TOLSTATE in (0, 1, 2, 3, 4, 5)");
        while(tolorder.getRs().nextrow()) {
            processOrder(tolorder);
        }
    }

    public static void processOrder(JTOLOrderElement tolorder) {
        try {
            JOrderElement order = new JOrderElement(TaxiInfo.get().getDb());
            order.getWhere(order.getKeyColumn()+" = ?", tolorder.taxiId());
            // не найден заказ в БД такси - отменим в БД таксионлайн
            if (order.getRs().size() == 0) {
                tolorder.state(-1);
                tolorder.save();
                return;
            }
            order.getRs().nextrow();
            // проверим, не сняли ли заказ
            if (order.termId() != null && ! TaxiInfo.get().getTermIsOk().get(order.termId()) ) {
                // если заказ сняли, то снимаем его и в БД таксионлайн
                tolorder.state(-1);
                tolorder.save();
                return;
            }
            switch(tolorder.state()) {
                case 0:
                    state0(tolorder, order);
                    break;
                case 1:
                    state1(tolorder, order);
                    break;
                case 2:
                    state2(tolorder, order);
                    break;
                case 3:
                    state3(tolorder, order);
                    break;
                case 4:
                    state4(tolorder, order);
                    break;
                case 5:
                    state5(tolorder, order);
                    break;
            }
        } catch (JDatabaseException e) {
            logger.error("ошибка при обработке заказа "+tolorder.id(), e);
        }
    }

    public static int getAnswerTime(JOrderElement order) throws JDatabaseException {
        LocalResultSet timeInfo = TaxiInfo.get().getDb().execute(new JQGetCarReply(order.id(), order.carId()));
        if (timeInfo.nextrow()) {
            if (timeInfo.getInt("CARTIME") != null) {
                return timeInfo.getInt("CARTIME");
            } else if (timeInfo.getString("CARANSWER") != null) {
                Integer minutes = u.i(u.digits(timeInfo.getString("CARANSWER")), 25);
                return minutes * 60;
            }
        }
        return 25 * 60;
    }

    public static void sendTrackEvent(JTOLOrderElement tolorder, JOrderElement order, boolean autoSize) throws JDatabaseException {
        JGpsElement gps = new JGpsElement(TaxiInfo.get().getDb());
        gps.getWhere("GLCARID = ? and GLTIME > ? order by GLTIME desc",
                order.carId(), new Timestamp(System.currentTimeMillis() - 5 * 60 * 1000));
        Point ordp = new Point(order.lat(), order.lon());
        Point carp = null;
        if (gps.getRs().nextrow()) {
            carp = new Point(gps.lat(), gps.lon());
        }
        TaxiInfo.get().putEvent(tolorder.sid(),
                jco.cput("event", "track")
                .put("order_point", ordp)
                .put("car_point", carp)
                .put("autosize", autoSize).get());
    }

    public static void sendStateEvent(JTOLOrderElement tolorder) {
        TaxiInfo.get().putEvent(tolorder.sid(), jco.cput("event", "state").put("state", tolorder.state()).get());
    }

    private static void state0(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {
        tolorder.state(1);
        tolorder.save(TaxiInfo.get().getTolDb());
        sendStateEvent(tolorder);
    }

    private static void state1(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {
        if (order.driverId() != null) {
            // TODO send event to client
            JCarElement car = new JCarElement(TaxiInfo.get().getDb());
            car.getById(order.carId());
            TaxiInfo.get().putEvent(tolorder.sid(),
                    jco.cput("event", "assigncar")
                    .put("car", new JSONObject()).lastO()
                        .put("mark", car.mark())
                        .put("number", TaxiInfo.get().digits(car.gosNum()))
                        .put("color", car.color())
                        .put("minutes", getAnswerTime(order) / 60)
                    .parentO()
                    .put("state", 2).get());
            tolorder.state(2);
            tolorder.save();
            sendStateEvent(tolorder);
        }
    }

    private static void state2(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {

    }

    private static void state3(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {
        sendTrackEvent(tolorder, order, false);
        JCarStatElement cse = new JCarStatElement();
        cse.getById(TaxiInfo.get().getDb(), order.carId());
        if (order.reachTime() != null || cse.taxOn()) {
            tolorder.state(3);
            tolorder.save(TaxiInfo.get().getTolDb());
            sendStateEvent(tolorder);
        }
    }

    private static void state4(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {
        sendTrackEvent(tolorder, order, false);
        JCarStatElement cse = new JCarStatElement();
        cse.getById(TaxiInfo.get().getDb(), order.carId());
        if (cse.taxOn()) {
            tolorder.state(4);
            tolorder.save(TaxiInfo.get().getTolDb());
            sendStateEvent(tolorder);
        }
    }

    private static void state5(JTOLOrderElement tolorder, JOrderElement order) throws JDatabaseException {
        sendTrackEvent(tolorder, order, false);
        JCarStatElement cse = new JCarStatElement();
        cse.getById(TaxiInfo.get().getDb(), order.carId());
        if ( (order.amount() != null && order.amount() > 0) || cse.orderId() == null ) {
            tolorder.state(5);
            tolorder.save(TaxiInfo.get().getTolDb());
            sendStateEvent(tolorder);
        }
    }

}
