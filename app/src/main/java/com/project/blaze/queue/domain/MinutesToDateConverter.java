package com.project.blaze.queue.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MinutesToDateConverter {

    public MinutesToDateConverter() {
    }

    public  String convertMinutesToDate(long minutes) {
        Calendar calendar = Calendar.getInstance();

        // Calculate the time in milliseconds based on the minutes
        long milliseconds = minutes * 60 * 1000;

        // Set the calendar time based on the milliseconds
        calendar.setTimeInMillis(System.currentTimeMillis() + milliseconds);

        // Get the date as a Date object
        Date date = calendar.getTime();

        // Get the current date
        Date currentDate = new Date();

        // Check if the date is the same as today
        if (isSameDay(date, currentDate)) {
            return "Today";
        } else {
            // Format the date as desired (e.g., "yyyy-MM-dd HH:mm:ss")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(date);
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

}
