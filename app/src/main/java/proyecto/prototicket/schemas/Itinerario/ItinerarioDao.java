package proyecto.prototicket.schemas.Itinerario;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by edison on 29/11/17.
 */

@Dao
public interface ItinerarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearItinerario(Itinerario itinerario);


    @Query("SELECT id, origen, empresa, destino FROM tabla_itinerario")
    public LiveData<List<Itinerario>> verItinerarioOrigen();

    @Query("SELECT id, empresa, fecha_salida,hora_salida, origen, destino FROM tabla_itinerario WHERE empresa=:empresa AND origen=:origen AND destino=:destino")
    public LiveData<List<Itinerario>> verItinerario(String empresa, String origen, String destino);

}
