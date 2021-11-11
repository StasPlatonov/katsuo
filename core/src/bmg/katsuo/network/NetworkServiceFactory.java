package bmg.katsuo.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceFactory
{
    public static final int CONNECT_TIMEOUT = 15;
    public static final int WRITE_TIMEOUT = 60;
    public static final int READ_TIMEOUT = 30;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
                                                   .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                                                   .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                                                   .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).build();
    //----------------------------------------------------------------------------------------------

    public static NetworkService getNetworkService(String url)
    {
        return getRetrofit(url).create(NetworkService.class);
    }
    //----------------------------------------------------------------------------------------------

    private static final String DateTimeFormat = "dd-MM-yyyy HH:mm:ss.SSS";

    private static Retrofit getRetrofit(String url)
    {
        Gson gson = new GsonBuilder().setDateFormat(DateTimeFormat).create();
        return new Retrofit.Builder()
                .baseUrl(url.endsWith("/") ? url : url + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }
    //----------------------------------------------------------------------------------------------

    public static final String DefaultServers = "127.0.0.1:80, 10.82.246.146:8080, www.oneclickfun.com:8090";
    //----------------------------------------------------------------------------------------------
}