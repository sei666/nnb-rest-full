package ru.icerebro.nnbrestfull.database.JDBC.entities;

import java.util.*;

public class Item {

    private Map<String, DocLine> lines;

    public Item(String itemStr) {

        lines = new HashMap<>();

        String[] strs = itemStr.split("\u001E");

        for (String s:strs) {
            String[] cells = s.split("\u001F");

            List<String> list = new ArrayList<>(Arrays.asList(cells));

            lines.put(list.remove(0).trim(), new DocLine(list));
        }
    }

    public String getVal(String lineKey, Character cellKey){
        if (lines.containsKey(lineKey))
            return lines.get(lineKey).getVal(cellKey);
        else
            return null;
    }

    private class DocLine {

        private Map<Character, String> cells;

        DocLine(List<String> strs) {
            cells = new HashMap<>();

            for (String s:strs) {
                cells.put(s.charAt(0), s.substring(1));
            }
        }

        String getVal(Character key){
            return cells.getOrDefault(key, null);
        }
    }
}
