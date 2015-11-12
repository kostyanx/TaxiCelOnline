package ru.kostyanx.taxicelonline;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.database.JFirebirdDatabase;
import ru.kostyanx.database.JH2Database;
import ru.kostyanx.json.jco;
import ru.kostyanx.taxi.database.JTermTypeElement;
import ru.kostyanx.taxicelonline.data.JSONString;
import ru.kostyanx.taxicelonline.data.TOrderMonitoring;
import ru.kostyanx.taxicelonline.database.JEventElement;
import ru.kostyanx.taxicelonline.database.JOrderElement;
import ru.kostyanx.taxicelonline.database.JQSendSms;
import ru.kostyanx.utils.Config;
import ru.kostyanx.utils.K;
import static ru.kostyanx.utils.KostyanxUtil.df;
import static ru.kostyanx.utils.KostyanxUtil.empty2;
import static ru.kostyanx.utils.KostyanxUtil.fillVariables;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostyanx
 */
public class TaxiInfo {
    public static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
    /* вспомогательные функции */
    static {
        PropertyConfigurator.configure(TaxiInfo.class.getResource("/log4j.properties"));
    }

    private static class Holder {
        public static final TaxiInfo instance = new TaxiInfo();
    }

    public static TaxiInfo get() {
        return Holder.instance;
    }
    /* ######################## */

    public final String version = "1.0.0";
    private JFirebirdDatabase db;
    private JFirebirdDatabase tolDb;
    private JH2Database intDb;
    private Properties config = new Properties();
    private Logger logger = Logger.getLogger(TaxiInfo.class);
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<JSONObject>> eventQueue = new ConcurrentHashMap<>();
    private ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(3);
    private Random random = new Random(System.currentTimeMillis());
    private HashMap<Integer, Boolean> termIsOk = new HashMap<>();
    private HashMap<String, Float> nacenki = new HashMap<>();
    private HashMap<String, String> nacenkiName = new HashMap<>();

    private TaxiInfo() {
        try {
            config.load(new InputStreamReader(TaxiInfo.class.getResourceAsStream("/config.properties"), "UTF-8"));
            config.load(new InputStreamReader(new FileInputStream("/etc/taxi/taxicelonline.properties"), "UTF-8"));
        } catch (IOException e) {}

        String host = config.getProperty("database.taxi.host");
        Integer port = K.i(config.getProperty("database.taxi.port"), 3050);
        String username = config.getProperty("database.taxi.username");
        String password = config.getProperty("database.taxi.password");
        String dbname = config.getProperty("database.taxi.dbname");
        db = new JFirebirdDatabase(host, port, username, password, dbname, "WIN1251", null);
        db.setQueryLimit(100);

        host = config.getProperty("database.tol.host");
        port = K.i(config.getProperty("database.tol.port"), 3050);
        username = config.getProperty("database.tol.username");
        password = config.getProperty("database.tol.password");
        dbname = config.getProperty("database.tol.dbname");
        tolDb = new JFirebirdDatabase(host, port, username, password, dbname, "WIN1251", null);
        tolDb.setQueryLimit(100);

        String[] databaseShema = {
            "CREATE TABLE EVENTS ("
                + "ID INT NOT NULL AUTO_INCREMENT,"
                + " SID VARCHAR(32) NOT NULL,"
                + " CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + " DATA VARCHAR(255) NOT NULL,"
                + " PRIMARY KEY(ID))",
            "CREATE INDEX ON EVENTS(SID)"
        };
        this.intDb = new JH2Database("taxiinfo", Arrays.asList(databaseShema));
        this.intDb.debug = false;

        nacenki.put("universal", 200.0F);
        nacenki.put("miniven", 400.0F);
        nacenki.put("miniwan", 400.0F);
        nacenki.put("zhivotnie", 200.0F);
        nacenki.put("animals", 200.0F);
        nacenki.put("lyzhi", 150.0F);
        nacenki.put("ski", 150.0F);
        nacenki.put("airport", 100.0F);
        nacenki.put("tablichka", 100.0F);
        nacenki.put("railway", 100.0F);

        nacenkiName.put("universal", "Заказ а/м универсал");
        nacenkiName.put("miniven", "Заказ минивена");
        nacenkiName.put("miniwan", "Заказ минивена");
        nacenkiName.put("zhivotnie", "Перевозка животных");
        nacenkiName.put("animals", "Перевозка животных");
        nacenkiName.put("lyzhi", "Перевозка лыж");
        nacenkiName.put("ski", "Перевозка лыж");
        nacenkiName.put("airport", "Встреча в аэропорту");
        nacenkiName.put("tablichka", "Встреча с табличкой");
        nacenkiName.put("railway", "Встреча на вокзале");
    }

    public Float calcNacenki(String options) {
        Float nacenka = 0.0F;

        if (options != null && !options.isEmpty()) {
            List<String> optionList = Arrays.asList(options.split(","));
            for(String option : optionList) {
                if (nacenki.containsKey(option)) {
                    if ("lyzhi".equals(option) && optionList.contains("universal")) { continue; }
                    nacenka += nacenki.get(option);
                }
            }
        }
        return nacenka;
    }

    public JSONArray getNacenki(String options) {
        JSONArray nacenka = new JSONArray();
        if (options != null && !options.isEmpty()) {
            List<String> optionList = Arrays.asList(options.split(","));
            for(String option : optionList) {
                if (nacenki.containsKey(option)) {
                    if ("lyzhi".equals(option) && optionList.contains("universal")) {
                        nacenka.add(jco.cput("name", nacenkiName.get(option)).put("cost", 0.0F).get());
                    } else {
                        nacenka.add(jco.cput("name", nacenkiName.get(option)).put("cost", nacenki.get(option)).get());
                    }
                }
            }
        }
        return nacenka;
    }

    public JFirebirdDatabase getDb() {
        return db;
    }

    public JH2Database getIntDb() {
        return intDb;
    }
    
    public JFirebirdDatabase getTolDb() {
        return tolDb;
    }

    public void disconnect() {
        db.disconnect();
        intDb.disconnect();
        tolDb.disconnect();
    }
    

    public void putEvent(String session, JSONObject event) {
        JEventElement ev = new JEventElement(intDb);
        ev.sid(session);
        ev.data(event.toJSONString());
        try { ev.save(); } catch(JDatabaseException e){logger.error("не удалось сохранить событие", e);}
    }

    public JSONArray getEvents(String session) {
        JSONArray res = new JSONArray();
        try {
            JEventElement ev = new JEventElement(intDb);
            ev.getWhere("SID = ?", session);
            while(ev.getRs().nextrow()) {
                JSONObject o = new JSONObject();
                o.put("id", ev.id());
                o.put("data", new JSONString(ev.data()));
                res.add(o);
            }
        } catch (JDatabaseException e) {}
        return res;
    }

    public void deleteEvent(Integer eventId, String session) {
        try {
            JEventElement ev = new JEventElement(intDb);
            ev.getById(eventId);
            if (session == null || session.equals(ev.sid())) {
                ev.delete();
            }
        } catch (JDatabaseException e) {}
    }

    public String genCode(int digits) {
        if (digits < 1) { digits = 1; }
        if (digits > 9) { digits = 9; }
        int max = 10;
        for(int i = 1; i < digits; i++) {
            max *= 10;
        }
        String fmt = String.format("%%0%dd", digits);
        return String.format(fmt, random.nextInt(max));
    }

    public String digits(String str) {
        if (str == null) { return null; }
        StringBuilder sb = new StringBuilder();
        for(char c: str.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void startThreads() {
        threadPool.scheduleAtFixedRate(new TOrderMonitoring(), 10, 10, TimeUnit.SECONDS);
    }

    public void shutdown() {
        threadPool.shutdown();
        disconnect();
    }

    public void loadTermTypes() throws JDatabaseException {
        JTermTypeElement tt = new JTermTypeElement(TaxiInfo.get().getDb()).getAll();
        while(tt.getRs().nextrow()) {
            termIsOk.put(tt.id(), tt.isOk());
        }
        logger.info("виды завершения загружены");
    }

    public HashMap<Integer, Boolean> getTermIsOk() {
        return termIsOk;
    }

    public Properties getConfig() {
        return config;
    }
    
    private void sendMessage(String phone, String text) {
        try {
            db.execute(new JQSendSms(phone, text));
        } catch (JDatabaseException e) {
            logger.error("can't send sms to client", e);
        }
    }
    
    private void sendOrderMessage(JOrderElement order, String template) {
        Config variables = Config.as(
                "client", order.client(),
                "address", order.address(),
                "pretime", df(tf, order.preTime()));
        String message = fillVariables(template, variables, false);
        sendMessage(order.phone(), message);
    }
    
    private void msgOnCreate(JOrderElement order) {
        String template = order.preOrder() ? config.getProperty("taxi.messages.on_create_pre") : config.getProperty("taxi.messages.on_create_curr");
        if (empty2(template)) { return; }
        sendOrderMessage(order, template);
    }
    
    private void msgOnCancel(JOrderElement order) {
        String template = config.getProperty("taxi.messages.on_cancel");
        if (empty2(template)) { return; }
        sendOrderMessage(order, template);
    }
    
    public void onOrderCreated(JOrderElement order) {
        msgOnCreate(order);
    }

    public void onOrderCancel(JOrderElement order) {
        msgOnCancel(order);
    }

}
