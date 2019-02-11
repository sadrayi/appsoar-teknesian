package ir.appsoar.teknesian.Helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class HelperShamsi
{

    private static String[] weekDays = {"شنبه", "یکشنبه", "دوشنبه", "سه شنبه",
            "چهارشنبه", "پنجشنبه", "جمعه"};
    private static String[] monthNames = {"فروردین", "اردیبهشت", "خرداد",
            "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن",
            "اسفند"};
    private static int[] shamsiMonthDays = new int[]{31, 31, 31, 31, 31, 31,
            30, 30, 30, 30, 30, 29};

    // Gregorian Base: 1970/1/1
    // Sahmsi Base: 1348/10/11
    // 3

    public static long shamsi() {
        return gregorianToShamsi(Calendar.getInstance());
    }

    public static Date convertToLocaleTimeZone(String date, String sourceTimeZone) {
        SimpleDateFormat simpleDateFormatInSourceTimeZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormatInSourceTimeZone.setTimeZone(TimeZone.getTimeZone(sourceTimeZone));

        SimpleDateFormat simpleDateFormatLocalTimeZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormatLocalTimeZone.setTimeZone(TimeZone.getDefault());

        Date inptdate = null;
        Date result = null;
        try {
            inptdate = simpleDateFormatInSourceTimeZone.parse(date);
            result = simpleDateFormatLocalTimeZone.parse(simpleDateFormatLocalTimeZone.format(inptdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (result);
    }

    public static long gregorianToShamsi(Calendar gregorianDate) {

        long dayMiliSecond = 86400000;
        int days;
        int year = 1348;

        Calendar gregorianBase = Calendar.getInstance();
        gregorianBase.set(1970, 0, 1, gregorianDate.get(Calendar.HOUR_OF_DAY), gregorianDate.get(Calendar.MINUTE), gregorianDate.get(Calendar.SECOND));

        try {
            days = (int) Math.round(((float) gregorianDate.getTimeInMillis() - (float) gregorianBase.getTimeInMillis()) / (float) dayMiliSecond);
            return addDays(48, 10, 11, days);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String gregorianToShamsiEdit(String gregorianStringDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date gregorianDate = simpleDateFormat.parse(gregorianStringDate);
            Calendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(gregorianDate);
            long shamsiDate =  gregorianToShamsi(gregorianCalendar);
            DateFormat df = new SimpleDateFormat("hh:mm");
            String hour = df.format(gregorianDate);
            String test = hour + " " +  "-" + " " + year(shamsiDate) + "/" + month(shamsiDate) + "/"  +
                    ((day(shamsiDate) < 10) ? "0"  + day(shamsiDate) : day(shamsiDate));

            return test;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String gregorianToShamsiDate(String gregorianStringDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date gregorianDate = simpleDateFormat.parse(gregorianStringDate);
            Calendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(gregorianDate);
            long shamsiDate =  gregorianToShamsi(gregorianCalendar);
            DateFormat df = new SimpleDateFormat("hh:mm");
            String hour = df.format(gregorianDate);
            String test =  year(shamsiDate) + "/" + month(shamsiDate) + "/"  +
                    ((day(shamsiDate) < 10) ? "0"  + day(shamsiDate) : day(shamsiDate));

            return test;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static long gregorianToShamsi(String gregorianStringDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date gregorianDate = simpleDateFormat.parse(gregorianStringDate);

            Calendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(gregorianDate);

            return gregorianToShamsi(gregorianCalendar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long gregorianToShamsi(Date gregorianDate) {
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(gregorianDate);

        return (gregorianToShamsi(gregorianCalendar));
    }

    public static Calendar shamsiToGregorian(long shamsiDate) {
        Double shamsiDiff = diff(481011, shamsiDate);

        Calendar gregorianBaseCalendar = Calendar.getInstance();
        gregorianBaseCalendar.set(1970, 0, 1);
        gregorianBaseCalendar.add(gregorianBaseCalendar.DATE,
                shamsiDiff.intValue());
        return gregorianBaseCalendar;
    }

    public static String dayNameOfWeek(long shamsiDate) {

        return weekDays[dayOfWeek(shamsiDate)];
    }
    public static int dayOfWeek(long shamsiDate) {
        int day;
        long shamsiBase;
        Double dif;
        shamsiBase = 481011;
        dif = diff(shamsiBase, shamsiDate);
        if (shamsiBase > shamsiDate) {
            dif = -dif;
        }
        day = (int) ((dif + 5) % 7);
        return day;
    }

    public static boolean isValid(long shamsiDate) {
        int month, year, day;
        boolean ValidDate = true;
        year = (int) year(shamsiDate);
        month = (int) month(shamsiDate);
        day = (int) day(shamsiDate);

        if (shamsiDate < 100101) {
            return true;
        }

        if (month > 12 || month == 0 || day == 0) {
            return false;
        }

        if (day > monthDays(year, month)) {
            return false;
        }
        return true;
    }

    public static String getDate(long year , long month,long day){
        String monthPrefix="",dayPrefix="";
        if(month<10) monthPrefix="0";
        if(day<10) dayPrefix="0";

        String result=year+monthPrefix+month+dayPrefix+day;
        return result;
    }

    public static int monthDays(int year, int month) {
        if (month <= 6) {
            return 31;
        } else {
            if (month < 12) {
                return 30;
            } else {
                if (year % 4 == 3) {
                    return 30;
                } else {
                    return 29;
                }
            }

        }
    }

    public static String monthName(long shamsiDate) {
        return monthNames[(int) (month(shamsiDate) - 1)];
    }

    public static long addDays(int year, int month, int day, int addDays) {
        String daySuffix = "";
        String monthSuffix = "";
        boolean isLeapYear = false;

        if (year % 4 == 3) {
            isLeapYear = true;
        }

        while (addDays > 0) {
            if (isLeapYear && month == 12 && day == 29) {
                day = 30;
            } else {
                if ((day + 1) > shamsiMonthDays[month - 1]) {
                    day = 1;
                    if (month >= 12) {
                        month = 1;
                        year++;
                        isLeapYear = false;
                        if (year % 4 == 3) {
                            isLeapYear = true;
                        }
                    } else {
                        month++;
                    }
                } else {
                    day++;
                }
            }
            addDays--;
        }

        if (day < 10) {
            daySuffix = "0";
        }
        if (month < 10) {
            monthSuffix = "0";
        }
        return Long.parseLong(year + monthSuffix + month + daySuffix + day);
    }

    public static Double diff(long fromDate, long toDate) {
        long temp;
        long year1, month1, day1, year2, month2, day2;
        Double Sumation;
        Boolean flag = false;

        if (fromDate == 0 || toDate == 0) {
            return 0.0;
        }

        if (fromDate > toDate) {
            flag = true;
            temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
        day1 = day(fromDate);
        month1 = month(fromDate);
        year1 = year(fromDate);
        day2 = day(toDate);
        month2 = month(toDate);
        year2 = year(toDate);
        Sumation = 0.0;

        while (year1 < (year2 - 1)
                || (year1 == (year2 - 1) && (month1 < month2 || (month1 == month2 && day1 <= day2)))) {
            // اگر یک سال یا بیشتر اختلاف بود
            if (year1 % 4 == 3) {
                if (month1 == 12 && day1 == 30) {
                    Sumation = Sumation + 365;
                    day1 = 29;
                } else {
                    Sumation = Sumation + 366;
                }
            } else {
                Sumation = Sumation + 365;
            }
            year1 = year1 + 1;
        }

        while (year1 < year2 || month1 < (month2 - 1) || month1 == (month2 - 1)
                && day1 < day2) {
            // اگر یک ماه یا بیشتر اختلاف بود
            if (month1 <= 6) {
                if (month1 == 6 && day1 == 31) {
                    Sumation = Sumation + 30;
                    day1 = 30;
                } else {
                    Sumation = Sumation + 31;
                }

                month1 = month1 + 1;

            } else {
                if (month1 < 12) {
                    if (month1 == 11 && day1 == 30 && year1 % 4 != 3) {
                        Sumation = Sumation + 29;
                        day1 = 29;
                    } else {
                        Sumation = Sumation + 30;
                    }
                    month1 = month1 + 1;
                } else {
                    if (year1 % 4 == 3) {
                        Sumation = Sumation + 30;
                    } else {
                        Sumation = Sumation + 29;
                    }
                    year1 = year1 + 1;
                    month1 = 1;
                }
            }
        }

        // اگر ادو تاریخ در یک سال بود
        if (month1 == month2) {
            Sumation = Sumation + (day2 - day1);
        } else {
            if (month1 <= 6) {
                Sumation = Sumation + (31 - day1) + day2;
            } else {
                if (month1 < 12) {
                    Sumation = Sumation + (30 - day1) + day2;
                } else {
                    if (year1 % 4 == 3) {
                        Sumation = Sumation + (30 - day1) + day2;
                    } else {
                        Sumation = Sumation + (29 - day1) + day2;
                    }
                }
            }
        }

        if (flag) {
            Sumation = -Sumation;
        }
        return Sumation;
    }

    public static long day(long shamsiDate) {
        return shamsiDate % 100;
    }

    public static long month(long shamsiDate) {
        return Math.round((shamsiDate % 10000) / 100);
    }

    public static long year(long shamsiDate) {
        return Math.round(shamsiDate / 10000);
    }

    public static String shamsiFull(long shamsi) {
        return dayNameOfWeek(shamsi) + " " + day(shamsi) + " " + monthName(shamsi) + " " + year(shamsi);
    }

    public static String shamsiFull2() {
        long shamsi = shamsi();
        return "امروز "  + day(shamsi) + " " + monthName(shamsi) + " 13" + year(shamsi);
    }

    public static String shamsiWithFormat(long shamsiDate) {
        return ((day(shamsiDate) < 10) ? "0" + day(shamsiDate) : day(shamsiDate)) + " / " + month(shamsiDate) + " / " + year(shamsiDate) ;
    }
}
