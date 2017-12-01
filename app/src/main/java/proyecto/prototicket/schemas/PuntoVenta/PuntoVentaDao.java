package proyecto.prototicket.schemas.PuntoVenta;

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
public interface PuntoVentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearPuntoVenta(PuntoVenta puntoVenta);

    @Query("SELECT id, nombre FROM tabla_punto_venta")
    public LiveData<List<PuntoVenta>> verPuntoVentas();

    @Query("SELECT nombre FROM tabla_punto_venta WHERE nombre = :nombre")
    public List<PuntoVenta> verificarPuntoVenta(String nombre);
}
