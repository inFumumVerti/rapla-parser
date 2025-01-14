package dhbw.timetable.rapla.test;

import dhbw.timetable.rapla.date.DateUtilities;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.data.event.BackportAppointment;
import dhbw.timetable.rapla.data.time.TimelessDate;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import dhbw.timetable.rapla.parser.DataImporter;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class Main {

    private static final boolean debugData = false;

    private final static String[] test_urls = {
            "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=ahrensb&file=TEL20AT",

            "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4"
    };

    public static void main(String[] args)  {
        unit_test(LocalDate.of(2020, 1, 1), LocalDate.of(2023, 1, 1));
    }

    private static void unit_test(LocalDate start, LocalDate end) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        System.out.println("Starting test from " + start.format(dtf) + " to " + end.format(dtf) +  "...");
        boolean error = false;
        for(String url : test_urls) {
            System.out.print("Checking " + url + " ... ");
            try {
                Map<LocalDate, ArrayList<Appointment>> data = DataImporter.ImportWeekRange(start, end, url);
                Map<TimelessDate, ArrayList<BackportAppointment>> backportData = DataImporter.Backport.ImportWeekRange(
                        DateUtilities.ConvertToCalendar(start), DateUtilities.ConvertToCalendar(end), url);
                if (checkEquality(data, backportData)) {
                    System.out.println("SUCCESS!");
                } else {
                    error = true;
                    System.out.println("INVALID!");
                }
                if (debugData) {
                    print(data);
                    print(backportData);
                }
            } catch (NoConnectionException | MalformedURLException | IllegalAccessException e) {
                error = true;
                System.out.println("FAIL!");
                e.printStackTrace();
            }
        }

        // Check for trivial equality
        Appointment a = new Appointment(LocalDateTime.MIN, LocalDateTime.MAX, "Title", "Persons", "Resources");
        BackportAppointment ba = new BackportAppointment((GregorianCalendar)Calendar.getInstance(), (GregorianCalendar)Calendar.getInstance(),
                "title", "persons", "resources");
        if(!error && (!a.equals(a) || !ba.equals(ba))) {
            error = true;
            System.err.println("Data structures equals not working");
        }

        if (error) {
            System.out.println("Main finished with errors :(");
        } else {
            System.out.println("Main successfully finished! :)");
        }
    }

    private static boolean checkEquality(Map<LocalDate, ArrayList<Appointment>> data, Map<TimelessDate, ArrayList<BackportAppointment>> backportData) {
        if (data == null || backportData == null || data.size() != backportData.size()) {
            return false;
        }

        for (LocalDate dateKey : data.keySet()) {
            ArrayList<Appointment> list1 = data.get(dateKey);
            ArrayList<BackportAppointment> list2 = backportData.get(DateUtilities.ConvertToCalendar(dateKey));

            if (list1 == null || list2 == null || list1.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list1.size(); i++) {
                Appointment a1 = list1.get(i);
                BackportAppointment a2 = list2.get(i);

                if(!a1.getDate().equals(a2.getDate()) || !a1.getStartTime().equals(a2.getStartTime())
                        || !a1.getEndTime().equals(a2.getEndTime()) || !a1.getTitle().equals(a2.getTitle())
                        || !a1.getInfo().equals(a2.getInfo())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void print(Map data) {
        int size = 0;
        for (Object oWeek : data.values()) {
            ArrayList week = (ArrayList) oWeek;
            size += week.size();
            for (Object a : week) {
                System.out.println(a);
            }
        }
        System.out.println("Size: " + size);
    }

}
