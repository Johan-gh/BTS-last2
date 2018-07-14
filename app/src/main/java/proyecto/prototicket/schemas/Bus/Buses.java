package proyecto.prototicket.schemas.Bus;

import java.util.List;

/**
 * Created by JOHAN on 31/01/2018.
 */

public class Buses {
    List<Bus> buses ;

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }

    public Buses(List<Bus> buses) {
        this.buses = buses;
    }

}
