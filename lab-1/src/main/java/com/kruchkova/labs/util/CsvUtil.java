package com.kruchkova.labs.util;

import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {

    private CsvUtil() {
        // Приватный конструктор для утилитарного класса
    }

    /**
     * Парсит строку CSV с учетом возможных кавычек
     */
    public static String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(currentToken.toString());
                currentToken = new StringBuilder();
            } else {
                currentToken.append(c);
            }
        }

        tokens.add(currentToken.toString());
        return tokens.toArray(new String[0]);
    }
}