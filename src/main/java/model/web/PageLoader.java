package model.web;

import java.util.Scanner;

public class PageLoader {
    public String getData (String filename) {
        return new Scanner(getClass().getResourceAsStream("/pages/" + filename)).useDelimiter("\\Z").next();
    }
}
