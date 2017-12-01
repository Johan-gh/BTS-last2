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

    @Query("SELECT  id, origen , destino, precio_N,precio_E,habilitada FROM tabla_ruta")
    public LiveData<List<Ruta>> verRutaOrigenDestino();

    @Query("SELECT origen,destino FROM tabla_ruta WHERE origen = :origen and destino = :destino")
    public List<Ruta> verificarRuta(String origen,String destino);



}
//(origen + '/' + destino) as ruta