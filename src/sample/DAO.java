package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DAO {

    private Connection connection;

    public DAO() throws SQLException {
        Connection connection = getConnection();
        this.connection = connection;
    }

    public static Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "admin");
        connectionProps.put("password", "1708");
        return DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;database=Schedule public transport", connectionProps);
    }

    public List<String> findTime (int stop, String transport) throws SQLException {
        List<String> time = new ArrayList<>();
        Statement statement = null;
        String query = "select Arrival from ArrivalTime where TransportID = '" + transport + "' and StopID = " + stop;
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String arrive = set.getString("Arrival");
                time.add(arrive);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return time;
    }

    public String findTransportID(String transport) throws SQLException {
        Statement statement = null;
        String query = "select TransportID, RouteNumber from Transports";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String route = set.getString("RouteNumber");
                if (route.equals(transport)) {
                    return set.getString("TransportID");
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return null;
    }

    public int findStopID(String stop) throws SQLException {
        Statement statement = null;
        String query = "select StopID, [NAME] from Stops";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String name = set.getString("NAME");
                if (name.equals(stop)) {
                    return set.getInt("StopID");
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return 0;
    }

    public List<ScheduleByTransport> findScheduleByTransport(String route, String type) throws SQLException {
        List<ScheduleByTransport> schedule = new ArrayList<>();
        Statement statement = null;
        String query = "select Transports.RouteNumber, ArrivalTime.Arrival, Transports.TransportID, ArrivalTime.StopID" +
                " from (Transports inner join ArrivalTime" +
                " on ArrivalTime.TransportID = (select Transports.TransportID where Transports.RouteNumber = '" + route + "'" +
                " and Transports.TypeOfTransport = '" + type + "'))";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String arrival = set.getString("Arrival");
                String transportID = set.getString("TransportID");
                int stopID = set.getInt("StopID");
                String direction = findDirection(stopID);
                String stopName = getStopName(stopID);
                schedule.add(new ScheduleByTransport(transportID, stopName, arrival, direction));
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return schedule;
    }

    private String findDirection(int stopID) throws SQLException {
        Statement statement = null;
        String query = "select StopID, DirectionOfTravel from Stops where StopID = " + stopID;
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String direction= set.getString("DirectionOfTravel");
                return direction;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return null;
    }

    private String getStopName(int stopID) throws SQLException {
        Statement statement = null;
        String query = "select StopID, [NAME] from Stops where StopID = " + stopID;
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String name = set.getString("NAME");
                return name;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return null;
    }

    public List<ScheduleByStop> findScheduleByStop(String stop, String type, String direction) throws SQLException {
        List<ScheduleByStop> schedule = new ArrayList<>();
        Statement statement = null;
        String query = "select ArrivalTime.Arrival, Stops.StopID, ArrivalTime.TransportID, Stops.DirectionOfTravel" +
                        " from (Stops inner join ArrivalTime" +
                        " on ArrivalTime.StopID = (select Stops.StopID where Stops.NAME = '" + stop + "'" +
                        " and Stops.TypeOfStop = '" + type + "'))";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String arrival = set.getString("Arrival");
                String transportID = set.getString("TransportID");
                String directionOfTravel = set.getString("DirectionOfTravel");
                int stopID = set.getInt("StopID");
                String routeNumber = findRoute(transportID);
                if (direction.equals(directionOfTravel) && !direction.equals("")) {
                    schedule.add(new ScheduleByStop(transportID, routeNumber, arrival, directionOfTravel));
                } else if (direction.equals("")){
                    schedule.add(new ScheduleByStop(transportID, routeNumber, arrival, directionOfTravel));
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return schedule;
    }

    private String findRoute(String transportID) throws SQLException {
        Statement statement = null;
        String query = "select routeNumber from Transports where TransportID = '" + transportID + "'";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String routeNumber = set.getString("routeNumber");
                return routeNumber;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) { statement.close(); }
        }
        return null;
    }

    public List<ScheduleByTransport> findScheduleByTransportAndStop(String stopName, String route, String type, String direction) throws SQLException {
        List<ScheduleByTransport> schedule = new ArrayList<>();
        Statement statement = null;
        String query = "select Transports.RouteNumber, ArrivalTime.Arrival, Transports.TransportID, ArrivalTime.StopID, Stops.DirectionOfTravel" +
                " from (ArrivalTime inner join Transports" +
                " on ArrivalTime.TransportID = (select Transports.TransportID where" +
                " Transports.RouteNumber = '" + route +
                "' and Transports.TypeOfTransport = '" + type + "'))" +
                " inner join Stops on ArrivalTime.StopID = (select Stops.StopID where Stops.[NAME] = '" + stopName + "')";
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String arrival = set.getString("Arrival");
                String transportID = set.getString("TransportID");
                String directionOfTravel = set.getString("DirectionOfTravel");
                if (direction.equals(directionOfTravel) && !direction.equals("")) {
                    schedule.add(new ScheduleByTransport(transportID, stopName, arrival, directionOfTravel));
                } else if (direction.equals("")){
                    schedule.add(new ScheduleByTransport(transportID, stopName, arrival, directionOfTravel));
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return schedule;
    }
}
