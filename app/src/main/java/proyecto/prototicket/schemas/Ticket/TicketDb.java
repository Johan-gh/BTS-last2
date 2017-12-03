package proyecto.prototicket.schemas.Ticket;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by edison on 24/11/2017.
 */
@Entity(tableName = "tabla_tiquete")
public class TicketDb {

    @PrimaryKey
    private String uuid;
    private String ruta;//int (id)
    private String valor;//se saca de la ruta (int)
    private String fecha_nicial;//fecha de compra "2017-12-17"
    private String punto_venta;//int (id)
    private String hora_llegada;//quemarla
    private String fechaViaje;
    private String hora_salida;//hora viaje "12:12:00"
    private String DS;
    private String sincro;
    private String cierre;
    private String empleado;
    private String empresa;



    public TicketDb(String uuid, String ruta, String valor, String fecha_nicial, String punto_venta, String hora_llegada, String fechaViaje, String hora_salida, String sincro, String cierre, String empleado, String empresa) {
        this.uuid = uuid;
        this.ruta = ruta;
        this.valor = valor;
        this.fecha_nicial = fecha_nicial;
        this.punto_venta = punto_venta;
        this.hora_llegada = "00:00";
        this.fechaViaje = fechaViaje;
        this.hora_salida = hora_salida;
        this.DS = null;
        this.sincro = sincro;
        this.cierre = cierre;
        this.empleado = empleado;
        this.empresa = empresa;
    }

    @Ignore
    public TicketDb(String uuid, String ruta, String valor, String fecha_nicial, String punto_venta, String hora_llegada, String fechaViaje, String hora_salida,String DS) {
        this.uuid = uuid;
        this.ruta = ruta;
        this.valor = valor;
        this.fecha_nicial = fecha_nicial;
        this.punto_venta = punto_venta;
        this.hora_llegada = hora_llegada;
        this.fechaViaje = fechaViaje;
        this.hora_salida = hora_salida;
        this.DS = DS;


    }


    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String usuario) {
        this.empleado = usuario;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getSincro() {
        return sincro;
    }

    public void setSincro(String sincro) {
        this.sincro = sincro;
    }

    public String getCierre() {
        return cierre;
    }

    public void setCierre(String cierre) {
        this.cierre = cierre;
    }

    public String getUuid() {return uuid;}

    public void setUuid(String uuid) {this.uuid = uuid;}

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFecha_nicial() {
        return fecha_nicial;
    }

    public void setFecha_nicial(String fecha_nicial) {
        this.fecha_nicial = fecha_nicial;
    }

    public String getPunto_venta() {
        return punto_venta;
    }

    public void setPunto_venta(String punto_venta) {
        this.punto_venta = punto_venta;
    }

    public String getHora_llegada() {
        return hora_llegada;
    }

    public void setHora_llegada(String hora_llegada) {
        this.hora_llegada = hora_llegada;
    }

    public String getFechaViaje() {
        return fechaViaje;
    }

    public void setFechaViaje(String fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public String getHora_salida() {
        return hora_salida;
    }

    public void setHora_salida(String hora_salida) {
        this.hora_salida = hora_salida;
    }

    public String getDS() {
        return DS;
    }

    public void setDS(String DS) {
        this.DS = DS;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    public void signTicket(String clave) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String protected_data =
                this.uuid +
                        this.valor +
                        this.fecha_nicial +
                        this.punto_venta +
                        this.fechaViaje+
                        this.hora_salida +
                        this.ruta + clave;
        String a = Base64.encodeToString(
                digest.digest((
                        protected_data)
                        .getBytes((StandardCharsets.UTF_8))
                ), Base64.NO_WRAP);
        this.setDS(a);
        String p="";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean verifyTicket(String clave) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String protected_data =
                this.uuid +
                        this.valor +
                        this.fecha_nicial +
                        this.punto_venta +
                        this.fechaViaje+
                        this.hora_salida +
                        this.ruta + clave;

        String readedDS = Base64.encodeToString(
                digest.digest((
                        protected_data)
                        .getBytes((StandardCharsets.UTF_8))
        ), Base64.NO_WRAP);

        return this.DS.compareTo(readedDS) == 0;
    }
}
