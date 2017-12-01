package proyecto.prototicket;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import proyecto.prototicket.schemas.Ticket.TicketDb;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static proyecto.prototicket.Utils.ClassCompression.decode;

public class MostrarQR extends AppCompatActivity {
    private final String clave = "eKNuL3ipnlLAzfxTstV7CM4vIZybztLcZ4ItqPZ9";
    private TextView codigoQR;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_qr);

        codigoQR = (TextView) findViewById(R.id.txtQr);

        //tomo los datos del intent

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            String qr = bundle.getString("CodigoQR");
            try {
                TicketDb ticketDb = decode(qr);
                if(ticketDb.verifyTicket(clave)){
                    Toast.makeText(this,"Tiquete valido", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this,"Tiquete no valido",Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

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
            case R.id.item4:
                Intent intent3 = new Intent(this, Pre_Itinerario.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
