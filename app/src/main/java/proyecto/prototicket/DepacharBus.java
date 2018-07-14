package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.schemas.Ruta.Ruta;
import proyecto.prototicket.schemas.Ticket.TicketDb;
import proyecto.prototicket.schemas.TicketDatabase;

public class DepacharBus extends AppCompatActivity implements LifecycleRegistryOwner {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    BluetoothUtils bT;
    AutoCompleteTextView txtplacas;
    EditText txtpasajeros;
    ImageButton btn_plus,btn_minus;
    HashMap<String, String> rutaList = new HashMap<String, String>();
    HashMap<String, String> contador = new HashMap<String, String>();
    List<String> listaRutas = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depachar_bus);
        txtplacas = (AutoCompleteTextView) findViewById(R.id.txtplaca);
        txtpasajeros = findViewById(R.id.txtNumPasajeros);

        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();


        autocompletarPlacas(db,this.txtplacas);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_ticket:
                Intent i_crear_ticket = new Intent(this, CrearTicket.class);
                startActivity(i_crear_ticket);
           /* case R.id.it_bus:
                Intent i_despachar_bus = new Intent(this, DepacharBus.class);
                startActivity(i_despachar_bus);*/
                return true;
            case R.id.it_verificar_ticket:
                Intent intent1 = new Intent(this, VerificarTicket.class);
                startActivity(intent1);
                return true;
                 /*case R.id.it_cierre:
                     Configuracion c= new Configuracion();
                     c.cierre();
                     return true;*/
            case R.id.it_itinerario:
                Intent intent2 = new Intent(this, Pre_Itinerario.class);
                startActivity(intent2);
                return true;

            case R.id.it_bluetooth:
                Intent intent3 =new Intent(this, BluetoothActivity.class);
                startActivity(intent3);
                return true;
            case R.id.it_ajustes:
                Intent i_ajustes =new Intent(this,Configuracion.class);
                startActivity(i_ajustes);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void autocompletarPlacas(TicketDatabase db, AutoCompleteTextView txt){
        /*db.busDao().verPlacas().observe( this, (List<String> strinList) -> {
            // TODO Considerar lo siguiente y ojala modificar para eliminar
            *//*
            Esto se puede dejar asi y no hay problema, sin embargo, hay un tema de arquitectura, en
            vez de usar esto directamente debemos considerar crear un repositorio para la informacion y todo eso
            ver https://developer.android.com/topic/libraries/architecture/guide.html

            Lo hice asi de forma rapida y el patron puede reproducirse para otras cosas, sin embargo a
            mediano plazo consideraremos el repossitorio con inyeccion de dependencia sy dagger...
             *//*
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,strinList);
            txt.setAdapter(adapter);
            txt.setThreshold(1);


        });*/
        //no despachados
        db.ticketDao().obtenerPlacasBuses("false").observe( this, (List<TicketDb> strinList) ->{
            List<String> placasList = new ArrayList<String>();

            for(TicketDb item: strinList){
                String placa = item.getPlacaBus();
                if(placasList.indexOf(placa) < 0) {
                    placasList.add(placa);
                }
                if (placasList.size()==0){
                    fastToast("No se han despachado buses aún");
                }

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,placasList);
            txt.setAdapter(adapter);
            txt.setThreshold(1);
        });
    }

    public void click_imprimir(View view) {
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        if (txtpasajeros.getText().toString().equals("") ||
                txtplacas.getText().toString().equals(""))
        {
            fastToast("Por favor ingrese todos los datos solicitados");
        }else{
            guardarDatos(db);
        }

    }

    private void fastToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void guardarDatos(TicketDatabase db){
        new AsyncTask<Void,Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                String despachado="";
                List<TicketDb> tickets_cerrados = new ArrayList<>();
                List<TicketDb> tl=   db.ticketDao().obtenerTicketPorPlaca(txtplacas.getText().toString());
                for(TicketDb item : tl) {
                    String ruta = item.getRuta().toString();
                    String valor = item.getValor().toString();
                    despachado = item.getDespachado().toString();
                    String[] split = ruta.split("-");
                    String id = split[0];
                    String c = "1";
                    String p = txtplacas.getText().toString();
                    String a = item.getPlacaBus().toString();
                    List<Ruta> rl= db.rutaDao().obtenerRutaPorId(id);
                    if (despachado.equals("false") ) {
                        for (Ruta r : rl) {

                            String origen = r.getOrigen().toString();
                            String destino = r.getDestino().toString();
                            String origenDestino = origen + "-" + destino;
                            try {
                                if (rutaList.size() != 0) {
                                    if (rutaList.containsKey(origenDestino)) {
                                        String item1 = rutaList.get(origenDestino);
                                        float total = Float.parseFloat(item1) + Float.parseFloat(valor);
                                        rutaList.remove(origenDestino);
                                        rutaList.put(origenDestino, String.valueOf(total));

                                        String item4 = contador.get(origenDestino);
                                        int suma = Integer.parseInt(item4) + 1;
                                        contador.remove(origenDestino);
                                        contador.put(origenDestino, String.valueOf(suma));
                                    } else {
                                        rutaList.put(origenDestino, valor);
                                        contador.put(origenDestino, String.valueOf(c));
                                        listaRutas.add(origenDestino);
                                    }
                                } else {
                                    rutaList.put(origenDestino, valor);
                                    contador.put(origenDestino, String.valueOf(c));
                                    listaRutas.add(origenDestino);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        TicketDb ticketDb = new TicketDb(item.getUuid().toString(), item.getRuta().toString(), item.getPlacaBus().toString()
                                , item.getValor().toString(), item.getFecha_nicial().toString(), item.getPunto_venta().toString(),
                                item.getHora_llegada().toString(), item.getFechaViaje().toString(), item.getHora_salida().toString(),
                                item.getSincro().toString(), item.getCierre().toString(), item.getEmpleado().toString(), item.getEmpresa().toString(),
                                "true");
                        tickets_cerrados.add(ticketDb);
                        db.ticketDao().actualizarTiquete(ticketDb);

                    }
                }
                if (despachado.equals("false")){
                    imprimirCierre(rutaList,contador,listaRutas);

                }
                return null;
            }
        }.execute();

    }


    private void imprimirCierre(HashMap<String, String> rutaList, HashMap<String, String> contador, List<String> listaRutas) {
        String logo_metis = "^FX Top section with company logo, name and address." +
                "^CF0,30" +
                "^FO10,20^GFA,1710,1710,19,,:::::::::gG07FE,Y01KF,X01FC0IF8,W01FC1IF8,Q0F8I01FC3IF8,Q07FI0FE3IFC,Q03KF3IFE,Q01OFC,Q01OF,R0NFC,R0KFE7F8,R0KF0FE,R03IF83FA,R03FFE07FC,R03DF81FFD,R033E03F7E,S07C07C,R01F81F8,R03F03C,R07E,R0DC,Q01B8,P0133,P0266,P04CE,P099C,O0139C,O01738,O02E78V01FC,J07FFI0C79JFE3KFCFF80IFC,J07FF001CF1JFE3KFCFF83JF,J07FF8038F1JFE3KFCFF87JF,J07FF8031F1JFE3KFCFF8JFE,J07FF8071F1JFE3KFCFF8JFE,J07FFC0E3F1JFE3KFCFF8JFE,J07FFC0E3F1FFK07FC00FF9FF80C,J07FFC1C3F1FFK03FC00FF9FF8,J07FFE1C7F1FF8J07FC00FF9IF8,J07FFE387F1JFC007FC00FF8JF,J07FFE387F1JFC007FC00FF8JFC,J07IF78FF1JFC007FC00FF87IFE,J07FBF70FF1JFC007FC00FF83JF,J07FBFF0FF1JFC007FC00FF81JF8,J07FBFF0FF1FF8J07FC00FF803IF8,J07F9FE1FF1FFK07FC00FF8003FF8,J07F9FE1FF1FFK07FC00FF8I0FF8,J07F8FE1FF1FFK07FC00FF8F00FF8,J07F8FE1FF1KF007FC00FF8KF8,J07F8FC1FF1KF007FC00FF9KF8,J07F87C1FF1KF007FC00FF9KF,J07F87C1FF1KF003FC00FF9KF,J07F83D1FF1KF007FC00FF9JFC,J07F83D1FF1KF003FC00FF87IF8,M03Dg01F,M01D,:M019,N09,N09J0A01K0401041,N01K04904008048204,N01N02K01442,V041K014008,S084820248448I08,,::::::::::::::::^FS" +
                "^FO160,30^FDMetis Consultores^FS" +
                "^CF0,25" +
                "^FO160,65^FDMedellin^FS" +
                "^FO160,90^FDColombia (COL)^FS" +
                "^FO20,115^GB340,1,3^FS";
        String datos = "^FO130,150^FDCierre de viaje^FS" +
                "^FO20,185^GB340,1,3^FS"+
                "^FO20,220^FDNumero de pasajeros: "+txtpasajeros.getText().toString()+"^FS";
        int pos = 250;
        float total=0;
        for (String item :
                listaRutas) {

            String valor = rutaList.get(item);
            String num = contador.get(item);
            datos = datos +  "^FO20,"+pos+ "^FDRuta: "+item+"^FS";
            pos = pos+40;
            datos = datos +  "^FO20,"+pos + "^FDValor: "+valor+"^FS";
            pos = pos+40;
            datos = datos +  "^FO20,"+pos + "^FDCantidad: "+num+"^FS";
            pos = pos+40;
            total += (Float.parseFloat(valor));
        }
        int a =(int)total;
        String totalString=String.valueOf(a);
        datos = datos +  "^FO20,"+pos + "^FDtotal Vendido: "+totalString+"^FS";
        int posPlaca = pos+60;
        int posfinal =pos + 100;
        String tiquete_texto = "^XA^POI^L"+ posfinal + logo_metis + datos +  "^CFZ,25^FO30,"+ posPlaca +"^FDPlaca: "+txtplacas.getText().toString()+"^FS^XZ";
        try {
            bT.write(tiquete_texto);
            Intent intent2 = new Intent(DepacharBus.this,Configuracion.class);
            startActivity(intent2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    protected void onResume() {
        try {
            bT = new BluetoothUtils(DepacharBus.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        try{
            bT.closeBT();
        }catch(Exception e){

        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try{
            bT.closeBT();
        }catch(Exception e){

        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try{
            bT.closeBT();
        }catch(Exception e){

        }
        super.onDestroy();
    }

    public void countUp(View view) {
        if(txtpasajeros.getText().toString().equals("")){
            txtpasajeros.setText("0");
        }
        count = Integer.parseInt(txtpasajeros.getText().toString()) + 1;
        txtpasajeros.setText(Integer.toString(count));
    }

    public void countDown(View view) {
        if(txtpasajeros.getText().toString().equals("")){
            txtpasajeros.setText("0");
            count = 0;
        }else {
            if(Integer.parseInt(txtpasajeros.getText().toString()) <= 0){
                txtpasajeros.setText(Integer.toString(0));
            }
            else {
                count = Integer.parseInt(txtpasajeros.getText().toString()) - 1;
                txtpasajeros.setText(Integer.toString(count));
            }
        }
    }
}
