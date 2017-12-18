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
import java.util.ArrayList;
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

            if (msg.arg1 == 1) {
                if(crearEmpleado((String)msg.obj)){
                    traerem(Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build());
                    progress.dismiss();
                    startActivity(new Intent(MainActivity.this, CrearTicket.class));
                }else{
                    MainActivity.this.mensaje = "No fue posibe iniciar Session";
                    showDialog(2);
                }

            } else {
                MainActivity.this.mensaje = (String) msg.obj;
                showDialog(msg.arg1);

            }
            if(progress.isShowing()){
                progress.dismiss();
            }

        }
    };

    public void traerem(TicketDatabase db){
        new AsyncTask<Void, Void, Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Boolean doInBackground(Void... voids) {
                String usu = txtUser.getText().toString();

                for (Empleado item : db.empleadoDao().verificarUsuario(usu)) {
                    String usuario = item.getUsuario().toString();
                    String clave = item.getClave().toString();
                    String t = "";
                }

                return true;
            }
        }.execute();


    }

    private Boolean crearEmpleado(String data){
        TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
        Executor exec=null;
        final boolean[] entro = {false};
        try {
            String json = data.toString();
            JSONArray jsonArray = null;
            //jsonArray = new JSONArray(json);


                exec = Executors.newSingleThreadExecutor();
                exec.execute(() -> {
                    db.empleadoDao().crearEmpleado(new Empleado(txtUser.getText().toString(),"secondary_hash","cedula","empresa","token"));
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

}