package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import proyecto.prototicket.schemas.TicketDatabase;

public class Itinerario extends AppCompatActivity implements LifecycleRegistryOwner {


    private String variables;

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
        db.ItinerarioDao().verItinerario(empresaId, origen, destino).observe(Itinerario.this,(List<proyecto.prototicket.schemas.Itinerario.Itinerario> strinList) -> {

            List<String> itemLista = new ArrayList<String>();

            for (proyecto.prototicket.schemas.Itinerario.Itinerario item : strinList) {
                String origendb = item.getOrigen().toString();
                String destinodb = item.getDestino().toString();
                String fechaSalida = item.getFecha_salida().toString();
                String horaSalida = item.getHora_salida().toString();



            }

        });

    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}

