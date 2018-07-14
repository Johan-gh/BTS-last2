package proyecto.prototicket.services;

import proyecto.prototicket.schemas.Bus.Bus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by JOHAN on 31/01/2018.
 */

public interface BusClient {
    @GET("bus/api_buses")
    Call<Bus>bus();
}
