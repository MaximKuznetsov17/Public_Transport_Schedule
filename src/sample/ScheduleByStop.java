package sample;

public class ScheduleByStop {
    private String transportID;
    private String routeNumber;
    private String arrivalTime;
    private String direction;

    public ScheduleByStop(String transportID, String routeNumber, String arrivalTime, String direction) {
        this.transportID = transportID;
        this.routeNumber = routeNumber;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public String getTransportID() {
        return transportID;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "transportID = " + transportID +
                " routeNumber = " + routeNumber +
                " arrivalTime = " + arrivalTime + '\n';
    }
}
