package proyecto.prototicket.schemas.Bus;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by User on 11/19/2017.
 */

@Entity(tableName = "tabla_bus")
public class Bus {

    @PrimaryKey
    private String placa;

    private String capacidad;

    private String tipo_servicio;

    public Bus(String placa, String capacidad, String tipo_servicio) {
        this.placa = placa;
        this.capacidad = capacidad;
        this.tipo_servicio = tipo_servicio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipo_servicio() {
        return tipo_servicio;
    }

    public void setTipo_servicio(String tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }


}
