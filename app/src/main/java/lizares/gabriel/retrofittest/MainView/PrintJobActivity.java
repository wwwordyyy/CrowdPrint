package lizares.gabriel.retrofittest.MainView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintJobActivity extends AppCompatActivity {
    private static final String TAG = "printjobactivity";
    TextView tvJobName, tvJobKey, tvPrice, tvStatus, tvPageSize, tvInkType;
    Button btnPrint, btnCancel;
    UserInformation userInformation;
    PrintJobInfo printJobInfo;
    CrowdPrintAPI client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_job);

        tvJobName = (TextView) findViewById(R.id.tvJobName);
        tvJobKey = (TextView) findViewById(R.id.tvJobKey);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvPageSize = (TextView) findViewById(R.id.tvPageSize);
        tvInkType = (TextView) findViewById(R.id.tvInkType);
        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");
        printJobInfo = (PrintJobInfo) getIntent().getSerializableExtra("printJobInfo");

        tvJobName.setText(printJobInfo.getJobName());
        tvJobKey.setText(printJobInfo.getJobKey());
        tvPrice.setText(printJobInfo.getJobPrice());
        tvStatus.setText(printJobInfo.getJobStatus());
        String pageSize = printJobInfo.getPageDimensionX() / 72 + "in by " + printJobInfo.getPageDimensionY() / 72 + "in";
        tvPageSize.setText(pageSize);
        tvInkType.setText(printJobInfo.getInkType());

        client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobKey = tvJobKey.getText().toString();
                String username = userInformation.getUsername();
                //Toast.makeText(context,jobKey,Toast.LENGTH_LONG).show();
                Call<String> call = client.startPrint(username, jobKey);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(PrintJobActivity.this, response.body(), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(PrintJobActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<String> call = client.updateJobStatus(userInformation.getUsername(), printJobInfo.getJobKey(), "canceled");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, response.body());
                        Toast.makeText(PrintJobActivity.this, "Job Canceled", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(PrintJobActivity.this, "Could not cancel job", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
