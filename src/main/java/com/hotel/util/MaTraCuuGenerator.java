package com.hotel.util;

import java.util.Random;

public class MaTraCuuGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public static String generate(int length) {
        StringBuilder sb = new StringBuilder("HTL"); // Tiền tố Hotel
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}