package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import lizares.gabriel.retrofittest.MainView.MainActivity;
import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinalizeJobActivity extends AppCompatActivity {
    private static final String TAG = "FinalizeJobActivity";
    TextView tvStationName,tvPrinterName,tvFileName;
    Button btnCreateJob;
    PrintJobSettings printJobSettings;
    UserInformation userInformation;
    CrowdPrintAPI client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_job);
        printJobSettings = (PrintJobSettings) getIntent().getSerializableExtra("printJobSettings");
        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");

        client = ServiceGenerator.CreateService(CrowdPrintAPI.class,userInformation.getAuthToken());

        tvStationName = (TextView) findViewById(R.id.tvStationName);
        tvPrinterName =(TextView) findViewById(R.id.tvPrinterName);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        btnCreateJob = (Button) findViewById(R.id.btnCreateJob);

        String fileName = printJobSettings.getJobFile().getName();
        String stationName = printJobSettings.getStationName();
        String printerName = printJobSettings.getPrinterName();

        tvFileName.setText(fileName);
        tvStationName.setText(stationName);
        tvPrinterName.setText(printerName);

        btnCreateJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJob(printJobSettings.getJobFile(),printJobSettings.getStationName(),printJobSettings.getPrinterName());
            }
        });
    }

    public void createJob(File theFile, String stationName, String printerName) {

        RequestBody requestFile = RequestBody.create(MediaType.parse(theFile.toURI().toString()), theFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("printFile", theFile.getName(), requestFile);

        RequestBody theJobName = RequestBody.create(MultipartBody.FORM, theFile.getName());
        RequestBody jobOwner = RequestBody.create(MultipartBody.FORM, userInformation.getUsername());
        RequestBody printStation = RequestBody.create(MultipartBody.FORM, stationName);
        RequestBody destPrinter = RequestBody.create(MultipartBody.FORM, printerName);

        Call<String> call = client.createJobWithFile(theJobName, jobOwner, printStation, destPrinter, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "Successful: " + response);
                Toast.makeText(FinalizeJobActivity.this, "Print Job Created", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                startActivity(new Intent(FinalizeJobActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(FinalizeJobActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error did not send: " + t.getMessage());
            }
        });
    }
}
