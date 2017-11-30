package lizares.gabriel.retrofittest.Retrofit;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Parcival on 8/16/2017.
 */

public interface CrowdPrintAPI {
    @Multipart
    @POST("addprintjob.php")
    Call<String> createJobWithFile(
            @Part("jobname") RequestBody jobname,
            @Part("jobOwner") RequestBody jobOwner,
            @Part("printStation") RequestBody printStation,
            @Part("destPrinter") RequestBody destPrinter,
            @Part("pageDimensionX") RequestBody pageDimensionX,
            @Part("pageDimensionY") RequestBody pageDimensionY,
            @Part("inkType") RequestBody inkType,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<String> registerAccount(
            @Field("username") String username,
            @Field("password") String password,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<String> loginAccount(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("getUserJobs.php")
    Call<String> getUserJobs(
            @Field("jobOwner") String jobOwner
    );

    @FormUrlEncoded
    @POST("addload.php")
    Call<String> addLoad(
            @Field("jobOwner") String username
    );

    @FormUrlEncoded
    @POST("getload.php")
    Call<String> getLoad(
            @Field("jobOwner") String username
    );

    @FormUrlEncoded
    @POST("updatefirebasetoken.php")
    Call<String> updateFirebaseToken(
            @Field("jobOwner") String username,
            @Field("firebaseToken") String firebaseToken
    );

    @FormUrlEncoded
    @POST("startprint.php")
    Call<String> startPrint(
            @Field("jobOwner") String username,
            @Field("jobKey") String jobKey

    );

    @FormUrlEncoded
    @POST("getstationlist.php")
    Call<String> getStationList(
            @Field("jobOwner") String username
    );

    @FormUrlEncoded
    @POST("getprinterlist.php")
    Call<String> getStationPrinterList(
            @Field("jobOwner") String username,
            @Field("stationOwner") String stationOwner,
            @Field("stationName") String stationName
    );

    @FormUrlEncoded
    @POST("getPrinterSettings.php")
    Call<String> getPrinterSettings(
            @Field("jobOwner") String username,
            @Field("stationName") String stationName,
            @Field("printerName") String printerName
    );

    @FormUrlEncoded
    @POST("getjob.php")
    Call<String> updateJobStatus(
      @Field("jobOwner") String username,
      @Field("jobId") String jobKey,
      @Field("statusUpdate") String  status
    );

}
