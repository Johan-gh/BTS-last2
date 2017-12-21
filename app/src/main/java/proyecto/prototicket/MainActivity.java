package proyecto.prototicket;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.Utils.MyJobService;
import proyecto.prototicket.schemas.Empleado.Empleado;
import proyecto.prototicket.schemas.Empleado.EmpleadoRepository;
import proyecto.prototicket.schemas.TicketDatabase;
import proyecto.prototicket.threads.Login;

public class MainActivity extends AppCompatActivity {


    private static final String jogTag = "my_job_tag";
    private FirebaseJobDispatcher jobDispatcher;

    ProgressDialog progress;

    public String mensaje;

    private EditText txtUser;
    private EditText txtPassword;

    private EmpleadoRepository er;

    BluetoothUtils bT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObject();

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
                validarIniciarSession(txtUser.getText().toString(),txtPassword.getText().toString());
            }
        });
    }

    private void validarIniciarSession(String user, String password) {
        //Intent intent = new Intent(this, CrearTicket.class);
        //startActivity(intent);

        progress = ProgressDialog.show(MainActivity.this, "Espere por favor","Iniciando Session", true);

        Login login = new Login(this, handler, user, password);
        login.start();
    }

    @Override
    protected void onStart() {
        Context context = new CrearTicket();
        //bT = new BluetoothUtils(context);
        super.onStart();
    }

    /*@Override
    protected void onDestroy() {
        try {
            bT.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }*/


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
            //traerem(db, (String)msg.obj);

            final Boolean existe[] = {false};
            String usu = txtUser.getText().toString();
            String clav = txtPassword.getText().toString();
            final String mensaje[] = {(String) msg.obj};

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    if (msg.arg1 == 1) {

                        if (db.empleadoDao().verificarUsuario(usu).size() == 0 || db.empleadoDao().verificarUsuario(usu).size() < 0) {

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
                        } else if (db.empleadoDao().verificarUsuario(usu).size() > 0) {
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
                        }
                    } else {

                        if (db.empleadoDao().verificarUsuario(usu).size() > 0) {

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
                    }
                    if(existe[0]) {
                        String y = "";
                        progress.dismiss();
                        startActivity(new Intent(MainActivity.this, CrearTicket.class));
                    }

                    return null;
                }
            }.execute();

            /*if(!existe[0]){
                txtUser.setText("");
                txtPassword.setText("");
                MainActivity.this.mensaje = "No fue posibe iniciar Session";
                showDialog(2);
            }*/

            if(progress.isShowing()){
                progress.dismiss();
            }


        }

    };

    public boolean traerem(TicketDatabase db, String data){
        final boolean[] existe = {false};
        new AsyncTask<Void, Void, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected String doInBackground(Void... voids) {
                String usu = txtUser.getText().toString();
                String clav = txtPassword.getText().toString();

                if (db.empleadoDao().verificarUsuario(usu).size() > 0) {

                    for (Empleado item : db.empleadoDao().verificarUsuario(usu)) {
                        String clave = item.getClave().toString();
                        String[] split = clave.split(":");
                        String salt = split[1];

                        String secondaryHash = item.getSecondary_Hash(usu, clav, salt);
                        String f = "";

                        if (secondaryHash.equals(clave)) {
                            existe[0] = true;
                            String a = "";
                        }
                    }
                }
                else{
                    Executor exec=null;
                    try {
                        JSONObject datos = null;
                        datos = new JSONObject(data);
                        String secondaryHash = datos.getString("secondary_hash");
                        String cedula = datos.getString("cedula");
                        String empresa = datos.getString("empresa");
                        String token = datos.getString("token");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String loginInternet = simpleDateFormat.format(Calendar.getInstance().getTime()).toString();
                        exec = Executors.newSingleThreadExecutor();
                        //exec.execute(() -> {
                        db.empleadoDao().crearEmpleado(new Empleado(txtUser.getText().toString(),secondaryHash,cedula,empresa,token, loginInternet));
                        existe[0] = true;
                        String r = "";
                        //});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();

        return existe[0];
    }

    private Boolean crearEmpleado(String data){
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        Executor exec=null;
        final Boolean entro[] = {false};
        try {
                JSONObject datos = new JSONObject(data);
                String secondaryHash = datos.getString("secondary_hash");
                String cedula = datos.getString("cedula");
                String empresa = datos.getString("empresa");
                String token = datos.getString("token");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String loginInternet = simpleDateFormat.format(Calendar.getInstance().getTime()).toString();
                exec = Executors.newSingleThreadExecutor();
                exec.execute(() -> {
                    db.empleadoDao().crearEmpleado(new Empleado(txtUser.getText().toString(),secondaryHash,cedula,empresa,token, loginInternet));
                    entro[0] = true;
                });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return entro[0];
    }

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

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

}