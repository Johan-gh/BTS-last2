package proyecto.prototicket.schemas.Empresa;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by edison on 29/11/17.
 */


@Dao
public interface EmpresaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void crearEmpresa(Empresa empresa);

    @Query("SELECT id,nombre FROM tabla_empresa")
    public LiveData<List<Empresa>> verEmpresa();

    @Query("SELECT nombre FROM tabla_empresa WHERE id = :empresaId")
    public LiveData<List<Empresa>> verEmpresaPorId(String empresaId);
}
