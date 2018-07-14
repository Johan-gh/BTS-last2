package proyecto.prototicket.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JOHAN on 31/01/2018.
 */
public class ServiceBuilder {
    private static final String URL="https://btsbymetis.herokuapp.com";

    //Create logger https://btsbymetis.herokuapp.com/api_auth/token/

    private static HttpLoggingInterceptor logger=
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    //Create OKHTTP Client

    private static OkHttpClient.Builder okHTTP=
            new OkHttpClient.Builder().addInterceptor(logger);

    private static Retrofit.Builder builder=new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(okHTTP.build());

    private static Retrofit retrofit= builder.build();

    public static <S> S buildService(Class<S> serviceType){
        return retrofit.create(serviceType);
    }
}