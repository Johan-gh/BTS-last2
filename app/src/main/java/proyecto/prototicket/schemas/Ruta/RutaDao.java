package proyecto.prototicket.schemas.Ruta;

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
public interface RutaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearRuta(Ruta ruta);

    @Query("SELECT  id, origen , destino, precio_N,precio_E,estado FROM tabla_ruta")
    public LiveData<List<Ruta>> verRutaOrigenDestino();

    @Query("SELECT origen,destino FROM tabla_ruta WHERE origen = :origen and destino = :destino")
    public List<Ruta> verificarRuta(String origen,String destino);


    @Query("SELECT precio_E FROM tabla_ruta WHERE origen = :origen AND destino=:destino AND empresaId = :empresaId")
    public String obtenerPrecioE(String origen, String destino, String empresaId);

    @Query("SELECT precio_N FROM tabla_ruta WHERE origen = :origen AND destino=:destino AND empresaId = :empresaId")
    public String obtenerPrecioN(String origen, String destino, String empresaId);

    @Query("SELECT origen, destino FROM tabla_ruta WHERE id = :id")
    public List<Ruta> obtenerRutaPorId(String id);
}
//(origen + '/' + destino) as ruta