package proyecto.prototicket.schemas.Empleado;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import proyecto.prototicket.schemas.TicketDatabase;

/**
 * Created by edison on 23/11/17.
 */

public class EmpleadoRepository {



    private static final String AUTH_TOKEN_URL ="https://btsbymetis.herokuapp.com/rest-auth/login/";

    private Boolean success = false;
    public static JSONObject getDataUser(String usuario,String clave){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("username",usuario);
            jObject.put("email","");
            jObject.put("password",clave);

            return jObject;
        }catch (JSONException ex){
            ex.getStackTrace();
        }
        return null;
    }

//    public void getEmpleado(String URLRest, TicketDatabase db){
//
//        Executor exec=null;
//        String sql = URLRest;
//        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        URL url = null;
//
//        HttpsURLConnection conn;
//
//
//        try{
//            url = new URL(sql);
//            conn = (HttpsURLConnection) url.openConnection();
//
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//            String json = "";
//
//            while ((inputLine = in.readLine()) != null){
//                response.append(inputLine);
//            }
//
//            json = response.toString();
//
//            JSONArray jsonArray = null;
//            jsonArray = new JSONArray(json);
//            String mensaje ="";
//            for (int i = 0; i < jsonArray.length(); i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                exec = Executors.newSingleThreadExecutor();
//                exec.execute(() -> {
//                    db.empleadoDao().crearEmpleado(new Empleado(jsonObject.optString("usuario"),jsonObject.optString("password")));
//                });
//            }
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    public String validarLogin(String usuario, String clave) { //parametros del tiquete (Int,Float,String,int,String,String,String)


        String mesaje = "";


        JSONObject login = getDataUser(usuario, clave);
            /*JSONObject login = parser.getLoginObject(username,password);*/
        String message = login.toString();
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        try {
            URL url = new URL(AUTH_TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            conn.setFixedLengthStreamingMode(message.getBytes().length);

            //header crap
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            // conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //Setup sen
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(message.getBytes());
            //
            os.flush();
            os.close();
            //connect
            conn.connect();

            // do something with response
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }
            String serverResponseMessage = conn.getResponseMessage();
            int serverResponseCode =  conn.getResponseCode();
            if(serverResponseCode == 201){
                this.success = true;
            }else {

                mesaje = serverResponseMessage + " " + serverResponseCode;
            }


            return contentAsString;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
