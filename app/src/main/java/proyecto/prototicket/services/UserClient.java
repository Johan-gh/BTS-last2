package proyecto.prototicket.services;

import android.util.Log;

import proyecto.prototicket.schemas.LoginRetrofit;
import proyecto.prototicket.schemas.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by JOHAN on 31/01/2018.
 */

public interface UserClient {
        @POST("api_auth/token/")
        Call<User> token (@Body LoginRetrofit login);
}

