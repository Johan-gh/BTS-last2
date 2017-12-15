package proyecto.prototicket;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

import java.io.IOException;

import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.Utils.MyJobService;
import proyecto.prototicket.schemas.Empleado.Empleado;
import proyecto.prototicket.schemas.Empleado.EmpleadoRepository;
import proyecto.prototicket.schemas.TicketDatabase;

public class MainActivity extends AppCompatActivity {


    private static final String jogTag = "my_job_tag";
    private FirebaseJobDispatcher jobDispatcher;

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
        Intent intent = new Intent(this, CrearTicket.class);
        startActivity(intent);
        //EmpleadoRepository empleadoRepository = new EmpleadoRepository();
        //empleadoRepository.validarLogin(user,password);
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
}