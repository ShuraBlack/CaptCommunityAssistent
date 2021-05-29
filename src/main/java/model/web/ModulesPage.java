package model.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import startup.DiscordBot;

import java.io.IOException;
import java.io.OutputStream;

public class ModulesPage implements HttpHandler, Page {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = fillContent(new PageLoader().getData("modules.html"));
        he.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    @Override
    public String fillContent (String page) {
        StringBuilder s = new StringBuilder();
        if (DiscordBot.INSTANCE.getCManager().commands.size() == 0) {
            s.append("Empty ...");
        } else {
            for (String name : DiscordBot.INSTANCE.getCManager().commands.keySet()) {
                s.append("- ").append(name).append("<br>");
            }
        }
        page = page.replace("PANELACTIV",s.toString());
        s = new StringBuilder();
        if (DiscordBot.INSTANCE.getCManager().deactivated.size() == 0) {
            s.append("Empty ...");
        } else {
            for (String name : DiscordBot.INSTANCE.getCManager().deactivated.keySet()) {
                s.append("- ").append(name).append("<br>");
            }
        }
        page = page.replace("PANELAVAILABLE",s.toString());
        return page;
    }
}
