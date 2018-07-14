package proyecto.prototicket.schemas.Bus;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import proyecto.prototicket.schemas.Bus.Bus;
import proyecto.prototicket.schemas.TicketDatabase;

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
                String placa=jsonObject.optString("placa");
                String capacidad = jsonObject.optString("capacidad");
                String num = jsonObject.optString("numero_bus");
                String servicio = jsonObject.optString("tipo_servicio");
                exec = Executors.newSingleThreadExecutor();
                exec.execute(() -> {

                    db.busDao().crearBus(new Bus(jsonObject.optString("id"),jsonObject.optString("placa"),jsonObject.optString("capacidad"),jsonObject.optString("numero_bus"),jsonObject.optString("tipo_servicio"), jsonObject.optString("empresa")));
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