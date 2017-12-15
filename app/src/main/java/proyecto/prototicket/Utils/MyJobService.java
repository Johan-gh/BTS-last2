package proyecto.prototicket.Utils;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import proyecto.prototicket.R;

import proyecto.prototicket.schemas.Ticket.TicketRepository;
import proyecto.prototicket.schemas.TicketDatabase;

/**
 * Created by edison on 15/12/17.
 */

public class MyJobService extends JobService {


    private static final String URL_BUS = "https://btsbymetis.herokuapp.com/bus/api_buses";
    private static final String URL_PUNTO_VENTAS = "https://btsbymetis.herokuapp.com/punto_venta/api_puntoVentas";
    private static final String URL_RUTA = "https://btsbymetis.herokuapp.com/rutas/api_rutas";
    private static final String URL_ITINERARIO = "https://btsbymetis.herokuapp.com/itinerario/api_itinerarios";
    private static final String URL_EMPRESA = "https://btsbymetis.herokuapp.com/usuarios/api_empresas";


    private TicketRepository ticketRepository;

    BackgroundTask backgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {



        backgroundTask = new BackgroundTask(){
            @Override
            protected void onPostExecute(String s) {

                if(isNetDisponible()) {

                    TicketDatabase db = Room.databaseBuilder(getApplicationContext(), TicketDatabase.class, getString(R.string.DB_NAME)).build();
                    ticketRepository = new TicketRepository();
                    ticketRepository.restTiquete(db);
                    Toast.makeText(getApplicationContext(), "Sincronizando tiquetes " + s, Toast.LENGTH_LONG).show();
                }
                jobFinished(job,false);
            }
        };
        backgroundTask.execute();
        //devuelve falso si el trabajo está realizado de lo contrario devuelve verdades
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        //¿Debería volver a intentarse este trabajo?
        return true;
    }

    public static class BackgroundTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            return "job";
        }
    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }


}
