package model.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.sql.LoadDriver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static model.util.SQLUtil.*;

public class BalancePage implements HttpHandler, Page {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = fillContent(new PageLoader().getData("balance.html"));
        he.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    @Override
    public String fillContent (String page) {
        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER("286628057551208450"));
        String gems = "0";
        try {
            rs.next();
            NumberFormat numFormat = new DecimalFormat();
            long balance = rs.getLong(1);
            if (balance < 0) {
                gems = "- ";
                page = page.replace("ff0000","FF6446");
            } else {
                gems = "+ ";
                page = page.replace("ff0000","64D264");
            }
            gems += numFormat.format(balance);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return page.replace("GEMS",gems);
    }
}
