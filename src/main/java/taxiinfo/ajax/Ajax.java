/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiinfo.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.kostyanx.database.JDatabaseException;
import ru.kostyanx.json.jco;
import org.apache.log4j.Logger;
import taxiinfo.TaxiInfo;
/**
 *
 * @author kostyanx
 */
@WebServlet(name = "Ajax", urlPatterns = {"/ajax", "/ajax/*"}, loadOnStartup = 1)
public class Ajax extends HttpServlet {
    private static Logger logger = Logger.getLogger(Ajax.class);
    public static final  DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private HashMap<String, JSONQuery> queries = new HashMap<>();

    @Override
    public void init() throws ServletException {
        TaxiInfo.get().startThreads();
        queries.put("operators", new QOperators());
        queries.put("calctariff", new QCalcTariff());
        queries.put("calc_tariff_airport", new QCalcTariffAirport());
        queries.put("getevents", new QGetEvents());
        queries.put("complete_events", new QCompleteEvents());
        queries.put("postevent", new QPostEvent());
        queries.put("showsid", new QShowSid());
        queries.put("neworder", new QNewOrder());
        queries.put("cancel_order", new QCancelOrder());
        queries.put("confirm_order", new QConfirmOrder());
        queries.put("request_code", new QRequestCode());
        queries.put("confirm_phone", new QConfirmPhone());
        queries.put("call_on_assign", new QCallOnAssign());
		queries.put("bumerang", new QBumerang());
        try { TaxiInfo.get().loadTermTypes(); } catch (JDatabaseException e) {}
    }



    @Override
    public void destroy() {
        TaxiInfo.get().shutdown();
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
    }





    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String context = request.getContextPath();
            String uri = request.getRequestURI();
            int length = context.length() + 5;
            String query = (uri.length() <= length) ? "" : uri.substring(length + 1);
            if (query.endsWith("/")) { query = query.substring(0, query.length() - 1); }
            JSONQuery jq = queries.get(query);
            if (jq != null) {
                jq.execute(request, response).writeJSONString(out);
            } else {
                jco.cput("error", 404).put("error_text", "not found").get().writeJSONString(out);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
