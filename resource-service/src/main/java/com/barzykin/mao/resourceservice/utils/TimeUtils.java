package com.barzykin.mao.resourceservice.utils;

public final class TimeUtils {

    private TimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String secondsAsDoubleToHMS(String seconds) {
        double s = Double.parseDouble(seconds);
        int h = (int) (s / 3600);
        int m = (int) (s % 3600) / 60;
        int sec = (int) Math.round(s % 60);
        return h == 0
                ? String.format("%02d:%02d", m, sec)
                : String.format("%02d:%02d:%02d", h, m, sec);
    }
}
