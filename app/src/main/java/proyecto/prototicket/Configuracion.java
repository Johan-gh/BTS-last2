package proyecto.prototicket;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import proyecto.prototicket.Utils.BluetoothUtils;
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
    BluetoothUtils bT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        //bT = new BluetoothUtils(Configuracion.this);

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


    public void click_sync_tiquetes(View view) {
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        ticketRepository = new TicketRepository();
        ticketRepository.restTiquete(db);
    }

    public void click_cierre(View view){
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        ticketRepository = new TicketRepository();
        ticketRepository.restCierre(db);
    }

    private SimpleDateFormat setDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }



    public void imprimirCierreCaja(String empleado, String fecha, String hora,
                                   String num_tiquetes, String total_valor_tiquetes, AsyncTask<Void, Void, String> asyncTask){
        Calendar calendario = new GregorianCalendar();
        bT = new BluetoothUtils(Configuracion.this);
        String logo_metis = "^FX Top section with company logo, name and address." +
                "^CF0,30" +
                "^FO10,20^GFA,1710,1710,19,,:::::::::gG07FE,Y01KF,X01FC0IF8,W01FC1IF8,Q0F8I01FC3IF8,Q07FI0FE3IFC,Q03KF3IFE,Q01OFC,Q01OF,R0NFC,R0KFE7F8,R0KF0FE,R03IF83FA,R03FFE07FC,R03DF81FFD,R033E03F7E,S07C07C,R01F81F8,R03F03C,R07E,R0DC,Q01B8,P0133,P0266,P04CE,P099C,O0139C,O01738,O02E78V01FC,J07FFI0C79JFE3KFCFF80IFC,J07FF001CF1JFE3KFCFF83JF,J07FF8038F1JFE3KFCFF87JF,J07FF8031F1JFE3KFCFF8JFE,J07FF8071F1JFE3KFCFF8JFE,J07FFC0E3F1JFE3KFCFF8JFE,J07FFC0E3F1FFK07FC00FF9FF80C,J07FFC1C3F1FFK03FC00FF9FF8,J07FFE1C7F1FF8J07FC00FF9IF8,J07FFE387F1JFC007FC00FF8JF,J07FFE387F1JFC007FC00FF8JFC,J07IF78FF1JFC007FC00FF87IFE,J07FBF70FF1JFC007FC00FF83JF,J07FBFF0FF1JFC007FC00FF81JF8,J07FBFF0FF1FF8J07FC00FF803IF8,J07F9FE1FF1FFK07FC00FF8003FF8,J07F9FE1FF1FFK07FC00FF8I0FF8,J07F8FE1FF1FFK07FC00FF8F00FF8,J07F8FE1FF1KF007FC00FF8KF8,J07F8FC1FF1KF007FC00FF9KF8,J07F87C1FF1KF007FC00FF9KF,J07F87C1FF1KF003FC00FF9KF,J07F83D1FF1KF007FC00FF9JFC,J07F83D1FF1KF003FC00FF87IF8,M03Dg01F,M01D,:M019,N09,N09J0A01K0401041,N01K04904008048204,N01N02K01442,V041K014008,S084820248448I08,,::::::::::::::::^FS" +
                "^FO160,30^FDMetis Consultores^FS" +
                "^CF0,25" +
                "^FO160,65^FDMedellin^FS" +
                "^FO160,90^FDColombia (COL)^FS" +
                "^FO20,115^GB340,1,3^FS";
        String datos = "^FO140,150^FDCierre de Caja^FS" +
                "^FO20,185^GB340,1,3^FS" +
                "^FO20,220^FDFecha:"+ setDate().format(Calendar.getInstance().getTime()).toString()+ "^FS" +
                "^FO20,250^FDHora: "+ calendario.get(Calendar.HOUR_OF_DAY)+":"+calendario.get(Calendar.MINUTE) +"^FS" +
                "^FO20,280^FDNumero de tiquetes: " + num_tiquetes +"^FS" +
                "^FO20,310^FDValor total: "+ total_valor_tiquetes + "^FS" +
                "^FO20,360^FDElaborado por: "+ empleado+"^FS";
        String qr = "^FO40,400^BQN,2,8^FD__"+setDate().format(Calendar.getInstance().getTime()).toString()+
                "_"+calendario.get(Calendar.HOUR_OF_DAY)+":"+num_tiquetes+"_"+total_valor_tiquetes
                +"_"+empleado+"^FS";
        String tiquete = "^XA^POI^LL780" + logo_metis + datos+qr+ "^XZ";
        try {
            bT.write(tiquete);
            bT.closeBT();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        try {
            bT.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        try {
            bT.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            bT.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
