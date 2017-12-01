package proyecto.prototicket.schemas.PuntoVenta;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by edison on 23/11/17.
 */

@Entity(tableName = "tabla_punto_venta")
public class PuntoVenta {

    @PrimaryKey
    private String id;
    private String nombre;
    private String ciudad;

    public PuntoVenta(String id,String nombre, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
