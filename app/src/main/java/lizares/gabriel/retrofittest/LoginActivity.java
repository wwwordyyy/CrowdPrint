package lizares.gabriel.retrofittest;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AccountAuthenticatorActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    EditText etServerAddress;
    Button btnChangeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        etServerAddress = (EditText) findViewById(R.id.etServerAddress);
        btnChangeAddress = (Button) findViewById(R.id.btnChangeAddress);

        btnChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceGenerator.changeRootURL(etServerAddress.getText().toString());
                etServerAddress.setText(ServiceGenerator.getRootURL().toString());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etUsername.getText().toString(), etPassword.getText().toString(), LoginActivity.this);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(final String username, String password, final Context context) {
        final AccountManager accountManager = AccountManager.get(context);
        CrowdPrintAPI client = ServiceGenerator.createService(CrowdPrintAPI.class);
        Call<String> call = client.loginAccount(username, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

              //  Log.d("cpdebug",response.body());
                try{
                    JSONObject loginResponse = new JSONObject(response.body());
                    if(loginResponse.getBoolean("success") == true){
                        createAccount(username,loginResponse.getString("authToken"));
                    }else{
                        Toast.makeText(context,loginResponse.getString("reason"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    Log.e("cpdebug",e.getMessage());

                }
                //createAccount(username, response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Fail: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createAccount(String username, String authToken) {
        Account account = new Account(username, "com.gab.CrowdPrint");
        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, "CROWDPRINT_USER", authToken);
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,"CROWDPRINT_USER");
        intent.putExtra(AccountManager.KEY_AUTHTOKEN,authToken);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();

    }
}
