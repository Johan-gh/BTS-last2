package proyecto.prototicket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtUser;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObject();
    }

    private void initObject(){
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarIniciarSession();
            }
        });
    }

    private void validarIniciarSession(){
        String user = txtUser.getText().toString();
        String password = txtPassword.getText().toString();
        if (user == null || user.equalsIgnoreCase("")){
            Toast.makeText(this, "Debe ingresar un usuario", Toast.LENGTH_LONG).show();
        }else if (password == null || password.equalsIgnoreCase("")){
            Toast.makeText(this, "Debe ingresar una clave", Toast.LENGTH_LONG).show();
        }else if(password.equals("1234") && user.equals("edison")){
            Intent intent = new Intent(this, CrearTicket.class);
            startActivity(intent);
            Toast.makeText(this, "inicio sesión", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "El usuario y la contraseña no son validos", Toast.LENGTH_LONG).show();
            txtUser.setText("");
            txtPassword.setText("");
        }

    }
}