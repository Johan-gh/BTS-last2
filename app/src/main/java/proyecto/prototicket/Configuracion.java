package proyecto.prototicket;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import proyecto.prototicket.schemas.Bus.BusRepository;
import proyecto.prototicket.schemas.Empresa.Empresa;
import proyecto.prototicket.schemas.Empresa.EmpresaRepository;
import proyecto.prototicket.schemas.Itinerario.ItinerarioRepository;
import proyecto.prototicket.schemas.PuntoVenta.PuntoVentaRepository;
import proyecto.prototicket.schemas.Ruta.RutaRepository;
import proyecto.prototicket.schemas.Ticket.TicketRepository;
import proyecto.prototicket.schemas.TicketDatabase;

public class Configuracion extends AppCompatActivity {


    private static final String URL_BUS ="https://btsbymetis.herokuapp.com/bus/api_buses";
    private static final String URL_PUNTO_VENTAS ="https://btsbymetis.herokuapp.com/punto_venta/api_puntoVentas";
    private static final String URL_RUTA ="https://btsbymetis.herokuapp.com/rutas/api_rutas";
    private static final String URL_ITINERARIO = "https://btsbymetis.herokuapp.com/itinerario/api_itinerarios";
    private static final String URL_EMPRESA = "https://btsbymetis.herokuapp.com/usuarios/api_empresas";
    private BusRepository br;
    private PuntoVentaRepository pvr;
    private RutaRepository rr;
    private TicketRepository ticketRepository;
    private ItinerarioRepository ir;
    private EmpresaRepository er;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Intent intent = new Intent(this, CrearTicket.class);
                startActivity(intent);
                break;
            case R.id.item2:
                Intent intent1 = new Intent(this, VerificarTicket.class);
                startActivity(intent1);
                break;
            case R.id.item4:
                Intent intent2 = new Intent(this, Pre_Itinerario.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void click_sincronizar(View view) {
        br = new BusRepository();
        pvr = new PuntoVentaRepository();
        rr = new RutaRepository();
        ir = new ItinerarioRepository();
        er = new EmpresaRepository();

        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        br.getBuses(URL_BUS,db);
        pvr.getPuntoVentas(URL_PUNTO_VENTAS,db);
        rr.getRuta(URL_RUTA,db);
        ir.getItinerario(URL_ITINERARIO, db);
        er.getEmpresa(URL_EMPRESA, db);
    }


    public void clic_sync_tiquetes(View view) {
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        ticketRepository = new TicketRepository();
        ticketRepository.restTiquete(db);
    }
}
