package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    private String variableCrearT;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerario);

        //tomo los datos del intent

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            variables = bundle.getString("itinerario");
        } else {
            Toast.makeText(Itinerario.this, "vacio", Toast.LENGTH_LONG).show();
        }

        listView = (ListView)findViewById(R.id.lvItinerario);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue    = (String)listView.getItemAtPosition(position);


                Intent intent = new Intent(Itinerario.this, CrearTicket.class);
                intent.putExtra("variableCargar",variableCrearT);
                startActivity(intent);
            }
        });



        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();

        cargarItinerario(db);
    }

    private void cargarItinerario(TicketDatabase db) {

        new AsyncTask<Void, Void, Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Boolean doInBackground(Void... voids) {

                List<String> rutaOrigen = new ArrayList<String>();
                String split[] = variables.split("-");
                String empresaId = split[0];
                String origen = split[1];
                String destino = split[2];
                String tipoServicio = split[3];

                if (empresaId.equals("null")) {

                    for (proyecto.prototicket.schemas.Itinerario.Itinerario item : db.ItinerarioDao().verListaItinerario(origen, destino)) {
                        String origendb = item.getOrigen().toString();
                        String destinodb = item.getDestino().toString();
                        String fechaSalida = item.getFecha_salida().toString();
                        String horaSalida = item.getHora_salida().toString();
                        String numero_bus = item.getNumero_bus().toString();
                        String empresa_id = item.getEmpresa().toString();

                        for( Empresa item1 : db.empresaDao().verNombreEmpresaId(empresa_id)){
                            String nombreEmpresa = item1.getNombre().toString();

                            for(Bus item2 : db.busDao().verPlaca(numero_bus)) {

                                String placaBus = item2.getPlaca().toString();

                                variableCrearT = origendb + "-" + destinodb + "~" + fechaSalida + "~" + horaSalida + "~" + nombreEmpresa + "~" + placaBus;
                                rutaOrigen.add(origendb + "-" + destinodb + "  " + fechaSalida + "  " + horaSalida + "  " + nombreEmpresa + "  " + placaBus);
                                String t = "";
                            }
                        }


                    }

                } else {

                    for (proyecto.prototicket.schemas.Itinerario.Itinerario item : db.ItinerarioDao().verItinerarioEmpresa(empresaId, origen, destino)) {
                        String origendb = item.getOrigen().toString();
                        String destinodb = item.getDestino().toString();
                        String fechaSalida = item.getFecha_salida().toString();
                        String horaSalida = item.getHora_salida().toString();
                        String numero_bus = item.getNumero_bus().toString();
                        String empresa_id = item.getEmpresa().toString();

                        for( Empresa item1 : db.empresaDao().verNombreEmpresaId(empresa_id)){
                            String nombreEmpresa = item1.getNombre().toString();

                            for(Bus item2 : db.busDao().verPlaca(numero_bus)) {

                                String placaBus = item2.getPlaca().toString();

                                variableCrearT = origendb + "-" + destinodb + "~" + fechaSalida + "~" + horaSalida + "~" + nombreEmpresa + "~" + placaBus;
                                rutaOrigen.add(origendb + "-" + destinodb + "  " + fechaSalida + "  " + horaSalida + "  " + nombreEmpresa + "  " + placaBus);
                                String t = "";
                            }
                        }


                    }

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Itinerario.this, android.R.layout.simple_list_item_1, rutaOrigen);

                listView.setAdapter(arrayAdapter);

                return true;

            }
        }.execute();

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

