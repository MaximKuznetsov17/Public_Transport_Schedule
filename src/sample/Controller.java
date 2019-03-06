package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private Button search;

    @FXML
    private TextField transport;

    @FXML
    private TextField stop;

    @FXML
    private TextField textField;

    @FXML
    private TableView<ScheduleByStop> tableViewStop;

    @FXML
    private TableView<ScheduleByTransport> tableViewTransport;

    @FXML
    private ScatterChart<String, Number> chart;

    @FXML
    private Axis<CategoryAxis> xAxis;

    @FXML
    private Axis<NumberAxis> yAxis;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private CheckBox checker;


    @FXML
    public void initialize() {
        TableColumn<ScheduleByStop, String> idColumn = new TableColumn<>("TransportID");
        idColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByStop, String>("transportID"));
        TableColumn<ScheduleByStop, String> routeColumn = new TableColumn<>("Route");
        routeColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByStop, String>("routeNumber"));
        TableColumn<ScheduleByStop, String> arrivalColumn = new TableColumn<>("Arrival Time");
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByStop, String>("arrivalTime"));
        TableColumn<ScheduleByStop, String> directionColumn = new TableColumn<>("Direction Of Travel");
        directionColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByStop, String>("direction"));
        tableViewStop.getColumns().add(idColumn);
        tableViewStop.getColumns().add(routeColumn);
        tableViewStop.getColumns().add(arrivalColumn);
        tableViewStop.getColumns().add(directionColumn);
        tableViewStop.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<ScheduleByTransport, String> transportIdColumn = new TableColumn<>("TransportID");
        transportIdColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByTransport, String>("transportID"));
        TableColumn<ScheduleByTransport, String> stopColumn = new TableColumn<>("Stop");
        stopColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByTransport, String>("stop"));
        TableColumn<ScheduleByTransport, String> arriveColumn = new TableColumn<>("Arrival Time");
        arriveColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByTransport, String>("arrivalTime"));
        TableColumn<ScheduleByTransport, String> directColumn = new TableColumn<>("Direction Of Travel");
        directColumn.setCellValueFactory(new PropertyValueFactory<ScheduleByTransport, String>("direction"));
        tableViewTransport.getColumns().add(transportIdColumn);
        tableViewTransport.getColumns().add(stopColumn);
        tableViewTransport.getColumns().add(arriveColumn);
        tableViewTransport.getColumns().add(directColumn);
        tableViewTransport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        List<String> transportType = new ArrayList<>();
        transportType.add("Метро");
        transportType.add("Трамвай");
        transportType.add("Троллейбус");
        transportType.add("Автобус");
        ObservableList<String> typeBox = FXCollections.observableArrayList();
        typeBox.addAll(transportType);
        choiceBox.setItems(typeBox);
        chart.getXAxis().setLabel("Stop");
        chart.getYAxis().setLabel("Time, min");
        chart.setTitle("Примерное время ожидания");
        chart.getData().clear();
        XYChart.Series series = new XYChart.Series();
        series.setName("Время до приезда данного автобуса на остановку");
        chart.getData().addAll(series);
    }

    @FXML
    public void onAction() throws SQLException {
        Service service = new Service();
        String route = transport.getText().trim();
        String stopName = stop.getText().trim();
        String direction = textField.getText().trim();
        String transportType = choiceBox.getValue();
        Boolean flag = checker.isSelected();
        if (route.equals("") && !stopName.equals("")) {
            List<ScheduleByStop> scheduleByStops = service.getScheduleByStop(stopName, transportType, flag, direction);
            fillTableByStop(scheduleByStops);
            fillChartsByStop(scheduleByStops);
        } else if (stopName.equals("") && !route.equals("")) {
            List<ScheduleByTransport> scheduleByTransports = service.getScheduleByTransport(route, transportType, flag);
            fillTableByTransport(scheduleByTransports);
            fillChartsByTransport(scheduleByTransports);
        } else {
            List<ScheduleByTransport> scheduleByTransportsAndStop = service.getScheduleByTransportAndStop(stopName, route, flag, transportType, direction);
            fillTableByTransport(scheduleByTransportsAndStop);
            fillChartsByTransport(scheduleByTransportsAndStop);
        }
    }

    private void fillTableByStop(List<ScheduleByStop> scheduleByStop) {
        tableViewStop.visibleProperty().setValue(true);
        tableViewTransport.visibleProperty().setValue(false);
        ObservableList<ScheduleByStop> schedule = FXCollections.observableArrayList();
        schedule.addAll(scheduleByStop);
        tableViewStop.setItems(schedule);
    }

    private void fillTableByTransport(List<ScheduleByTransport> scheduleByTransport) {
        tableViewStop.visibleProperty().setValue(false);
        tableViewTransport.visibleProperty().setValue(true);
        ObservableList<ScheduleByTransport> schedule = FXCollections.observableArrayList();
        schedule.addAll(scheduleByTransport);
        tableViewTransport.setItems(schedule);
    }

    private void fillChartsByStop(List<ScheduleByStop> schedule) {
        chart.getXAxis().setLabel("Route Number");
        chart.getYAxis().setLabel("Time, min");
        chart.setTitle("Примерное время ожидания");
        XYChart.Series series = new XYChart.Series();
        series.setName("Время до приезда автобуса на данную остановку");
        for (int i = 0; i < schedule.size(); i++) {
            float time = waitingTime(schedule.get(i).getArrivalTime());
            if (time >= 0) {
                series.getData().add(new XYChart.Data<String, Number>(schedule.get(i).getRouteNumber(),time));
            }
        }
        chart.getData().clear();
        chart.getData().addAll(series);
    }

    private void fillChartsByTransport(List<ScheduleByTransport> schedule) {
        chart.getXAxis().setLabel("Stop");
        chart.getYAxis().setLabel("Time, min");
        chart.setTitle("Примерное время ожидания");
        XYChart.Series series = new XYChart.Series();
        series.setName("Время до приезда данного автобуса на остановку");
        for (int i = 0; i < schedule.size(); i++) {
            float time = waitingTime(schedule.get(i).getArrivalTime());
            if (time >= 0) {
                series.getData().add(new XYChart.Data<String, Number>(schedule.get(i).getStop(),time));
            }
        }
        chart.getData().clear();
        chart.getData().addAll(series);
    }

    private float waitingTime(String time) {
        float waiting = -1;
        Date date = new Date();
        Pattern pt = Pattern.compile("(\\d+):(\\d+):(\\d+)");
        Matcher m = pt.matcher(time);
        if(m.find()) {
            int hours = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            int sec = Integer.parseInt(m.group(3));
            waiting = (hours - date.getHours()) * 60 + (min - date.getMinutes()) + (float) (sec - date.getSeconds()) / 60;
        }
        return waiting;
    }

}
