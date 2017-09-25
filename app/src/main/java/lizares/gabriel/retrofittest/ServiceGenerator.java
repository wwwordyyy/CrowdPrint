package lizares.gabriel.retrofittest;

import android.text.TextUtils;

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


    public static <S> S createService(Class<S> serviceClass) {
      //  retrofit = builder.build();
      //  return retrofit.create(serviceClass);
        return CreateService(serviceClass,null);
    }

    public static <S> S CreateService(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                builder.client(httpClient.build());
                //retrofit = builder.build();
            }
        }
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
