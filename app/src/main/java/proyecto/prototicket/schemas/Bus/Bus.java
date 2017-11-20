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

    private String tipo_de_servicio;

    private Integer capacidad;

    public Bus(String placa, String tipo_de_servicio, Integer capacidad){
        this.placa = placa;
        this.tipo_de_servicio = tipo_de_servicio;
        this.capacidad = capacidad;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo_de_servicio() {
        return tipo_de_servicio;
    }

    public void setTipo_de_servicio(String tipo_de_servicio) {
        this.tipo_de_servicio = tipo_de_servicio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
}
