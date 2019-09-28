import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeParser {
    public LocalDateTime getDateTime(UserIO userIO){
        LocalDateTime currentDate = LocalDateTime.now();
        Calendar userDate = new GregorianCalendar();
        userDate.set(Calendar.YEAR, getYear(userIO,currentDate));
        userDate.set(Calendar.MONTH, getMonth(userIO, currentDate));
        userDate.set(Calendar.DAY_OF_MONTH, getDay(userIO,getDaysInMonth(userDate)));
        userDate.setTime(getTime(userIO));

        return LocalDateTime.ofInstant(userDate.toInstant(),userDate.getTimeZone().toZoneId());
    }

    public LocalDateTime getDateTimeWithOffset(UserIO userIO, LocalDateTime date){
        LocalDateTime newDate;
        int respond = userIO.getOnClickButton(new String[]{
                "No remind",
                "A hour before",
                "A day before",
                "A week before",
                "Set date..."});
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
        userIO.showMessage("Choose year");
        String[] years = {
                Integer.toString(currentDate.getYear()),
                Integer.toString(currentDate.plusYears(1).getYear()),
                "Other"};
        int respond = userIO.getOnClickButton(years);
        int year;
        if (respond > years.length){
            DateFormat sdf = new SimpleDateFormat("yyyy");
            while (true){
                try {
                    year = Integer.parseInt(userIO.getUserText("Set year (yyyy)"));
                    if (year < currentDate.getYear() || year > 2035) {
                        throw new ParseException("Illegal year", 1);
                    }
                    break;
                } catch (ParseException e) {
                    userIO.showMessage("Wrong format");
                }
            }
        }
        else {
            year = Integer.parseInt(years[respond]);
        }
        return year;
    }

    private int getMonth(UserIO userIO, LocalDateTime currentDate){
        userIO.showMessage("Choose month");
        String[] months = new String[13];
        for (int i = 0; i < 12; i++){
            months[i] = currentDate.plusMonths(i).getMonth().toString();
        }
        int respond = userIO.getOnClickButton(months);
        return currentDate.plusMonths(respond).getMonthValue();
    }

    private int getDay(UserIO userIO, int daysInMonth){
        userIO.showMessage("choose day");
        String[] days = new String[daysInMonth];
        for (int i = 1; i<=daysInMonth; i++){
            days[i-1] = Integer.toString(i);
        }
        int respond = userIO.getOnClickButton(days);
        return respond+1;
    }

    private Date getTime(UserIO userIO){
        Date time;
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        while (true){
            try {
                time = sdf.parse(userIO.getUserText("Set time (hh:mm)"));
                break;
            } catch (ParseException e) {
                userIO.showMessage("Wrong format");
            }
        }
        return time;
    }

    private int getDaysInMonth(Calendar calendar){
        return YearMonth.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)).lengthOfMonth();
    }
}
