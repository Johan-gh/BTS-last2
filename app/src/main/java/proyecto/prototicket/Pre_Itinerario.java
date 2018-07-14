package proyecto.prototicket;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import proyecto.prototicket.schemas.Empresa.Empresa;
import proyecto.prototicket.schemas.Itinerario.Itinerario;
import proyecto.prototicket.schemas.TicketDatabase;

public class Pre_Itinerario extends AppCompatActivity implements LifecycleRegistryOwner {


    private Spinner servicio;

    private String tipoServicio;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private Dictionary<String, String> empresaId = new Hashtable<>();
    private Dictionary<String, String> itinerarioId = new Hashtable<>();
    private AutoCompleteTextView txtEmpresa;
    private AutoCompleteTextView txtOrigen;
    private AutoCompleteTextView txtDestino;

    private Button btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre__itinerario);

        txtEmpresa = (AutoCompleteTextView) findViewById(R.id.txtEmpresa);
        txtOrigen = (AutoCompleteTextView) findViewById(R.id.txtOrigen);
        txtDestino = (AutoCompleteTextView) findViewById(R.id.txtDestino);

        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();

        autoCompletarEmpresa(db, this.txtEmpresa);
        autoCompletarOrigen(db,this.txtOrigen);
        autoCompletarDestino(db, this.txtDestino);

        servicio = (Spinner) findViewById(R.id.txtservicio);
        String[] valores = {"Servicio normal", "Servicio especial", "Ambos servicios"};
        servicio.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, valores));
        servicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                tipoServicio = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(
                        txtOrigen.getText().toString().equals("")||
                        txtDestino.getText().toString().equals("")){

                    Toast.makeText(Pre_Itinerario.this,"Debe ingresar todos los campos",Toast.LENGTH_LONG).show();

                }else{
                    String iti = empresaId.get(txtEmpresa.getText().toString()) + "-" + txtOrigen.getText().toString() + "-" + txtDestino.getText().toString()+"-"+tipoServicio;

                    Intent intent = new Intent(Pre_Itinerario.this, proyecto.prototicket.Itinerario.class);
                    intent.putExtra("itinerario", iti);
                    startActivity(intent);
                }
            }

        });


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
                return true;
            case R.id.it_bus:
                Intent i_despachar_bus = new Intent(this, DepacharBus.class);
                startActivity(i_despachar_bus);
                return true;
            case R.id.it_verificar_ticket:
                Intent intent1 = new Intent(this, VerificarTicket.class);
                startActivity(intent1);
                return true;
                 /*case R.id.it_cierre:
                Intent i_cierre =new Intent(this,C)*/
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

    private void autoCompletarEmpresa(TicketDatabase db, AutoCompleteTextView txt){
        db.empresaDao().verEmpresa().observe(this, (List<Empresa> strinList) -> {

            List<String> empresas = new ArrayList<String>();

            for(Empresa empresa: strinList){
                String nombre = empresa.getNombre().toString();
                String id = empresa.getId().toString();
                empresaId.put(nombre, id);
                empresas.add(nombre);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,empresas);
            txt.setAdapter(adapter);
            txt.setThreshold(1);

        });
    }


    private void autoCompletarOrigen(TicketDatabase db, AutoCompleteTextView txt){
        db.ItinerarioDao().verItinerarioOrigen().observe(this, (List<Itinerario> strinList) ->{

            List<String> rutaOrigenes = new ArrayList<String>();

            for(Itinerario it: strinList) {

                String origen = it.getOrigen().toString();
                String destino = it.getDestino().toString();
                String id = it.getId().toString();

                if(rutaOrigenes.indexOf(origen) < 0){
                    rutaOrigenes.add(origen);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,rutaOrigenes);
            txt.setAdapter(adapter);
            txt.setThreshold(1);
        });
    }


    private void autoCompletarDestino(TicketDatabase db, AutoCompleteTextView txt){
        db.ItinerarioDao().verItinerarioOrigen().observe(this, (List<Itinerario> strinList) -> {

            List<String> rutaDestinos = new ArrayList<String>();

            for(Itinerario it: strinList){
                String destino = it.getDestino();

                if(rutaDestinos.indexOf(destino) < 0) {
                    rutaDestinos.add(destino);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,rutaDestinos);
            txt.setAdapter(adapter);
            txt.setThreshold(1);

        });
    }

    /*private void autoCompletarOrigen(TicketDatabase db, AutoCompleteTextView txt){

        List<String> rutaOrigen = new ArrayList<String>();
        for (Itinerario item:db.ItinerarioDao().verItinerarioOrigen(empresaId.get(txtEmpresa.getText().toString()))) {
            String origen = item.getOrigen();
            String id = item.getId();
            rutaOrigen.add(origen);

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,rutaOrigen);
        txt.setAdapter(adapter);
        txt.setThreshold(1);

    }*/



    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}

