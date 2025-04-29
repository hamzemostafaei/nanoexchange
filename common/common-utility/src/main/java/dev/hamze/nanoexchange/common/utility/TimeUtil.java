package dev.hamze.nanoexchange.common.utility;

@SuppressWarnings("unused")
public final class TimeUtil {

    public static String millisecondsToTime(long milliseconds) {
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long seconds = (milliseconds / 1000) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String millisecondsToTimeMills(long milliseconds) {
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long seconds = (milliseconds / 1000) % 60;
        long remainingMilliseconds = milliseconds % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, remainingMilliseconds);
    }

}
