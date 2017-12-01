package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.Empresa.Empresa;
import proyecto.prototicket.schemas.TicketDatabase;

public class Itinerario extends AppCompatActivity implements LifecycleRegistryOwner {


    private String variables;
    private ListView listView;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerario);

        //tomo los datos del intent

        Bundle bundle = getIntent().getExtras();
        listView = (ListView)findViewById(R.id.lvItinerario);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue    = (String)listView.getItemAtPosition(position);



                Intent intent = new Intent(getApplicationContext(), CrearTicket.class);

                intent.putExtra("datosItinerario",itemValue);
                intent.putExtra("from_act", "Itinerario");
                startActivity(intent);
            }
        });
        if (bundle != null) {
            variables = bundle.getString("itinerario");
        } else {
            Toast.makeText(Itinerario.this, "vacio", Toast.LENGTH_LONG).show();
        }

        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();

        cargarItinerario(db);
    }

    private void cargarItinerario(TicketDatabase db) {

        List<String> rutaOrigen = new ArrayList<String>();
        String split[] = variables.split("-");
        String empresaId = split[0];
        String origen = split[1];
        String destino = split[2];
        String tipoServicio = split[3];
        if (empresaId.equals("null")){
            db.ItinerarioDao().verItinerario(origen, destino).observe(Itinerario.this,(List<proyecto.prototicket.schemas.Itinerario.Itinerario> strinList) -> {

                for (proyecto.prototicket.schemas.Itinerario.Itinerario item : strinList) {
                    String origendb = item.getOrigen().toString();
                    String destinodb = item.getDestino().toString();
                    String fechaSalida = item.getFecha_salida().toString();
                    String horaSalida = item.getHora_salida().toString();
                    String numero_bus = item.getNumero_bus().toString();
                    String empresa_id = item.getEmpresa().toString();

                    db.empresaDao().verEmpresaPorId(empresa_id).observe(Itinerario.this,(List<Empresa> empresaList)->{
                        for(Empresa item1 : empresaList){

                            String nombredb = item1.getNombre().toString();

                            db.busDao().verPlacaPorNumero(numero_bus).observe(Itinerario.this, (List<Bus> busList)->{
                               for(Bus item2:busList){

                                   String placadb = item2.getPlaca();
                                   rutaOrigen.add(origendb + "-" + destinodb + "  " + fechaSalida + "  " + horaSalida + "  " + nombredb + "  " + placadb);
                               }


                            });

                        }
                    });

                }
            });
        }else{
            db.ItinerarioDao().verItinerarioEmpresa(empresaId, origen, destino).observe(Itinerario.this,(List<proyecto.prototicket.schemas.Itinerario.Itinerario> strinList) -> {

                for (proyecto.prototicket.schemas.Itinerario.Itinerario item : strinList) {
                    String origendb = item.getOrigen().toString();
                    String destinodb = item.getDestino().toString();
                    String fechaSalida = item.getFecha_salida().toString();
                    String horaSalida = item.getHora_salida().toString();
                    String numero_bus = item.getNumero_bus().toString();
                    db.empresaDao().verEmpresaPorId(empresaId).observe(Itinerario.this,(List<Empresa> empresaList)->{
                        for(Empresa item1 : empresaList){
                            String nombredb = item1.getNombre().toString();

                            db.busDao().verPlacaPorNumero(numero_bus).observe(Itinerario.this, (List<Bus> busList)-> {
                                for (Bus item2 : busList) {

                                    String placadb = item2.getPlaca();
                                    rutaOrigen.add(origendb + "-" + destinodb + "  " + fechaSalida + "  " + horaSalida + "  " + nombredb + "  " + placadb);
                                }
                            });
                        }

                    });


                }
            });
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                rutaOrigen );

        listView.setAdapter(arrayAdapter);
    }



    @Override
    protected void onPause() {
        listView.setAdapter(null);
        super.onPause();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}

