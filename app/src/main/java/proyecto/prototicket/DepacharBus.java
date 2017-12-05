package proyecto.prototicket;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import proyecto.prototicket.schemas.Ticket.TicketDb;
import proyecto.prototicket.schemas.TicketDatabase;

public class DepacharBus extends AppCompatActivity {

    private Spinner placas;

    private String traerPlaca;
    private List<String> valores = new ArrayList<String>();

    private String[] array={"1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depachar_bus);

        placas = (Spinner) findViewById(R.id.txtplaca);
        cargarPlacas();
        placas.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array));

        placas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                traerPlaca = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarPlacas(){
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

    }
}
