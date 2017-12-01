package proyecto.prototicket.schemas.Itinerario;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by edison on 29/11/17.
 */

@Entity(tableName = "tabla_itinerario")
public class Itinerario {
    @PrimaryKey
    private String id;
    private String empresa;
    private String fecha_salida;
    private String hora_salida;
    private String origen;
    private String destino;
    private String numero_bus;

    public Itinerario(String id, String empresa, String fecha_salida, String hora_salida, String origen, String destino, String numero_bus) {
        this.id = id;
        this.empresa = empresa;
        this.fecha_salida = fecha_salida;
        this.hora_salida = hora_salida;
        this.origen = origen;
        this.destino = destino;
        this.numero_bus = numero_bus;
    }

    public String getNumero_bus() {return numero_bus;}

    public void setNumero_bus(String numero_bus) {this.numero_bus = numero_bus;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(String fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public String getHora_salida() {
        return hora_salida;
    }

    public void setHora_salida(String hora_salida) {
        this.hora_salida = hora_salida;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}
