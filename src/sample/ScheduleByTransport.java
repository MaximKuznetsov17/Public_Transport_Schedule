package sample;

public class ScheduleByTransport {
    private String transportID;
    private String stop;
    private String arrivalTime;
    private String direction;

    public ScheduleByTransport(String transportID, String stop, String arrivalTime, String direction) {
        this.transportID = transportID;
        this.stop = stop;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public String getTransportID() {
        return transportID;
    }

    public String getStop() {
        return stop;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

}
