package proyecto.prototicket.schemas.Empleado;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by edison on 23/11/17.
 */

@Dao
public interface EmpleadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearEmpleado(Empleado empleado);

    @Query("SELECT * FROM tabla_empleado WHERE usuario= :usuario AND clave= :clave")
    public LiveData<List<Empleado>> verificarUsuario(String usuario, String clave);

}
