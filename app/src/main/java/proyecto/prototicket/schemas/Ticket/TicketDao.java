package proyecto.prototicket.schemas.Ticket;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by edison on 23/11/17.
 */

@Dao
public interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearTicket(TicketDb ticket);

    @Query("SELECT  uuid,ruta,placaBus ,valor,fecha_nicial,punto_venta ,hora_llegada,fechaViaje,hora_salida,sincro,cierre,empresa,empleado, despachado FROM tabla_tiquete")
    public List<TicketDb> verTiquete();

    @Update
    void actualizarTiquete(TicketDb ticketDb);

    @Delete
    public void eliminarTiquete(TicketDb ticketDb);

    @Query("SELECT uuid,ruta, placaBus, valor,fecha_nicial,punto_venta ,hora_llegada,fechaViaje,hora_salida,sincro,cierre,empresa,empleado, despachado FROM tabla_tiquete WHERE empleado = :empleado AND cierre = :flag")
    public List<TicketDb> obtenerTicketPorEmpleado(String empleado, String flag);

    @Query("SELECT placaBus FROM tabla_tiquete WHERE despachado = :despachado")
    public LiveData<List<TicketDb>> obtenerPlacasBuses(String despachado);

    @Query("SELECT uuid,ruta, placaBus, valor,fecha_nicial,punto_venta ,hora_llegada,fechaViaje,hora_salida,sincro,cierre,empresa,empleado, despachado FROM tabla_tiquete WHERE placaBus =:placaBus")
    public List<TicketDb> obtenerTicketPorPlaca(String placaBus);
}
