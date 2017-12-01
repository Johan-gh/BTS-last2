package proyecto.prototicket.schemas.Bus;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import proyecto.prototicket.CrearTicket;
import proyecto.prototicket.schemas.TicketDatabase;

/**
 * Created by User on 11/20/2017.
 */

public class BusRepository {



    public void getBuses(String URLRest, TicketDatabase db){

        Executor exec=null;
        String sql = URLRest;
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;

        HttpsURLConnection conn;


        try{
            url = new URL(sql);
            conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            String json = "";

            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }

            json = response.toString();

            JSONArray jsonArray = null;
            jsonArray = new JSONArray(json);
            String mensaje ="";
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                exec = Executors.newSingleThreadExecutor();
                exec.execute(() -> {
                    db.busDao().crearBus(new Bus(jsonObject.optString("placa"),jsonObject.optString("capacidad"),jsonObject.optString("tipo_servicio")));
                });
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
