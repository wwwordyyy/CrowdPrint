package lizares.gabriel.retrofittest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Parcival on 8/16/2017.
 */

public class ServiceGenerator {

    private static String rootURL = "http://192.168.1.14/CrowdPrintServer/";
    // private static String rootURL = "http://49.149.239.130:8080/crowdprint/";

    private static Retrofit.Builder builder = new Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).baseUrl(rootURL);
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    public static void changeRootURL(String newRootURL) {
        rootURL = "http://" + newRootURL + "/CrowdPrintServer/";
        builder = new Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).baseUrl(rootURL);
    }

    public static String getRootURL() {
        return rootURL;
    }


    public static <S> S CreateService(Class<S> serviceClass) {
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
