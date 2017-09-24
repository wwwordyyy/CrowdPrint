package lizares.gabriel.retrofittest;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AccountAuthenticatorActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etUsername.getText().toString(),etPassword.getText().toString(),Login.this);
            }
        });
    }

    public void login(final String username, String password, final Context context){
        final AccountManager accountManager = AccountManager.get(context);
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class);
        Call<String> call=client.loginAccount(username,password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(context,response.body(),Toast.LENGTH_LONG).show();
                createAccount(username,response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context,"Fail: "+t.getMessage(),Toast.LENGTH_LONG).show();
                createAccount(username,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ");
            }
        });
    }
    public void createAccount(String username,String authToken){
        Account account = new Account(username,"com.gab.CrowdPrint");
        AccountManager accountManager=AccountManager.get(this);
        accountManager.addAccountExplicitly(account,null,null);
        accountManager.setAuthToken(account,"CROWDPRINT_USER",authToken);


    }
}
