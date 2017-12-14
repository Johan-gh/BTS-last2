package proyecto.prototicket.schemas.Empleado;

/**
 * Created by edison on 23/11/17.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tabla_empleado")
public class Empleado {

    @PrimaryKey
    private String usuario;
    private String clave;

    public Empleado(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
