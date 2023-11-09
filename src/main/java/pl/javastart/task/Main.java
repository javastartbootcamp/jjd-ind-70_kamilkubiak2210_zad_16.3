package pl.javastart.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.run(new Scanner(System.in));
    }

    private void run(Scanner userInput) {
        List<String> allDateFormats = getAllDateFormats();
        LocalDateTime dateTime = getLocalDateTime(userInput, allDateFormats);
        if (dateTime != null) {
            printInfoAboutZonedDateTime(dateTime);
        } else {
            System.out.println("Nieobsługiwany format");
        }
    }

    private static void printInfoAboutZonedDateTime(LocalDateTime dateTime) {
        ZonedDateTime localZone = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZone = localZone.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime londonZone = localZone.withZoneSameInstant(ZoneId.of("Europe/London"));
        ZonedDateTime losAngelesZone = localZone.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime sydneyZone = localZone.withZoneSameInstant(ZoneId.of("Australia/Sydney"));

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Czas lokalny: " + localZone.format(outputFormatter));
        System.out.println("UTC: " + utcZone.format(outputFormatter));
        System.out.println("Londyn: " + londonZone.format(outputFormatter));
        System.out.println("Los Angeles: " + losAngelesZone.format(outputFormatter));
        System.out.println("Sydney: " + sydneyZone.format(outputFormatter));
    }

    private static LocalDateTime getLocalDateTime(Scanner scanner, List<String> allDateFormats) {
        System.out.println("Podaj datę(yyyy-MM-dd), lub podaj modyfikacje do aktualnego czasu (t) +/- (yMdhms) np: t+2h:");
        String userInput = scanner.nextLine();
        LocalDateTime dateTime = null;
        Matcher matcher = Pattern.compile("^t([+-]\\d+[yMdhms])*").matcher(userInput);
        if (matcher.matches()) {
            return parseDate(userInput, LocalDateTime.now());
        }
        if (userInput.length() == 10) {
            LocalDate date = LocalDate.parse(userInput, DateTimeFormatter.ISO_LOCAL_DATE);
            dateTime = date.atStartOfDay();
        } else if (userInput.length() == 19) {
            for (String dateTimeFormat : allDateFormats) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
                    dateTime = LocalDateTime.parse(userInput, formatter);
                    return dateTime;
                } catch (DateTimeParseException ignored) {
                    //ignored
                }
            }
        }
        return dateTime;
    }

    private static List<String> getAllDateFormats() {
        List<String> formats = new LinkedList<>();
        formats.add("yyyy-MM-dd HH:mm:ss");
        formats.add("dd.MM.yyyy HH:mm:ss");
        return formats;
    }

    private static LocalDateTime parseDate(String inputTime, LocalDateTime currentTime) {
        LocalDateTime resultTime = currentTime;
        Matcher componentMatcher = Pattern.compile("[+-]?\\d+[yMdhms]").matcher(inputTime);
        while (componentMatcher.find()) {
            String component = componentMatcher.group();
            char unit = component.charAt(component.length() - 1);
            int value = Integer.parseInt(component.substring(0, component.length() - 1));

            switch (unit) {
                case 'y' -> resultTime = resultTime.plusYears(value);
                case 'M' -> resultTime = resultTime.plusMonths(value);
                case 'd' -> resultTime = resultTime.plusDays(value);
                case 'h' -> resultTime = resultTime.plusHours(value);
                case 'm' -> resultTime = resultTime.plusMinutes(value);
                case 's' -> resultTime = resultTime.plusSeconds(value);
                default -> {
                    return null;
                }
            }
        }
        return resultTime;
    }
}