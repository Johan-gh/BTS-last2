package proyecto.prototicket.schemas.Ruta;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by edison on 23/11/17.
 */

@Entity(tableName = "tabla_ruta")
public class Ruta {

    @PrimaryKey
    private String id;
    private String origen;
    private String destino;
    private String precio_N;
    private String precio_E;
    private String estado;
    private String empresaId;

    public Ruta(String id, String origen, String destino, String precio_N, String precio_E, String estado, String empresaId) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.precio_N = precio_N;
        this.precio_E = precio_E;
        this.estado = estado;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPrecio_N() {
        return precio_N;
    }

    public void setPrecio_N(String precio_N) {
        this.precio_N = precio_N;
    }

    public String getPrecio_E() {
        return precio_E;
    }

    public void setPrecio_E(String precio_E) {
        this.precio_E = precio_E;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
