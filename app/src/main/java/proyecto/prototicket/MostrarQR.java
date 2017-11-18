package proyecto.prototicket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MostrarQR extends AppCompatActivity {

    private TextView codigoQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_qr);

        codigoQR = (TextView) findViewById(R.id.txtQr);

        //tomo los datos del intent

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String qr = bundle.getString("CodigoQR");
            codigoQR.setText(qr);
        }else{
            Toast.makeText(this, "EL mensaje está vacio", Toast.LENGTH_LONG).show();
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
        switch (item.getItemId()){
            case R.id.item3:
                Intent intent = new Intent(this, Configuracion.class);
                startActivity(intent);
                break;
            case R.id.item1:
                Intent intent1 = new Intent(this, CrearTicket.class);
                startActivity(intent1);
                break;
            case R.id.item2:
                Intent intent2 = new Intent(this, VerificarTicket.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
