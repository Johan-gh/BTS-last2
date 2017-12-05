package proyecto.prototicket.schemas;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import proyecto.prototicket.models.Ticket;
import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.Bus.BusDao;
import proyecto.prototicket.schemas.Empresa.Empresa;
import proyecto.prototicket.schemas.Empresa.EmpresaDao;
import proyecto.prototicket.schemas.Itinerario.Itinerario;
import proyecto.prototicket.schemas.Itinerario.ItinerarioDao;
import proyecto.prototicket.schemas.PuntoVenta.PuntoVenta;
import proyecto.prototicket.schemas.PuntoVenta.PuntoVentaDao;
import proyecto.prototicket.schemas.Ruta.Ruta;
import proyecto.prototicket.schemas.Ruta.RutaDao;
import proyecto.prototicket.schemas.Ticket.TicketDao;
import proyecto.prototicket.schemas.Ticket.TicketDb;

/**
 * Created by User on 11/20/2017.
 */

@Database(entities = {Bus.class,PuntoVenta.class,Ruta.class, TicketDb.class, Itinerario.class, Empresa.class}, version = 28,exportSchema = false)
public abstract class TicketDatabase extends RoomDatabase{
    public abstract BusDao busDao();
    public abstract PuntoVentaDao puntoVentaDao();
    public abstract RutaDao rutaDao();
    public abstract TicketDao ticketDao();
    public abstract ItinerarioDao ItinerarioDao();
    public abstract EmpresaDao empresaDao();

}
