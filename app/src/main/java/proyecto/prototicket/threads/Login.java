package proyecto.prototicket.threads;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import proyecto.prototicket.MainActivity;

/**
 * Created by edison on 18/12/17.
 */

public class Login extends Thread {

    private Handler handler;
    private MainActivity main;
    private String usuario;
    private String clave;

    public Login(MainActivity mainActivity, Handler handlerClass, String usuario, String clave){
        this.main = mainActivity;
        this.handler = handlerClass;
        this.usuario = usuario;
        this.clave = clave;
    }

    private static final String AUTH_TOKEN_URL ="https://btsbymetis.herokuapp.com/api_auth/token/";

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


    @Override
    public void run(){
        try {
            // obtengo el mensaje y lo envio
            Message msg = handler.obtainMessage();

            String response = validarLogin();


            if(response.equals("")){
                msg.arg1 = 0;
                msg.obj = "Usuario no valido";
            }else{
                msg.arg1 = 1;
                msg.obj = response;
            }


            handler.sendMessage(msg); // envio el mensaje al hilo principal
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String validarLogin(){

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
