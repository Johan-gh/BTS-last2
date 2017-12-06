package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import proyecto.prototicket.schemas.Ruta.Ruta;
import proyecto.prototicket.schemas.Ticket.TicketDb;
import proyecto.prototicket.schemas.TicketDatabase;

public class DepacharBus extends AppCompatActivity implements LifecycleRegistryOwner {

    //private Spinner placas;
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    AutoCompleteTextView txtplacas;
    private List<String> valores = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depachar_bus);


        txtplacas = (AutoCompleteTextView) findViewById(R.id.txtplaca);


        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();


        autocompletarPlacas(db,this.txtplacas);



    }

    private void guardarDatos(TicketDatabase db){
        new AsyncTask<Void,Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {

                HashMap<String, String> rutaList = new HashMap<String, String>();
                HashMap<String, String> contador = new HashMap<String, String>();

                for(TicketDb item : db.ticketDao().obtenerTicketPorPlaca(txtplacas.getText().toString())){
                    String ruta = item.getRuta().toString();
                    String valor = item.getValor().toString();
                    String despachado = item.getDespachado().toString();
                    String[] split = ruta.split("-");
                    String id = split[0];
                    String c = "1";

                    for(Ruta r: db.rutaDao().obtenerRutaPorId(id)) {


                        String origen = r.getOrigen().toString();
                        String destino = r.getDestino().toString();
                        String origenDestino = origen+"-"+destino;

                        try {
                            if(rutaList.size() != 0) {
                                if (rutaList.containsKey(origenDestino)) {
                                    String item1 = rutaList.get(origenDestino);
                                    float total = Float.parseFloat(item1) + Float.parseFloat(valor);
                                    rutaList.remove(origenDestino);
                                    rutaList.put(origenDestino, String.valueOf(total));

                                    String item4 = contador.get(origenDestino);
                                    int suma = Integer.parseInt(item4) +1;
                                    contador.remove(origenDestino);
                                    contador.put(origenDestino, String.valueOf(suma));
                                } else {
                                    rutaList.put(origenDestino, valor);
                                    contador.put(origenDestino,String.valueOf(c));

                                }
                            }else{
                                rutaList.put(origenDestino, valor);
                                contador.put(origenDestino,String.valueOf(c));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            }
        }.execute();

    }


    private void autocompletarPlacas(TicketDatabase db, AutoCompleteTextView txt){

        db.ticketDao().obtenerPlacasBuses("False").observe( this, (List<TicketDb> strinList) ->{
            List<String> placasList = new ArrayList<String>();

            for(TicketDb item: strinList){
                String placa = item.getPlacaBus();
                if(placasList.indexOf(placa) < 0) {
                    placasList.add(placa);
                }

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,placasList);
            txt.setAdapter(adapter);
            txt.setThreshold(1);
        });

    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    public void click_imprimir(View view) {
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();

        guardarDatos(db);
    }

    /*private void cargarPlacas(){
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();

        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... voids) {
                for(TicketDb item: db.ticketDao().obtenerPlacasBuses("False")){
                    String placa = item.getPlacaBus().toString();
                    if(valores.indexOf(placa) < 0) {
                        valores.add(placa);
                    }
                }

                return null;
            }
        }.execute();

    }*/
}
