package model.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.sql.LoadDriver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static model.util.SQLUtil.*;

public class PlaylistPage implements HttpHandler, Page {

    private static final String REPLACER = "PANELPLAYLIST";

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = fillContent(new PageLoader().getData("playlist.html"));
        he.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    @Override
    public String fillContent(String page) {
        String lists = "";

        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SELECTALLSONGS("286628057551208450"));
        try {
            Map<String, List<String>> playlists = new TreeMap<>();
            while (rs.next()) {
                String playlist = rs.getString(3);
                String song = rs.getString(4);
                if (playlists.containsKey(playlist)) {
                    playlists.get(playlist).add(song);
                } else {
                    List<String> newPlaylist = new LinkedList<>();
                    newPlaylist.add(song);
                    playlists.put(playlist, newPlaylist);
                }
            }
            lists = mapPlaylists(playlists);
        } catch (SQLException throwables) {
            ld.close();
            throwables.printStackTrace();
        }
        ld.close();
        return page.replace(REPLACER, lists);
    }

    public String mapPlaylists (Map<String, List<String>> playlists) {
        StringBuilder stringBuilder = new StringBuilder();
        if (playlists.size() == 0) {
            stringBuilder.append("No Playlists or songs");
        } else {
            for (Map.Entry<String, List<String>> entry : playlists.entrySet()) {
                stringBuilder.append("<b>").append(entry.getKey()).append(":</b><br>");
                entry.getValue().forEach(song -> stringBuilder.append(song).append("<br>"));
                stringBuilder.append("<br>");
            }
        }
        return stringBuilder.toString();
    }

}
