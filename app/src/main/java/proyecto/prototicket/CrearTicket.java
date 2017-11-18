package proyecto.prototicket;

import android.Manifest;
import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Iterator;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat;
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

            }
                    , dia, mes, anio);
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
                Log.d("edison", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("edison", result.getContents());

                ArrayList<String> cedulaData = new ArrayList<String>();

                String _data = result.getContents();
                String _temp = "";
                for (int i = 0; i < _data.length(); i++) {

                    int charData = (int) _data.charAt(i);

                    if (charData == 0 || charData == 32) {
                        if (!_temp.equals("")) {
                            cedulaData.add(_temp);
                            _temp = "";
                        }
                    } else {
                        _temp += (char) _data.charAt(i);
                    }

                    Log.d("edison", (int) _data.charAt(i) + ": " + _data.charAt(i));
                }

                if (!_temp.equals("")) {
                    cedulaData.add(_temp);
                }

                cedulaData.size();

                ArrayList<Character> letras = new ArrayList<Character>();
                ArrayList<Character> numeros = new ArrayList<Character>();

                String primerApellido = "";
                String cedula = "";
                String segundoApellido = "";
                String primerNombre = "";
                String segundoNombre = "";
                String rH = "";

                int contador = 0;
                Boolean entro = true;
                for (Iterator<String> i = cedulaData.iterator(); i.hasNext(); ) {
                    String item = i.next();
                    if (entro == false) {
                        switch (contador) {
                            case 3:
                                for (char c : item.toCharArray()) {
                                    if (Character.isDigit(c)) {
                                        numeros.add(c);
                                    } else if (Character.isLetter(c)) {
                                        letras.add(c);
                                    }
                                }

                                for (Character le : letras) {
                                    primerApellido = primerApellido + Character.toString(le);
                                }
                                String cedulaInvertida = "";
                                for (int dis = numeros.size(); dis > (numeros.size() - 10); dis--) {
                                    cedulaInvertida = cedulaInvertida + Character.toString(numeros.get(dis - 1));
                                    //cedula = cedula + Character.toString(numeros.get(dis));
                                }
                                StringBuilder builder = new StringBuilder(cedulaInvertida);
                                cedula = builder.reverse().toString();
                                break;

                            case 4:
                                segundoApellido = cedulaData.get(4);
                                break;
                            case 5:
                                primerNombre = cedulaData.get(5);
                                break;

                            case 6:
                                for (char c : item.toCharArray()) {
                                    if (Character.isDigit(c)) {
                                        segundoNombre = "";
                                        break;
                                    } else {
                                        segundoNombre = cedulaData.get(6);
                                    }
                                }
                                break;

                            case 7:
                                String grupoSanguineo = "";
                                String invertido = "";
                                if (segundoNombre.equals("")) {
                                    StringBuilder builder1 = new StringBuilder(cedulaData.get(6));
                                    grupoSanguineo = builder1.reverse().toString();

                                } else {
                                    StringBuilder builder1 = new StringBuilder(cedulaData.get(7));
                                    grupoSanguineo = builder1.reverse().toString();
                                }

                                for (char c : grupoSanguineo.toCharArray()) {
                                    if (Character.isLetter(c)) {
                                        invertido += Character.toString(c);
                                    } else if (Character.isDigit(c)) {
                                        StringBuilder builder2 = new StringBuilder(invertido);
                                        rH = builder2.reverse().toString();
                                        break;
                                    } else {
                                        invertido += Character.toString(c);
                                    }
                                }
                        }
                    } else {
                        switch (contador) {
                            case 2:
                                for (char c : item.toCharArray()) {
                                    if (Character.isDigit(c)) {
                                        numeros.add(c);
                                    } else if (Character.isLetter(c)) {
                                        letras.add(c);
                                    }
                                }

                                for (Character le : letras) {
                                    primerApellido = primerApellido + Character.toString(le);
                                }
                                if (primerApellido.equals("")) {
                                    entro = false;
                                    numeros.clear();
                                    letras.clear();
                                    break;
                                }
                                String cedulaInvertida = "";
                                for (int dis = numeros.size(); dis > (numeros.size() - 10); dis--) {
                                    cedulaInvertida = cedulaInvertida + Character.toString(numeros.get(dis - 1));
                                    //cedula = cedula + Character.toString(numeros.get(dis));
                                }
                                StringBuilder builder = new StringBuilder(cedulaInvertida);
                                cedula = builder.reverse().toString();
                                break;

                            case 3:
                                segundoApellido = cedulaData.get(3);
                                break;

                            case 4:
                                primerNombre = cedulaData.get(4);
                                break;

                            case 5:

                                for (char c : item.toCharArray()) {
                                    if (Character.isDigit(c)) {
                                        segundoNombre = "";
                                        break;
                                    } else {
                                        segundoNombre = cedulaData.get(5);
                                    }
                                }
                                break;
                            case 6:
                                String grupoSanguineo = "";
                                String invertido = "";
                                if (segundoNombre.equals("")) {
                                    StringBuilder builder1 = new StringBuilder(cedulaData.get(5));
                                    grupoSanguineo = builder1.reverse().toString();

                                } else {
                                    StringBuilder builder1 = new StringBuilder(cedulaData.get(6));
                                    grupoSanguineo = builder1.reverse().toString();
                                }

                                for (char c : grupoSanguineo.toCharArray()) {
                                    if (Character.isLetter(c)) {
                                        invertido += Character.toString(c);
                                    } else if (Character.isDigit(c)) {
                                        StringBuilder builder2 = new StringBuilder(invertido);
                                        rH = builder2.reverse().toString();
                                        break;
                                    } else {
                                        invertido += Character.toString(c);
                                    }
                                }

                        }
                    }

                    contador++;

                /*

                    posicion 6: caracter 1 se ignora + Sexo (M,F) + fecha nacimiento AAAAMMDD + 6 caracteres ignoran + tipo sangre

                    *
                    * */

                }

                if (segundoNombre.equals("")) {
                    txtPasajero.setText(primerNombre + " " + primerApellido + " " + segundoApellido);

                } else {
                    txtPasajero.setText(primerNombre + " " + segundoNombre + " " + primerApellido + " " + segundoApellido);
                }
                txtCedula.setText(cedula);
                txtRH.setText(rH);


                //Intent intent = new Intent(this, MostrarQR.class);
                //intent.putExtra("CodigoQR", result.getContents());
                //startActivity(intent);
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
}
