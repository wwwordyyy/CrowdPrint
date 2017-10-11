package lizares.gabriel.retrofittest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePrintJobActivity extends AppCompatActivity {

    Button btnSelectFile, btnCreate;
    TextView tvFileName;
    Spinner spPrintStation, spPrinter;
    FileUtilities fileUtilities = new FileUtilities(CreatePrintJobActivity.this);
    UserInformation userInformation;

    CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());

    private static final int SELECT_FILE_RESULT = 50;
    private final String TAG = "cpCreateJobAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_print_job);

        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");
        Log.d(TAG,userInformation.getUsername() +" "+userInformation.getAuthToken());
        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        spPrintStation = (Spinner) findViewById(R.id.spPrintStation);
        spPrinter = (Spinner) findViewById(R.id.spPrinter);



        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJob();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILE_RESULT && resultCode == RESULT_OK) {
            String fileName = fileUtilities.setFileName(data.getData());
            fileUtilities.createTemporaryFile(data.getData(),fileName);
            tvFileName.setText(fileName);
        }
    }

    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SELECT_FILE_RESULT);
    }

    public void createJob(){
        File theFile = fileUtilities.getTempFile();

        RequestBody requestFile = RequestBody.create(MediaType.parse(theFile.toURI().toString()), theFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("printFile", theFile.getName(), requestFile);

        RequestBody theJobName = RequestBody.create(MultipartBody.FORM, theFile.getName());
        RequestBody jobOwner = RequestBody.create(MultipartBody.FORM, userInformation.getUsername());
        RequestBody printStation = RequestBody.create(MultipartBody.FORM, "Station3");
        RequestBody destPrinter = RequestBody.create(MultipartBody.FORM, "hpprinter");

        Call<String> call = client.createJobWithFile(theJobName, jobOwner, printStation, destPrinter, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "Successful: " + response);

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(CreatePrintJobActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error did not send: " + t.getMessage());
            }
        });
    }






}
