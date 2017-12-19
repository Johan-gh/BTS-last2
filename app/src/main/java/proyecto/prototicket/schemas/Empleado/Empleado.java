package proyecto.prototicket.schemas.Empleado;

/**
 * Created by edison on 23/11/17.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity(tableName = "tabla_empleado")
public class Empleado {

    @PrimaryKey
    private String usuario;
    private String clave;
    private String cedula;
    private String nombreEmpresa;
    private String token;
    private String loginInternet;

    public Empleado(String usuario, String clave, String cedula, String nombreEmpresa, String token, String loginInternet) {
        this.usuario = usuario;
        this.clave = clave;
        this.cedula = cedula;
        this.nombreEmpresa = nombreEmpresa;
        this.token = token;
        this.loginInternet = loginInternet;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public String getSecondary_Hash(String usuario, String clave, String salt){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        md.update((usuario + clave + salt).getBytes());
        return byteArrayToHex(md.digest()) + ":" + salt;
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginInternet() {
        return loginInternet;
    }

    public void setLoginInternet(String loginInternet) {
        this.loginInternet = loginInternet;
    }
}
