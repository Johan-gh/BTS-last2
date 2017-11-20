package proyecto.prototicket;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.models.Ticket;

public class CrearTicket extends AppCompatActivity implements View.OnClickListener {

    private EditText txtFecha, txtHora, txtDateBuy, txtCedula, txtPasajero, txtRH, txtTravelRoute, txtPosting, txtVehicle;

    private ImageButton btnCodigoCedula;
    private Button btnSave;

    private int dia, mes, anio, hora, minutos;

    private static final int CAMERA_CODE = 1888;
    BluetoothUtils bT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ticket);

        txtDateBuy = (EditText) findViewById(R.id.txtDateBuy);
        txtTravelRoute = (EditText) findViewById(R.id.txtTravelRoute);
        txtFecha = (EditText) findViewById(R.id.txtDate);
        txtHora = (EditText) findViewById(R.id.txtTime);
        txtPasajero = (EditText) findViewById(R.id.txtTraveler);
        txtCedula = (EditText) findViewById(R.id.txtCedula);
        txtRH = (EditText) findViewById(R.id.txtRH);
        txtPosting = (EditText) findViewById(R.id.txtPosting);
        txtVehicle = (EditText) findViewById(R.id.txtVehicle);


        btnCodigoCedula = (ImageButton) findViewById(R.id.btnCodigoCedula);

        txtDateBuy.setText(setDate().format(Calendar.getInstance().getTime()));
        txtDateBuy.setEnabled(false);

        btnCodigoCedula.setOnClickListener(this);
        txtFecha.setOnClickListener(this);
        txtHora.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    saveData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            bT = new BluetoothUtils(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SimpleDateFormat setDate() {
        return new SimpleDateFormat("dd/MM/yyyy");
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
                    txtFecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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
                cleaned_reading = reading.replaceAll("(00(?=\\d+\\p{L}{2,}(\\s+|\0+)))|((\0+|\\s+)0(?=(M|F)\\d{8}))|\\s+|(\0+PubDSK_1\0+)|\0+|((?<=\\d)(?=\\p{L}{2,}))|((?<=M|F)(?=\\d{8}))|((?<=(M|F)\\d{8})(?=\\d+(O|A|B|(AB))(\\+|-)))|(?=(O|A|B|(AB))(\\+|-))", " || ");

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


    private void saveData() {
        Ticket ticket = new Ticket();

        //private EditText txtFecha, txtHora, txtDateBuy, txtCedula, txtPasajero, txtRH;
        ticket.setBuyDate(txtDateBuy.getText().toString());
        ticket.setTravelDate(txtFecha.getText().toString());
        ticket.setTravelRoute(txtTravelRoute.getText().toString());
        ticket.setDestiny(txtPosting.getText().toString());
        ticket.setVehicle(txtVehicle.getText().toString());
        ticket.setName(txtPasajero.getText().toString());
        ticket.setSchedule(txtCedula.getText().toString());
        ticket.setRh(txtRH.getText().toString());
        ticket.setTravelHour(txtHora.getText().toString());


        try {

            bT.sendData(ticket.getDataPrint());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            bT.closeBT();
        }catch(Exception e){

        }
    }

    private void fastToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}

// TODO: Pasar texto escrito aqui a referencias en string.xml