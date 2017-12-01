package proyecto.prototicket.schemas.Bus;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

/**
 * Created by User on 11/19/2017.
 */
@Dao
public interface BusDao {
    @Query("SELECT placa from tabla_bus")
    public LiveData<List<String>> verPlacas();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearBus(Bus bus);

    @Query("SELECT placa FROM tabla_bus WHERE placa = :placa")
    public List<Bus> verificarPlaca(String placa);

    @Query("SELECT placa FROM tabla_bus WHERE numero_bus = :numeroBus")
    public LiveData<List<Bus>> verPlacaPorNumero(String numeroBus);
}
