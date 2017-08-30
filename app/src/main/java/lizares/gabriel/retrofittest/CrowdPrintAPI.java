package lizares.gabriel.retrofittest;


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
    Call<ResponseBody> createJobWithFile(
            @Part("jobname") RequestBody jobname,
            @Part MultipartBody.Part file
    );
    @FormUrlEncoded
    @POST("addprintjob.php")
    Call<ResponseBody> createJob(
            @Field("jobname") String jobname
    );


}
