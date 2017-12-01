package proyecto.prototicket.schemas.Empleado;

/**
 * Created by edison on 23/11/17.
 */

import android.arch.persistence.room.Entity;

@Entity(tableName = "tabla_empleado")
public class Empleado {

    private String nombre;
    private String Usuario;
    private String cedula;
    private String clave;
    private String permiso;
}
