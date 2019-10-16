import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateTimeParser {
    public LocalDateTime getDateTime(UserIO userIO){
        LocalDateTime currentDate = LocalDateTime.now();
        Calendar userDate = new GregorianCalendar();
        userDate.set(Calendar.YEAR, getYear(userIO,currentDate));
        userDate.set(Calendar.MONTH, getMonth(userIO, currentDate));
        userDate.set(Calendar.DAY_OF_MONTH, getDay(userIO,getDaysInMonth(userDate)));
        LocalTime time = getTime(userIO);
        userDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        userDate.set(Calendar.MINUTE, time.getMinute());

        return LocalDateTime.ofInstant(userDate.toInstant(),userDate.getTimeZone().toZoneId());
    }

    public LocalDateTime getDateTimeWithOffset(UserIO userIO, LocalDateTime date){
        LocalDateTime newDate;
        int respond = userIO.getOnClickButton(new String[]{
                "No remind",
                "A hour before",
                "A day before",
                "A week before",
                "Set date..."}, 0);
        switch (respond) {
            case 0:
                newDate = date;
                break;
            case 1:
                newDate = date.minusHours(1);
                break;
            case 2:
                newDate = date.minusDays(1);
                break;
            case 3:
                newDate = date.minusWeeks(1);
                break;
            case 4:
                newDate = getDateTime(userIO);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return newDate;
    }

    private int getYear(UserIO userIO, LocalDateTime currentDate){
        userIO.showMessage("Choose year", 0);
        String[] years = {
                Integer.toString(currentDate.getYear()),
                Integer.toString(currentDate.plusYears(1).getYear()),
                "Other"};
        int respond = userIO.getOnClickButton(years, 0);
        int year;
        if (respond > years.length){
            while (true){
                try {
                    year = Integer.parseInt(userIO.getUserText("Set year (yyyy)", 0));
                    if (year < currentDate.getYear() || year > 2035) {
                        throw new ParseException("Illegal year", 1);
                    }
                    break;
                } catch (ParseException e) {
                    userIO.showMessage("Wrong format", 0);
                }
            }
        }
        else {
            year = Integer.parseInt(years[respond]);
        }
        return year;
    }

    private int getMonth(UserIO userIO, LocalDateTime currentDate){
        userIO.showMessage("Choose month", 0);
        String[] months = new String[12];
        for (int i = 0; i < 12; i++){
            months[i] = currentDate.plusMonths(i).getMonth().toString();
        }
        int respond = userIO.getOnClickButton(months, 0);
        return currentDate.plusMonths(respond - 1).getMonthValue();
    }

    private int getDay(UserIO userIO, int daysInMonth){
        userIO.showMessage("choose day", 0);
        String[] days = new String[daysInMonth];
        for (int i = 1; i<=daysInMonth; i++){
            days[i-1] = Integer.toString(i);
        }
        int respond = userIO.getOnClickButton(days, 0);
        return respond+1;
    }

    private LocalTime getTime(UserIO userIO){
        LocalTime time;
        while (true){
            try {
                time = LocalTime.parse(userIO.getUserText("Set time (hh:mm)", 0));
                break;
            } catch (Exception e){
                userIO.showMessage("Wrong format", 0);
            }
        }
        return time;
    }

    private int getDaysInMonth(Calendar calendar){
        return YearMonth.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)).lengthOfMonth();
    }
}
