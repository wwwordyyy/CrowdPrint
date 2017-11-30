package lizares.gabriel.retrofittest.Retrofit;

import android.text.TextUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

import lizares.gabriel.retrofittest.UserAuthentication.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Parcival on 8/16/2017.
 */

public class ServiceGenerator {

    private static String rootURL = "https://192.168.1.17/CrowdPrintServer/";
    // private static String rootURL = "http://49.149.239.130:8080/crowdprint/";
    private static OkHttpClient okHttpClient = unsafeClient("");
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(rootURL).addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    public static void changeRootURL(String newRootURL) {
        rootURL = "https://" + newRootURL + "/CrowdPrintServer/";
        builder = new Retrofit.Builder().baseUrl(rootURL).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static String getRootURL() {
        return rootURL;
    }


    public static <S> S createService(Class<S> serviceClass) {
      //  retrofit = builder.build();
      //  return retrofit.create(serviceClass);
        return CreateService(serviceClass,null);
    }

    public static <S> S CreateService(Class<S> serviceClass, String authToken) {
       // if (!TextUtils.isEmpty(authToken)) {
        if(authToken == null){
            authToken="";
        }
            OkHttpClient hClient = unsafeClient(authToken);
            builder.client(hClient);
            /*
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                builder.client(httpClient.build());
                //retrofit = builder.build();
            }
            */
      //  }
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient unsafeClient(String authToken){
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            httpClient = new OkHttpClient.Builder();
            if(!authToken.isEmpty()){
                AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
                if (!httpClient.interceptors().contains(interceptor)) {
                    httpClient.addInterceptor(interceptor);
                    builder.client(httpClient.build());
                    //retrofit = builder.build();
                }
            }

            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = httpClient.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
