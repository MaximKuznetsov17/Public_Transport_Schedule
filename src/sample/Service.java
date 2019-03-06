package sample;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service {
    public DAO dao;

    public Service() throws SQLException {
        dao = new DAO();
    }

    public List<ScheduleByTransport> getScheduleByTransportAndStop(String stopName, String route, Boolean flag, String type, String direction) throws SQLException {
        List<ScheduleByTransport> schedule = dao.findScheduleByTransportAndStop(stopName, route, type, direction);
        if (!flag) {
            return schedule;
        }
        List<ScheduleByTransport> removed = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            if (!checkTime(schedule.get(i).getArrivalTime())) {
                removed.add(schedule.get(i));
            }
        }
        schedule.removeAll(removed);
        return schedule;
    }

    public List<ScheduleByStop> getScheduleByStop(String stop, String type, boolean flag, String direction) throws SQLException {
        List<ScheduleByStop> schedule = dao.findScheduleByStop(stop, type, direction);
        if (!flag) {
            return schedule;
        }
        List<ScheduleByStop> removed = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            if (!checkTime(schedule.get(i).getArrivalTime())) {
                removed.add(schedule.get(i));
            }
        }
        schedule.removeAll(removed);
        return schedule;
    }

    public List<ScheduleByTransport> getScheduleByTransport(String stopName, String type, boolean flag) throws SQLException {
        List<ScheduleByTransport> schedule = dao.findScheduleByTransport(stopName, type);
        if (!flag) {
            return schedule;
        }
        List<ScheduleByTransport> removed = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            if (!checkTime(schedule.get(i).getArrivalTime())) {
                removed.add(schedule.get(i));
            }
        }
        schedule.removeAll(removed);
        return schedule;
    }

    public boolean checkTime(String time) {
        Date date = new Date();
        Pattern pt = Pattern.compile("(\\d+):(\\d+):(\\d+)");
        Matcher m = pt.matcher(time);
        if(m.find()) {
            int hours = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            int sec = Integer.parseInt(m.group(3));
            if (hours > date.getHours()) {
                return true;
            } else if (min > date.getMinutes() && hours == date.getHours()){
                return true;
            } else if (sec > date.getSeconds() && min == date.getMinutes()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
