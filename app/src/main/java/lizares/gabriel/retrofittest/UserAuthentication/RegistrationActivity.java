package lizares.gabriel.retrofittest.UserAuthentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    EditText etFirstName;
    EditText etLastName;
    Button btnSubmit;
    EditText etServerAddress;
    Button btnChangeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        etServerAddress = (EditText) findViewById(R.id.etServerAddress);
        btnChangeAddress = (Button) findViewById(R.id.btnChangeAddress);

        btnChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceGenerator.changeRootURL(etServerAddress.getText().toString());
                etServerAddress.setText(ServiceGenerator.getRootURL().toString());
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrowdPrintAPI client = ServiceGenerator.createService(CrowdPrintAPI.class);
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();

                Call<String> call = client.registerAccount(username, password, firstName, lastName);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(RegistrationActivity.this, "Succesful: " + response.body(), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(RegistrationActivity.this, "Fail: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
