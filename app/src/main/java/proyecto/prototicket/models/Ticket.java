package proyecto.prototicket.models;

import java.util.ArrayList;

public class Ticket {


    private String buyDate;
    private String travelDate;
    private String travelRoute;
    private String destiny;
    private String vehicle;
    private String name;
    private String schedule;
    private String rh;
    private String travelHour;

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    private String precio;

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getTravelRoute() {
        return travelRoute;
    }

    public void setTravelRoute(String travelRoute) {
        this.travelRoute = travelRoute;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRh() {
        return rh;
    }

    public void setRh(String rh) {
        this.rh = rh;
    }

    public String getTravelHour() {
        return travelHour;
    }

    public void setTravelHour(String travelHour) {
        this.travelHour = travelHour;
    }


    public ArrayList<String> getDataPrint(){
        ArrayList<String> dataPrint = new ArrayList<String>();
        dataPrint.add(("^XA^CFA,15^FO20,20^FDFecha Compra: " + this.getBuyDate() + "^FS"));
        return dataPrint;
    }


}
