package proyecto.prototicket.schemas.Empresa;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by edison on 29/11/17.
 */

@Entity(tableName = "tabla_empresa")
public class Empresa {

    @PrimaryKey
    private String id;
    private String nombre;
    private String nit;


    public Empresa(String id, String nombre, String nit) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }
}
