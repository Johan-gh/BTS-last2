package proyecto.prototicket.schemas.Ticket;

import android.os.AsyncTask;

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
import java.util.ArrayList;
import java.util.List;

import proyecto.prototicket.Configuracion;
import proyecto.prototicket.CrearTicket;
import proyecto.prototicket.Utils.BluetoothUtils;
import proyecto.prototicket.schemas.TicketDatabase;

/**
 * Created by edison on 23/11/17.
 */

public class TicketRepository {

    private static final String AUTH_TOKEN_URL = "https://btsbymetis.herokuapp.com/tiquetes/api_createTiquete/";
    private static final String AUTH_TOKEN_URL_CIERRE = "https://btsbymetis.herokuapp.com/tiquetes/api_createCierreCaja/";
    private static final String SUCCESS_MESSAGE =  "Successful result";
    private static final String FAILURES_MESSAGE =  "Something went wrong";
    private CrearTicket crearTicket;
    private Boolean success = false;
    public Boolean succesCierre = false;
    List<TicketDb> tdbList ;
    Configuracion configuracion;

    public static JSONObject getDataTiquete(String uuid, int ruta, String placa, float valor, String fecha_inicial,
                                            int punto_venta, String hora_salida, String hora_llegada,
                                            String fecha_viaje, String sincro, String caja_cerrada, String empresa, String empleado, String despacho){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("id_global",uuid);
            jObject.put("ruta",ruta);
            jObject.put("placa",placa);
            jObject.put("empresa",empresa); 
            jObject.put("empleado",empleado);
            jObject.put("valor",valor);
            jObject.put("fecha_inicial",fecha_inicial);
            jObject.put("punto_venta",punto_venta);
            jObject.put("hora_salida",hora_salida);
            jObject.put("hora_llegada",hora_llegada);
            jObject.put("fecha_viaje",fecha_viaje);
            jObject.put("caja_cerrada",caja_cerrada);




            return jObject;
        }catch (JSONException ex){
            ex.getStackTrace();
        }
        return null;
    }

    public static JSONObject getDataCierre(String empleado,String fecha,String hora, String numero_tiquetes,
                                           String valor_total_tiquetes){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("empleado",empleado);
            jObject.put("fecha",fecha);
            jObject.put("hora",hora);
            jObject.put("num_tiquetes",numero_tiquetes);
            jObject.put("total_tiquetes",valor_total_tiquetes);


            return jObject;
        }catch (JSONException ex){
            ex.getStackTrace();
        }
        return null;
    }

    public void restTiquete(TicketDatabase db){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                tdbList = db.ticketDao().verTiquete();
                for (TicketDb tdb:tdbList) {
                    if(tdb.getSincro().toString().equals("False")) {
                        String uuid = tdb.getUuid().toString();
                        String rutaStr = tdb.getRuta().toString();
                        String[] rutaSplit = rutaStr.split("-");
                        int ruta = Integer.parseInt(rutaSplit[0]);
                        float valor = Float.parseFloat(tdb.getValor().toString());
                        String fecha_inicial = tdb.getFecha_nicial().toString();
                        int punto_venta = Integer.parseInt(tdb.getPunto_venta());
                        String hora_salida = tdb.getHora_salida().toString();
                        String hora_llegada = tdb.getHora_llegada().toString();
                        String fecha_viaje = tdb.getFechaViaje().toString();
                        String sincro = tdb.getSincro().toString();
                        String cierre = tdb.getCierre().toString();
                        String empleado = tdb.getEmpleado().toString();
                        String empresa = tdb.getEmpresa().toString();
                        String placaBus = tdb.getPlacaBus().toString();
                        String despachado = tdb.getDespachado().toString();
                        guardarTiquete(uuid, ruta, placaBus, valor, fecha_inicial, punto_venta, hora_salida, hora_llegada, fecha_viaje, sincro, cierre, empleado, empresa, despachado, db);

                        TicketDb ticketDb = new TicketDb(uuid,String.valueOf(ruta),placaBus,String.valueOf(valor),fecha_inicial,String.valueOf(punto_venta),hora_llegada,fecha_viaje,hora_salida,"True",cierre,empleado,empresa,despachado);
                        //TicketDb ticketDb = new TicketDb(uuid,String.valueOf(ruta),String.valueOf(valor),fecha_inicial,String.valueOf(punto_venta),hora_llegada,fecha_viaje,
                                //hora_salida,"True",cierre,empleado,empresa);
                        if (success) {

                            db.ticketDao().actualizarTiquete(ticketDb);

                        }
                    }
                }
                return null;
            }
        }.execute();

    }



    public String guardarTiquete(String uuid,int ruta, String placaBus, float valor, String fecha_inicial,
                                  int punto_venta, String hora_salida, String hora_llegada,
                                  String fecha_viaje, String sincro, String cierre, String empleado, String empresa, String despachado, TicketDatabase db) { //parametros del tiquete (Int,Float,String,int,String,String,String)



        String mesaje = "";

        //ruta y punto de venta TIENE que ser el id que se asigno en django
        TicketDb ticketDb = new TicketDb(uuid,String.valueOf(ruta),placaBus ,String.valueOf(valor),fecha_inicial,String.valueOf(punto_venta)
                ,hora_salida,hora_llegada,fecha_viaje, sincro, cierre, empleado, empresa, despachado);
        JSONObject login = getDataTiquete(uuid,ruta,placaBus,valor,fecha_inicial,punto_venta,
                hora_salida,hora_llegada,fecha_viaje,sincro,cierre,empresa,empleado,"false");
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

    public String guardarRestcierreCaja(String empleado,String fecha,String hora, String numero_tiquetes,
                                  String valor_total_tiquetes){

        String mesaje = "";

        //ruta y punto de venta TIENE que ser el id que se asigno en django
        JSONObject login = getDataCierre(empleado,fecha,hora,numero_tiquetes,
                valor_total_tiquetes);
            /*JSONObject login = parser.getLoginObject(username,password);*/
        String message = login.toString();
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        try {
            URL url = new URL(AUTH_TOKEN_URL_CIERRE);
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
                this.succesCierre = true;
            }else {

                mesaje = serverResponseMessage + " " + serverResponseCode;
            }


            return contentAsString;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private String readIt(InputStream  stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
