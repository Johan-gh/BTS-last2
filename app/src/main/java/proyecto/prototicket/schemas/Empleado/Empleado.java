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
    private String cedula;
    private String nombreEmpresa;
    private String token;

    public Empleado(String usuario, String clave, String cedula, String nombreEmpresa, String token) {
        this.usuario = usuario;
        this.clave = clave;
        this.cedula = cedula;
        this.nombreEmpresa = nombreEmpresa;
        this.token = token;
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
