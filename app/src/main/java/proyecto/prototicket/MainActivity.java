package proyecto.prototicket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.Utils.MyJobService;
import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.Bus.BusRepository;
import proyecto.prototicket.schemas.Empleado.Empleado;
import proyecto.prototicket.schemas.Empresa.EmpresaRepository;
import proyecto.prototicket.schemas.Itinerario.ItinerarioRepository;
import proyecto.prototicket.schemas.LoginRetrofit;
import proyecto.prototicket.schemas.PuntoVenta.PuntoVentaRepository;
import proyecto.prototicket.schemas.Ruta.RutaRepository;
import proyecto.prototicket.schemas.Ticket.TicketDb;
import proyecto.prototicket.schemas.Ticket.TicketRepository;
import proyecto.prototicket.schemas.TicketDatabase;
import proyecto.prototicket.schemas.User;
import proyecto.prototicket.services.BusClient;
import proyecto.prototicket.services.ServiceBuilder;
import proyecto.prototicket.services.UserClient;
import proyecto.prototicket.threads.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final String jogTag = "my_job_tag";
    private FirebaseJobDispatcher jobDispatcher;

    ProgressDialog progress;

    public String mensaje;

    private EditText txtUser;
    private EditText txtPassword;


    //private EmpleadoRepository er;

    private static final String URL_BUS = "https://btsbymetis.herokuapp.com/bus/api_buses";
    private static final String URL_PUNTO_VENTAS = "https://btsbymetis.herokuapp.com/punto_venta/api_puntoVentas";
    private static final String URL_RUTA = "https://btsbymetis.herokuapp.com/rutas/api_rutas";
    private static final String URL_ITINERARIO = "https://btsbymetis.herokuapp.com/itinerario/api_itinerarios";
    private static final String URL_EMPRESA = "https://btsbymetis.herokuapp.com/usuarios/api_empresas";
    private Boolean succesCierre = false;
    private BusRepository br;
    private PuntoVentaRepository pvr;
    private RutaRepository rr;
    private TicketRepository ticketRepository;
    private ItinerarioRepository ir;
    private EmpresaRepository er;
    //BluetoothUtils bT;
    List<TicketDb> tdbList;

    BluetoothUtils bT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (sync()){

            Toast.makeText(MainActivity.this, "BTS está sincronizado", Toast.LENGTH_LONG).show();

        }else {
        }
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        //login retrofit
        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view,txtUser.getText().toString(),txtPassword.getText().toString());
            }
        });
        //initObject();
        //LOGIN LOCAL
       /* txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtUser.setText("bts");
        txtPassword.setText("1234");

        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtUser.getText().toString().isEmpty()||txtPassword.getText().toString().isEmpty()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Por favor ingresa todos los campos").setTitle("Atención!")                      .setPositiveButton                                           ("Aceptar",null);
                    alert.create();
                    alert.show();
                    Toast toast = Toast.makeText(MainActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    if (txtUser.getText().toString().equals("bts")&&txtPassword.getText().toString().equals("1234")){
                        Intent i = new Intent(MainActivity.this,CrearTicket.class);
                        startActivity(i);
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this).setMessage("Datos incorrectos")                      .setPositiveButton("Aceptar",null);
                        alert.create();
                        alert.show();
                        //Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });*/
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }



    private void initObject(){
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Job job = jobDispatcher.newJobBuilder().
                        setService(MyJobService.class).
                        setLifetime(Lifetime.FOREVER).
                        setRecurring(true).
                        setTag(jogTag).
                        setTrigger(Trigger.executionWindow(10,15)).
                        setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                        setReplaceCurrent(false).
                        setConstraints(Constraint.ON_ANY_NETWORK).
                        build();
                jobDispatcher.schedule(job);
                if (txtUser.getText().toString().isEmpty()||txtPassword.getText().toString().isEmpty()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Por favor ingresa todos los campos")                      .setPositiveButton                                           ("Aceptar",null);
                            alert.create();
                            alert.show();
                    //Toast.makeText(MainActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();

                }else {
                    validarIniciarSession(txtUser.getText().toString(),txtPassword.getText().toString());
                }

            }
        });
    }

    private void validarIniciarSession(String user, String password) {
        //Intent intent = new Intent(this, CrearTicket.class);
        //startActivity(intent);

        progress = ProgressDialog.show(MainActivity.this, "Espere por favor","Iniciando Sesión", true);

        Login login = new Login(this, handler, user, password);
        login.start();
    }

    @Override
    protected void onStart() {
        Context context = new CrearTicket();
        //bT = new BluetoothUtils(context);
        super.onStart();
    }

    @Override
    protected void onResume() {

        //sync();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        try {
            bT.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
            //traerem(db, (String)msg.obj);

            final Boolean existe[] = {false};
            final Boolean validar[] = {true};
            String usu = txtUser.getText().toString();
            String clav = txtPassword.getText().toString();
            final String mensaje[] = {(String) msg.obj};

            if (msg.arg1 == 1 && isNetDisponible()) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {

                        if (db.empleadoDao().verificarUsuario(usu).size() == 0 || db.empleadoDao().verificarUsuario(usu).indexOf(usu) < 0) {

                            try {
                                JSONObject datos = null;
                                datos = new JSONObject(mensaje[0]);
                                String secondaryHash = datos.getString("secondary_hash");
                                String cedula = datos.getString("cedula");
                                String empresa = datos.getString("empresa");
                                String token = datos.getString("token");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String loginInternet = simpleDateFormat.format(Calendar.getInstance().getTime()).toString();
                                db.empleadoDao().crearEmpleado(new Empleado(txtUser.getText().toString(), secondaryHash, cedula, empresa, token, loginInternet));
                                existe[0] = true;
                                String r = "";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (db.empleadoDao().verificarUsuario(usu).indexOf(usu) > 0) {
                            for (Empleado item : db.empleadoDao().verificarUsuario(usu)) {
                                String clave = item.getClave().toString();
                                String[] split = clave.split(":");
                                String salt = split[1];

                                String secondaryHash = item.getSecondary_Hash(usu, clav, salt);

                                if (secondaryHash.equals(clave)) {
                                    JSONObject datos = null;
                                    try {
                                        datos = new JSONObject(mensaje[0]);
                                        String secondary_Hash = datos.getString("secondary_hash");
                                        String cedula = datos.getString("cedula");
                                        String empresa = datos.getString("empresa");
                                        String token = datos.getString("token");
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        String loginInternet = simpleDateFormat.format(Calendar.getInstance().getTime()).toString();
                                        db.empleadoDao().actualizarEmpleado(new Empleado(txtUser.getText().toString(), secondary_Hash, cedula, empresa, token, loginInternet));
                                        existe[0] = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }else{
                            MainActivity.this.mensaje = "Usuario ó contraseña no válidos";
                            showDialog(2);
                        }
                        if (existe[0]) {
                            String y = "";
                            progress.dismiss();
                            startActivity(new Intent(MainActivity.this, CrearTicket.class));
                        }

                        return null;
                    }

                }.execute();
            }else {
                MainActivity.this.mensaje = "No fue posibe iniciar Sesión jajajajaja";
                showDialog(2);
            }
            if(!isNetDisponible() && msg.arg1 == 0){

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        if (db.empleadoDao().verificarUsuario(usu).size() != 0 || db.empleadoDao().verificarUsuario(usu).size() > 0) {

                            for (Empleado item : db.empleadoDao().verificarUsuario(usu)) {
                                String clave = item.getClave().toString();
                                String[] split = clave.split(":");
                                String salt = split[1];

                                String secondaryHash = item.getSecondary_Hash(usu, clav, salt);
                                String f = "";

                                if (secondaryHash.equals(clave)) {
                                    existe[0] = true;
                                }
                            }

                        }
                        else{
                            MainActivity.this.mensaje = "Usuario ó contraseña no válidos";
                            showDialog(2);
                        }
                        if (existe[0]) {
                            String y = "";
                            progress.dismiss();
                            startActivity(new Intent(MainActivity.this, CrearTicket.class));
                        }

                        return null;
                    }


                }.execute();

            }
            else{
                MainActivity.this.mensaje = "No fue posibe iniciar Sesión";
                showDialog(2);
            }

            if (progress.isShowing()) {
                progress.dismiss();
            }
        }

    };

    @Override
    protected Dialog onCreateDialog(final int id) {

        return new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                }).create();
    }

    public static String tokenRetrieve;

    private void login(View v, String user, String pass){
        if (isNetDisponible()) {
            LoginRetrofit login = new LoginRetrofit(user, pass);
            final View view = v;

            UserClient taskService = ServiceBuilder.buildService(UserClient.class);

            Call<User> call = taskService.token(login);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    int succesfullyLogged;

                    if (txtUser.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Por favor ingresa todos los campos").setTitle("Atención!").setPositiveButton("Aceptar", null);
                        alert.create();
                        alert.show();
                        /*Toast toast = Toast.makeText(MainActivity.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();*/
                    } else if (response.isSuccessful()) {

                        tokenRetrieve = "Token " + response.body().getToken();
                        Intent i = new Intent(MainActivity.this, CrearTicket.class);
                        startActivity(i);
                        succesfullyLogged = 1;

                        //Toast.makeText(MainActivity.this,tokenRetrieve,Toast.LENGTH_SHORT).show();

                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this).setMessage("Los datos ingresados son incorrectos").setPositiveButton("Aceptar", null);
                        alert.create();
                        alert.show();
                        succesfullyLogged = -1;
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "error1111", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this).setMessage("No hay conexion a internet").setPositiveButton("Aceptar", null);
            alert.create();
            alert.show();
        }
    }
    public boolean sync(){
        if(isNetDisponible()) {

            br = new BusRepository();
            pvr = new PuntoVentaRepository();
            rr = new RutaRepository();
            ir = new ItinerarioRepository();
            er = new EmpresaRepository();

            TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
            br.getBuses(URL_BUS, db);
            pvr.getPuntoVentas(URL_PUNTO_VENTAS, db);
            rr.getRuta(URL_RUTA, db);
            ir.getItinerario(URL_ITINERARIO, db);
            er.getEmpresa(URL_EMPRESA, db);
            return true;

        }else{
            //Toast.makeText(this,"No hay conexión a internet", Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "No hay internet, no fue posible sincronizar BTS", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

}
