package proyecto.prototicket.schemas;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.Bus.BusDao;

/**
 * Created by User on 11/20/2017.
 */

@Database(entities = {Bus.class}, version = 1)
public abstract class TicketDatabase extends RoomDatabase{
    public abstract BusDao busDao();


}
