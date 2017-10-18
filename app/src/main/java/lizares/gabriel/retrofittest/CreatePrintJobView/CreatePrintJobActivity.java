package lizares.gabriel.retrofittest.CreatePrintJobView;

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

public class CreatePrintJobActivity extends AppCompatActivity {

    Button btnSelectFile, btnSelectStation;
    TextView tvFileName;
    FileUtilities fileUtilities = new FileUtilities(CreatePrintJobActivity.this);
    UserInformation userInformation;
    CrowdPrintAPI client;

    PrintJobSettings printJobSettings;


    private static final int SELECT_FILE_RESULT = 50;
    private static final int SELECT_PRINTER_RESULT = 51;
    private final String TAG = "cpCreateJobAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_print_job);

        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");
        client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());

        Log.d(TAG, userInformation.getUsername() + " " + userInformation.getAuthToken());
        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSelectStation = (Button) findViewById(R.id.btnSelectStation);
        tvFileName = (TextView) findViewById(R.id.tvFileName);

        printJobSettings = new PrintJobSettings();


        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
            }
        });

        btnSelectStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(printJobSettings.getJobFile()!=null) {
                    Intent intent = new Intent(CreatePrintJobActivity.this, SelectStationActivity.class);
                    intent.putExtra("userInformation", userInformation);
                    intent.putExtra("printJobSettings", printJobSettings);
                    startActivityForResult(intent, SELECT_PRINTER_RESULT);
                }else{
                    Toast.makeText(CreatePrintJobActivity.this,"Please select a file",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILE_RESULT && resultCode == RESULT_OK) {
            String fileName = fileUtilities.setFileName(data.getData());
            printJobSettings.setJobFile(fileUtilities.createTemporaryFile(data.getData(), fileName));
            tvFileName.setText(fileName);
        }
    }

    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SELECT_FILE_RESULT);
    }





}
