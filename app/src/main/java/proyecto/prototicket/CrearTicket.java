package proyecto.prototicket;

import android.Manifest;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import com.google.zxing.pdf417.PDF417Reader;
//import com.google.zxing.pdf417.encoder.PDF417;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.models.Ticket;
import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.PuntoVenta.PuntoVenta;
import proyecto.prototicket.schemas.Ruta.Ruta;
import proyecto.prototicket.schemas.Ticket.TicketDb;
import proyecto.prototicket.schemas.TicketDatabase;

import static proyecto.prototicket.Utils.ClassCompression.encode;


public class CrearTicket extends AppCompatActivity implements View.OnClickListener,LifecycleRegistryOwner, View.OnTouchListener  {
    private final String clave = "eKNuL3ipnlLAzfxTstV7CM4vIZybztLcZ4ItqPZ9";
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    private EditText txtFecha, txtHora, txtCedula, txtPasajero, txtRH, txtTravelRoute, txtPosting;
    AutoCompleteTextView txtVehicle;
    AutoCompleteTextView txtPuntoVenta;
    AutoCompleteTextView txtOrigenDestino;
    List<TicketDb> tdbList ;

    private String precio;

    private String variables;

    private Dictionary<String, String> rutaId = new Hashtable<>();
    private Dictionary<String, String> puntoVentaId = new Hashtable<>();



    private  EditText txtPrecio;

    private ImageButton btnCodigoCedula;
    private Button btnSave;
    boolean busExiste;
    private UUID uuid ;
    private int dia, mes, anio, hora, minutos;


    private static final int CAMERA_CODE = 1888;
    BluetoothUtils bT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ticket);


        txtOrigenDestino = (AutoCompleteTextView) findViewById(R.id.txtTravelRoute);
        txtFecha = (EditText) findViewById(R.id.txtDate);
        txtHora = (EditText) findViewById(R.id.txtTime);
        txtPasajero = (EditText) findViewById(R.id.txtTraveler);
        txtCedula = (EditText) findViewById(R.id.txtCedula);
        txtRH = (EditText) findViewById(R.id.txtRH);
        txtPuntoVenta = (AutoCompleteTextView) findViewById(R.id.txtPosting);
        txtVehicle = (AutoCompleteTextView) findViewById(R.id.txtVehicle);
        txtPrecio = (EditText) findViewById(R.id.txtPrecio);

        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();


        autoCompletarBus(db,this.txtVehicle);
        autoCompletarPuntoVenta( db,this.txtPuntoVenta);
        autoCompletarRuta( db,this.txtOrigenDestino);

        btnCodigoCedula = (ImageButton) findViewById(R.id.btnCodigoCedula);

        //tomo los datos del intent

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            variables = bundle.getString("variableCargar");
            String[] split = variables.split("~");
            String origenDestino = split[0];
            String fechaSalida = split[1];
            String horaSalida = split[2];
            String placa = split[4];
            txtOrigenDestino.setText(origenDestino);
            txtFecha.setText(fechaSalida);
            txtHora.setText(horaSalida);
            txtVehicle.setText(placa);

        } else {
            Toast.makeText(CrearTicket.this, "vacio", Toast.LENGTH_LONG).show();
        }


        btnCodigoCedula.setOnClickListener(this);
        txtFecha.setOnClickListener(this);
        txtHora.setOnClickListener(this);
        txtPrecio.setOnTouchListener(this);


        btnSave = (Button) findViewById(R.id.btnSave);



        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(txtVehicle.getText().toString().equals("") ||
                        txtFecha.getText().toString().equals("") ||
                        txtPuntoVenta.getText().toString().equals("") ||
                        txtHora.getText().equals("") ||
                        txtOrigenDestino.getText().equals("") ||
                        txtPrecio.getText().toString().equals("") ||
                        txtPasajero.getText().toString().equals("") ||
                        txtCedula.getText().toString().equals("") ||
                        txtHora.getText().toString().equals(""))
                {

                    fastToast("Por favor ingrese todos los datos solicitados");
                }
                else{
                    try {

                        saveData(db);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });


        Intent intent = getIntent();

        String from_act = intent.getStringExtra("from_act");
        if(from_act == null){
            return;
        }

        if(from_act.equals("Itinerario")){
            // inicializar con extras
            String datosIti = intent.getStringExtra("datosItinerario");
            String[] datos = datosIti.split("  ");
            String[] ruta = datos[0].split("-");
            String origen = ruta[0].toString();
            String destino = ruta[1].toString();
            String fecha_viaje = datos[1].toString();
            String hora_viaje = datos[2].toString();
            String empresa = datos[3].toString();
            String numero_bus = datos[4].toString();
            txtOrigenDestino.setText(origen + "-" + destino);
            txtFecha.setText(fecha_viaje);
            txtHora.setText(hora_viaje);

            Log.d("De donde viene", "Itinerario");
        }else {
            // no se
            Log.d("De donde viene", "Npi");
        }

    }

    private void autoCompletarBus(TicketDatabase db, AutoCompleteTextView txt){
        db.busDao().verPlacas().observe( this, (List<String> strinList) -> {
            // TODO Considerar lo siguiente y ojala modificar para eliminar
            /*
            Esto se puede dejar asi y no hay problema, sin embargo, hay un tema de arquitectura, en
            vez de usar esto directamente debemos considerar crear un repositorio para la informacion y todo eso
            ver https://developer.android.com/topic/libraries/architecture/guide.html

            Lo hice asi de forma rapida y el patron puede reproducirse para otras cosas, sin embargo a
            mediano plazo consideraremos el repossitorio con inyeccion de dependencia sy dagger...
             */
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,strinList);
            txt.setAdapter(adapter);
            txt.setThreshold(1);
            

        });
    }

    private void autoCompletarPuntoVenta(TicketDatabase db, AutoCompleteTextView txt){
        db.puntoVentaDao().verPuntoVentas().observe( this, (List<PuntoVenta> strinList) -> {
            // TODO Considerar lo siguiente y ojala modificar para eliminar
            /*
            Esto se puede dejar asi y no hay problema, sin embargo, hay un tema de arquitectura, en
            vez de usar esto directamente debemos considerar crear un repositorio para la informacion y todo eso
            ver https://developer.android.com/topic/libraries/architecture/guide.html

            Lo hice asi de forma rapida y el patron puede reproducirse para otras cosas, sin embargo a
            mediano plazo consideraremos el repossitorio con inyeccion de dependencia sy dagger...
             */

            List<String> puntoDeVentas = new ArrayList<String>();
            for (PuntoVenta puntoVenta :
                    strinList) {
                String nombre = puntoVenta.getNombre().toString();
                String id = puntoVenta.getId().toString();
                puntoVentaId.put(nombre, id);
                puntoDeVentas.add(nombre);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,puntoDeVentas);
            txt.setAdapter(adapter);
            txt.setThreshold(1);

        });
    }

    private void autoCompletarRuta(TicketDatabase db, AutoCompleteTextView txt){
        db.rutaDao().verRutaOrigenDestino().observe( this, (List<Ruta> strinList) -> {
            // TODO Considerar lo siguiente y ojala modificar para eliminar
            /*
            Esto se puede dejar asi y no hay problema, sin embargo, hay un tema de arquitectura, en
            vez de usar esto directamente debemos considerar crear un repositorio para la informacion y todo eso
            ver https://developer.android.com/topic/libraries/architecture/guide.html

            Lo hice asi de forma rapida y el patron puede reproducirse para otras cosas, sin embargo a
            mediano plazo consideraremos el repossitorio con inyeccion de dependencia sy dagger...
             */
            List<String> rutasList = new ArrayList<String>();

            for (Ruta ruta :
                    strinList) {
                String origen = ruta.getOrigen().toString();
                String destino = ruta.getDestino().toString();
                String id = ruta.getId();
                String precio = ruta.getPrecio_E();
                String habilitada = ruta.getHabilitada();
                rutaId.put(origen + '-' + destino, id + '-' + precio);
                if (habilitada.equals("true")){
                    rutasList.add(origen + '-' + destino);
                }

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,rutasList);
            txt.setAdapter(adapter);
            txt.setThreshold(1);

        });
    }


    private SimpleDateFormat setDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public void onClick(View view) {

        if (view == txtFecha) {

            Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            anio = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {


                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    txtFecha.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth );
                }


            }, anio, mes, dia);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } else if (view == txtHora) {

            Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    txtHora.setText(hourOfDay + ":" + minute);
                }
            }
                    , hora, minutos, false);
            timePickerDialog.show();

        } else if (view == btnCodigoCedula) {
            //comprobar version de android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkPermission(Manifest.permission.CAMERA)) {
                    IntentIntegrator integrator = new IntentIntegrator(this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("Scan Cédula");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(true);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();

                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        //no se le ha preguntado aun por el permiso
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                    } else {
                        Toast.makeText(this, "Por favor, activa el permiso", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }
            } else {
                olderVersion();
            }
        }
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
            case R.id.item3:
                Intent intent = new Intent(this, Configuracion.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                fastToast(getString(R.string.ERROR_LECTURA_CEDULA));
            } else {
                String reading = null;
                String cleaned_reading = null;

                // Patron de extraccion de datos luego de limpiar la cedula
                Pattern cleaned_cc = Pattern.compile("(?:(?:\\d+H?) \\|\\| (?:\\d+){1,2}) \\|\\| (\\d+) \\|\\| ((?:\\p{L}{2,}+(?: \\|\\| )?)+) \\|\\| (M|F) \\|\\| (\\d{8}) \\|\\| (?:\\d+) \\|\\| ((?:A|B|O|(?:AB))(?:\\+|-)).*");
                HashMap<String,String> cedula = new HashMap<String, String>();
                reading = result.getContents();

                // limpieza de la cedula para soportar los formatos existentes
                cleaned_reading = reading.replaceAll("((\0+|\\s+)0(?=(M|F)\\d{8}))|\\s+|(\0+PubDSK_1\0+)|\0+|((?<=\\d)(?=\\p{L}{2,}))|((?<=M|F)(?=\\d{8}))|((?<=(M|F)\\d{8})(?=\\d+(O|A|B|(AB))(\\+|-)))|(?=(O|A|B|(AB))(\\+|-))|(?:(?<=\\d{8})(?=\\d{10}[^\\W\\d_]+))", " || ");

                // elimina huella digital que va despues del tipo de sangre
                cleaned_reading = cleaned_reading.replaceAll("(?<=(O|A|B|(AB))(\\+|-)).*","");
                Matcher cc_matcher = cleaned_cc.matcher(cleaned_reading);
                String message = null;
                String reading_status = null;

                if(cc_matcher.find()){
                    // Esto ocurre cuando la cedula se lee correctamente por el lector de PDF417 y ademas es posible extraer la informacion con las regex
                    reading_status = "Scanned";
                    cedula.put("cedula", cc_matcher.group(1));

                    // Extrae el nombre del grupo 2 y cambia el separador arbitrario que asignamos por un espacio
                    cedula.put("nombre", cc_matcher.group(2).replaceAll(" \\|\\| ", " "));
                    cedula.put("sexo", cc_matcher.group(3));
                    cedula.put("fecha_nacimiento", cc_matcher.group(4));
                    cedula.put("GRH", cc_matcher.group(5));

                    // Agregar a base de datos (ignorar por ahora, aqui se agregan cosas a la db y se actualiza la vista de los tiquetes de ser necesario)
                   // addData(cedula.get("cedula"), cedula.get("nombre"));
                   // updateList();

                    // Aqui ponemos la info en el formulario


                    txtPasajero.setText(cedula.get("nombre"));
                    txtCedula.setText(cedula.get("cedula"));
                    /*
                    * TODO: Quitar el RH de toda la interfaz, debe gusrdarse en la base de datos pero no verse en la APP ni imprimir en el tiquete
                    * TODO: Tambien hay que guardar sexo y fecha de nacimiento pero no mostrarlos ni en la GUI ni en el tiquete
                    * */
                    txtRH.setText(cedula.get("GRH"));

                    fastToast(getString(R.string.INFO__TIQUETE_CREADO_EXITOSAMENTE));
                }else{
                    // Esto ocurre cuando la cedula se lee correctamente por el lector de PDF417 y no es posible extraer la informacion con las regex
                    // significa o que no se esta leyendo una cedula o que la informacion esta malformada
                    fastToast(getString(R.string.ERROR_FORMATO_CC_MAL_FORMADO));
                }

                // Se muestra un dialog (solo para debugging, la idea es usar un toast para informar la lectura exitosa
                // La info esta en el hashmap HashMap<String, String> cedula con los campos cedula, nombre, sexo, fecha_nacimiento, GRH.
                // El nombre no hay que ordenarlo (por lo menos en esta etapa), nombres como de la santa cruz (no se si exista) imponen un constrain muy complicado
                // y la unica forma que se me ocurre de hacerlo es usando un algoritmo de NLP (se sale del alcance del proyecto y no aporta valor)
                // La opcion mas simple que veo que conserva generalidad es tener una tabla con nombres y apellidos comunes que ayude a decidir como reorganizar el nombre...
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void olderVersion() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan cédula");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        } else {
            Toast.makeText(this, "No tiene acceso al permiso", Toast.LENGTH_LONG).show();
        }
    }

    private Boolean checkPermission(String permission) {
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CODE:
                String permission = permissions[0];
                int result = grantResults[0];
                if (permission.equals(Manifest.permission.CAMERA)) {
                    //Comrobar si a sido aceptado o denegado el permiso
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        //acepto su permiso
                        IntentIntegrator integrator = new IntentIntegrator(this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Scan cédula");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(true);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.initiateScan();

                    } else {
                        //denego su permiso
                        return;
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    private void saveData(TicketDatabase db) {
        new AsyncTask<Void,Void,Boolean>(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean esta=false;
                int tamBuses = db.busDao().verificarPlaca(txtVehicle.getText().toString()).size();
                int tamPuntos = db.puntoVentaDao().verificarPuntoVenta(txtPuntoVenta.getText().toString()).size();
                String ruta = txtOrigenDestino.getText().toString();
                String rutas[] = ruta.split("-");
                int tamRuta = db.rutaDao().verificarRuta(rutas[0],rutas[1]).size();
                if (tamBuses >= 1 && tamPuntos >= 1 && tamRuta >= 1){


                    try {
                        imprimir(db);
                        tdbList  = db.ticketDao().verTiquete();


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                }else{
                    if (tamBuses == 0 ) {
                        fastToast("La placa no esta registrada en la base de datos");
                    }
                    if (tamPuntos == 0){
                        fastToast("El punto de venta no se le ha asignado.");
                    }
                    if (tamRuta == 0){
                        fastToast("La ruta no se le existe.");
                    }

                }
                return busExiste;
            }
        }.execute();

        //private EditText txtFecha, txtHora, txtDateBuy, txtCedula, txtPasajero, txtRH;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void imprimir(TicketDatabase db) throws IOException, NoSuchAlgorithmException {
            /*Ticket ticket = new Ticket();
            ticket.setBuyDate(txtDateBuy.getText().toString());
            ticket.setTravelDate(txtFecha.getText().toString());
            ticket.setTravelHour(txtHora.getText().toString());
            ticket.setTravelRoute(txtOrigenDestino.getText().toString());
            ticket.setDestiny(txtPuntoVenta.getText().toString());
            ticket.setVehicle(txtVehicle.getText().toString());
            ticket.setName(txtPasajero.getText().toString());
            ticket.setSchedule(txtCedula.getText().toString());
            ticket.setRh(txtRH.getText().toString());
            ticket.setPrecio(txtPrecio.getText().toString());*/

            MainActivity m = new MainActivity();
            String usuario = m.usuario.toString();
            String ruta2 = txtOrigenDestino.getText().toString();
            String rutaIdStr = rutaId.get(txtOrigenDestino.getText().toString());
            String[] rutaSplit = ruta2.split("-");
            String origen = rutaSplit[0];
            String destino = rutaSplit[1];
            String pvId = puntoVentaId.get(txtPuntoVenta.getText().toString());
            String precio = txtPrecio.getText().toString();
            String fecha_inicio = setDate().format(Calendar.getInstance().getTime()).toString();
            String fecha_viaje = txtFecha.getText().toString();
            String hora_salida = txtHora.getText().toString();
            String placaBus = txtVehicle.getText().toString();
            uuid= UUID.randomUUID();
            TicketDb ticketDb = new TicketDb(uuid.toString(),rutaIdStr, placaBus,precio, fecha_inicio, pvId, "10:59",fecha_viaje, hora_salida, "False","False","7","5","false" );
            ticketDb.signTicket(clave);
        String logo_metis = "^FX Top section with company logo, name and address." +
                "^CF0,30" +
                "^FO10,20^GFA,1710,1710,19,,:::::::::gG07FE,Y01KF,X01FC0IF8,W01FC1IF8,Q0F8I01FC3IF8,Q07FI0FE3IFC,Q03KF3IFE,Q01OFC,Q01OF,R0NFC,R0KFE7F8,R0KF0FE,R03IF83FA,R03FFE07FC,R03DF81FFD,R033E03F7E,S07C07C,R01F81F8,R03F03C,R07E,R0DC,Q01B8,P0133,P0266,P04CE,P099C,O0139C,O01738,O02E78V01FC,J07FFI0C79JFE3KFCFF80IFC,J07FF001CF1JFE3KFCFF83JF,J07FF8038F1JFE3KFCFF87JF,J07FF8031F1JFE3KFCFF8JFE,J07FF8071F1JFE3KFCFF8JFE,J07FFC0E3F1JFE3KFCFF8JFE,J07FFC0E3F1FFK07FC00FF9FF80C,J07FFC1C3F1FFK03FC00FF9FF8,J07FFE1C7F1FF8J07FC00FF9IF8,J07FFE387F1JFC007FC00FF8JF,J07FFE387F1JFC007FC00FF8JFC,J07IF78FF1JFC007FC00FF87IFE,J07FBF70FF1JFC007FC00FF83JF,J07FBFF0FF1JFC007FC00FF81JF8,J07FBFF0FF1FF8J07FC00FF803IF8,J07F9FE1FF1FFK07FC00FF8003FF8,J07F9FE1FF1FFK07FC00FF8I0FF8,J07F8FE1FF1FFK07FC00FF8F00FF8,J07F8FE1FF1KF007FC00FF8KF8,J07F8FC1FF1KF007FC00FF9KF8,J07F87C1FF1KF007FC00FF9KF,J07F87C1FF1KF003FC00FF9KF,J07F83D1FF1KF007FC00FF9JFC,J07F83D1FF1KF003FC00FF87IF8,M03Dg01F,M01D,:M019,N09,N09J0A01K0401041,N01K04904008048204,N01N02K01442,V041K014008,S084820248448I08,,::::::::::::::::^FS" +
                "^FO160,30^FDMetis Consultores^FS" +
                "^CF0,25" +
                "^FO160,65^FDMedellin^FS" +
                "^FO160,90^FDColombia (COL)^FS" +
                "^FO20,115^GB340,1,3^FS";
        String texto ="^FX Second section with recipient address and permit information." +
                "^CFA,20^FO20,140" +
                "^FDFecha de viaje: " + (txtFecha.getText().toString()) + "^FS"+
                "^FS^FO20,170^FDHora de viaje: " + (txtHora.getText().toString()) + "^FS"+
                "^FS^FO20,200^FDOrigen: "+origen + "^FS" +
                "^FO20,230^FDDestino: "+destino+"^FS" +
                "^FO20,260^FDPrecio: "+ (txtPrecio.getText().toString()) +"^FS" +
                "^FO20,290^FDVehiculo: "+ (txtVehicle.getText().toString()) +"^FS" +
                "^FO20,320^FDPasajero: "+(txtPasajero.getText().toString())+"^FS" +
                "^FO20,350^FDCedula: " +(txtCedula.getText().toString())+"^FS" +
                "^FO20,380^GB340,1,3^FS";
            String p = encode(ticketDb);

            /*String qr = "^POI^FO40,410^BQN,2,8^FD__"+(txtPasajero.getText().toString())+
                    "_"+(txtCedula.getText().toString())+"_"+(txtFecha.getText().toString())
                    +"_"+(txtHora.getText().toString())+"_"+(txtOrigenDestino.getText().toString())+
                    "_"+(txtPrecio.getText().toString())+"_"+uuid+"^FS";*/

            String qr = "^FO20,410^BQN,2,6^FD__"+ encode(ticketDb) + "^FS";
            String tiquete_texto = "^XA^POI^LL980" +qr+ logo_metis + texto +  "^CFZ,35^FO30,800^FDFELIZ VIAJE!^FS^XZ";




            db.ticketDao().crearTicket(ticketDb);


            try {
                //bT.sendData(ticket.getDataPrint());
                //bT = new BluetoothUtils(CrearTicket.this);
                bT.write(tiquete_texto);
                Intent intent2 = new Intent(CrearTicket.this,CrearTicket.class);
                startActivity(intent2);
                //bT.closeBT();
                //refrescar();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @Override
    protected void onResume() {
        try {
            bT = new BluetoothUtils(CrearTicket.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void fastToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void refrescar(){

        txtOrigenDestino.setText("");
        txtFecha.setText(" ");
        txtHora.setText(" ");
        txtPasajero.setText(" ");
        txtCedula.setText(" ");
        txtRH.setText(" ");
        txtPuntoVenta.setText(" ");
        txtVehicle.setText(" ");
        txtPrecio.setText(" ");
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.txtPrecio){
            String id = rutaId.get(txtOrigenDestino.getText().toString());
            if(txtOrigenDestino.getText().toString().equals("") || id == null || txtVehicle.getText().toString().equals("")){
                precio = "";
            }else {
                TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
                try {
                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... voids) {
                            String precio_n = "";
                            List<Bus> buses = db.busDao().verificarServicio(txtVehicle.getText().toString());
                            for (Bus it : buses) {
                                String split[] = (txtOrigenDestino.getText().toString()).split("-");
                                String origen = split[0];
                                String destino = split[1];
                                String empresaId = it.getEmpresaId().toString();
                                String servicio = it.getTipo_servicio().toString();
                                if (servicio.equals("Especial")) {
                                    precio_n = db.rutaDao().obtenerPrecioE(origen, destino, empresaId).toString();
                                    precio = precio_n;
                                } else if (servicio.equals("Normal")) {
                                    precio_n = db.rutaDao().obtenerPrecioN(origen, destino, empresaId).toString();
                                    precio = precio_n;
                                }
                            }
                            try {
                                return precio_n;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return "";
                        }

                        @Override
                        protected void onPostExecute(String buses) {
                            precio=buses;
                        }

                    }.execute();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            txtPrecio.setText(precio);
        }

        return true;
    }


}

// TODO: Pasar texto escrito aqui a referencias en string.xml