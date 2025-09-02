package com.ccpay.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {
    
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATETIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT);
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATETIME_FORMAT);
    
    private DateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
    
    public static LocalDateTime startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1).atStartOfDay();
    }
    
    public static LocalDateTime endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
    }
    
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }
    
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    public static boolean isExpired(LocalDateTime expiryTime) {
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    public static boolean isWithinDays(LocalDateTime dateTime, int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return dateTime.isAfter(threshold);
    }
    
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
    public static String formatDate(LocalDate date) {
        return date.format(ISO_DATE_FORMATTER);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(ISO_DATETIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, ISO_DATE_FORMATTER);
    }
    
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, ISO_DATETIME_FORMATTER);
    }
    
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }
}