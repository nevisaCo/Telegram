package com.finalsoft.helper;

import android.util.Log;

import java.util.Calendar;

public class PersianCalender {
    private Calendar calendar;
    private int day;
    private int month;
    private int weekDay;
    private int year;

    private String hour = "";
    private String minute = "";
    private String second = "";
    private String a = "";

    public PersianCalender() {
        this.calendar = Calendar.getInstance();
        calSolarCalendar();
    }

    public PersianCalender(Calendar calendar) {
        this.calendar = calendar;
        calSolarCalendar();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        calSolarCalendar();
    }

    private void calSolarCalendar() {
        int georgianYear = calendar.get(Calendar.YEAR);
        int georgianMonth = calendar.get(Calendar.MONTH) + 1;
        int georgianDate = calendar.get(Calendar.DATE);
        weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int[] buf1 = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int[] buf2 = new int[]{0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
        int ld;
        if (georgianYear % 4 != 0) {
            day = buf1[georgianMonth - 1] + georgianDate;
            if (day > 79) {
                day -= 79;
                if (day <= 186) {
                    switch (day % 31) {
                        case 0:
                            month = day / 31;
                            day = 31;
                            break;
                        default:
                            month = (day / 31) + 1;
                            day %= 31;
                            break;
                    }
                    year = georgianYear - 621;
                    return;
                }
                day -= 186;
                switch (day % 30) {
                    case 0:
                        month = (day / 30) + 6;
                        day = 30;
                        break;
                    default:
                        month = (day / 30) + 7;
                        day %= 30;
                        break;
                }
                year = georgianYear - 621;
                return;
            }
            if (georgianYear <= 1996 || georgianYear % 4 != 1) {
                ld = 10;
            } else {
                ld = 11;
            }
            day += ld;
            switch (day % 30) {
                case 0:
                    month = (day / 30) + 9;
                    day = 30;
                    break;
                default:
                    month = (day / 30) + 10;
                    day %= 30;
                    break;
            }
            year = georgianYear - 622;
            return;
        }
        day = buf2[georgianMonth - 1] + georgianDate;
        if (georgianYear >= 1996) {
            ld = 79;
        } else {
            ld = 80;
        }
        if (day > ld) {
            day -= ld;
            if (day <= 186) {
                switch (day % 31) {
                    case 0:
                        month = day / 31;
                        day = 31;
                        break;
                    default:
                        month = (day / 31) + 1;
                        day %= 31;
                        break;
                }
                year = georgianYear - 621;
                return;
            }
            day -= 186;
            switch (day % 30) {
                case 0:
                    month = (day / 30) + 6;
                    day = 30;
                    break;
                default:
                    month = (day / 30) + 7;
                    day %= 30;
                    break;
            }
            year = georgianYear - 621;
            return;
        }
        day += 10;
        switch (day % 30) {
            case 0:
                month = (day / 30) + 9;
                day = 30;
                break;
            default:
                month = (day / 30) + 10;
                day %= 30;
                break;
        }
        year = georgianYear - 622;
    }

    public String getWeekDay() {
        //String strWeekDay = BuildConfig.FLAVOR;
        String[] s = {"یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"};
        return s[weekDay];
    }

    public String getMonthName() {
        String[] s = {
                "فروردین", "اردیبهشت", "خرداد",
                "تیر", "مرداد", "شهریور",
                "مهر", "آبان", "آذر",
                "دی", "بهمن", "اسفند"};
        return s[month - 1];

    }

    public String getShortMonthName() {
        String[] s = {
                "فرو", "ارد", "خرد",
                "تیر", "مرد", "شهر",
                "مهر", "آبا", "آذر",
                "دی", "بهم", "اسف"};
        return s[month - 1];

    }

    public String getMonth() {
        return String.valueOf(month);
    }

    public String getYear() {
        return String.valueOf(year);
    }

    public String getDesDate() {
        String describedDateFormat =
                String.valueOf(day) + " " + getMonthName() + " " + String.valueOf(year) + " "
                        + "ساعت" + " " + getTime();
        return String.valueOf(
                describedDateFormat);
    }

    public String getShortDesDateTime() {
        String describedDateFormat = String.valueOf(day) + " " + getMonthName() + " "
                + "ساعت" + " " + getTime();
        return String.valueOf(
                describedDateFormat);
    }

    public String getShortDesDate() {
        return String.valueOf(String.valueOf(day) + " " + getMonthName() + " ");
    }

    public String getNumDateTime() {
        return String.valueOf(String.valueOf(year)
                + "/"
                + String.valueOf(month)
                + "/"
                + String.valueOf(day)
                + " "
                + getTime());
    }

    public String getNumDate() {
        return String.format("%s/%s/%s ", year, month, day);
    }

    public String getDate(Calendar calendar, String pattern) {
        if (this.calendar != calendar) {
            this.calendar = calendar;
            calSolarCalendar();
        }
        if (pattern.equals("MMMM d")) {
            pattern = pattern.replace("MMMM d", "d MMMM");
        }
        if (pattern.equals("MMM dd")) {
            pattern = pattern.replace("MMM dd", "dd MMM");
        }
        if (pattern.equals("MMM d")) {
            pattern = pattern.replace("MMM d", "d MMM");
        }
        if (pattern.contains("HH")) {
            getTime(false);
        } else if (pattern.contains("h")) {
            getTime(true);
        }
        Log.i("finalsoftpc", "getDate: pattern:" + pattern);
        return pattern
                .replace("yyyy", String.valueOf(year))
                .replace("yyy", getYear())
                .replace("MMMM", getMonthName())
                .replace("MMM", getMonthName())
                .replace("MM", getMonth())
                .replace("dd", String.valueOf(day))
                .replace("EEE", getWeekDay())
                .replace("d", String.valueOf(day))
                .replace("HH", hour)
                .replace("hh", hour)
                .replace("h", hour)
                .replace("mm", minute)
                .replace("m", minute)
                .replace("ss", second)
                .replace("a", a)
                ;
    }

    public String getTime() {
        return getTime(false);
    }

    public String getTime(boolean is24) {
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND) + "";
        StringBuilder time = new StringBuilder();

        if (!is24) {
            int i = h < 12 ? h : h == 12 ? 12 : h - 12;
            time.append(i).append(":").append(m < 10 ? "0" + m : Integer.valueOf(m)).append(h < 12 ? " " + "قظ" : " " + "بظ");

        } else {
            time.append(h).append(":").append(m);

        }
        hour = ("" + h).length() == 1 ? "0" + h : "" + h;
        minute = ("" + m).length() == 1 ? "0" + m : "" + m;
        a = h < 12 ? "ق" : "ب";
        return String.valueOf(time);
    }

    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    public String toString() {
        return getDesDate();
    }

    public static void main(String[] args) {
    }
}
