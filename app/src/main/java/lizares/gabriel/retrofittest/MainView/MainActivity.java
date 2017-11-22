package lizares.gabriel.retrofittest.MainView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.CreatePrintJobView.CreatePrintJobActivity;
import lizares.gabriel.retrofittest.Firebase.CrowdPrintFCMService;
import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.AccountPreference;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    //When the servers ip changes
    Button setURL;
    EditText rootIP;
    TextView currentURL;
    Boolean connectedToServer = false;
    //User info for login and validation
    AccountManager accountManager;
    AccountPreference accountPreference;
    UserInformation userInformation = new UserInformation();

    //Display username and Balance
    TextView tvWelcomeUser;
    TextView tvUserBalance;
    Button btnAddBalance;

    //For displaying the users jobs
    ListView lvJobList;
    PrintJobListAdapter printJobListAdapter = null;
    int ACCOUNT_PICKER_RESULT = 50;
    private static final int CREATE_JOB_ACTIVITY_RESULT = 51;

    //FCM receiver
    BroadcastReceiver receiver;
    Button btnCreatePrintJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setURL = (Button) findViewById(R.id.setURL);
        rootIP = (EditText) findViewById(R.id.rootIP);
        currentURL = (TextView) findViewById(R.id.currentURL);

        tvWelcomeUser = (TextView) findViewById(R.id.tvWelcomeUser);
        tvUserBalance = (TextView) findViewById(R.id.tvUserBalance);
        btnAddBalance = (Button) findViewById(R.id.btnAddBalance);

        ArrayList<PrintJobInfo> printJobArray = new ArrayList<>();
        printJobListAdapter = new PrintJobListAdapter(this, printJobArray);
        lvJobList = (ListView) findViewById(R.id.lvJobList);
        lvJobList.setAdapter(printJobListAdapter);
        accountPreference = new AccountPreference(this);
        accountManager = AccountManager.get(this);

        btnCreatePrintJob = (Button) findViewById(R.id.btnCreatePrintJob);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String notifAction = intent.getStringExtra("action");
                    String notifStatus = intent.getStringExtra("status");
                    String notifJobName = intent.getStringExtra("printJobName");
                    updateUserJobs();
                    if (notifAction.equals(CrowdPrintFCMService.FCM_PRINTSTATUSUPDATE)) {
                        if (notifStatus.equals("canceled")) {
                            Toast.makeText(context, notifJobName + " has been canceled", Toast.LENGTH_LONG).show();
                        } else if (notifStatus.equals("completed")) {
                            Toast.makeText(context, notifJobName + " has finished printing", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        //Check if user has logged in
        authenticateUser();

        tvWelcomeUser.setText(userInformation.getUsername());

        currentURL.setText(ServiceGenerator.getRootURL());
        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserBalance();
            }
        });

        setURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceGenerator.changeRootURL(rootIP.getText().toString());
                currentURL.setText(ServiceGenerator.getRootURL());

                updateUserJobs();
                if (connectedToServer) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreatePrintJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectedToServer) {
                    Intent intent = new Intent(MainActivity.this, CreatePrintJobActivity.class);
                    Log.d(TAG, "User information: " + userInformation.getUsername() + " " + userInformation.getAuthToken());
                    intent.putExtra("userInformation", userInformation);
                    startActivityForResult(intent, CREATE_JOB_ACTIVITY_RESULT);
                } else {
                    Toast.makeText(MainActivity.this, "Cannot connect to server. Trying to reconnect.", Toast.LENGTH_SHORT).show();
                    //check if connected again
                    updateUserJobs();
                    if (connectedToServer) {
                        Toast.makeText(MainActivity.this, "Connection Established", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACCOUNT_PICKER_RESULT && resultCode == RESULT_OK) {
            userInformation.setUsername(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            accountPreference.setUsername(userInformation.getUsername());
            Account account = new Account(userInformation.getUsername(), "com.gab.CrowdPrint");
            userInformation.setAuthToken(accountManager.peekAuthToken(account, "CROWDPRINT_USER"));
            printJobListAdapter.setCredentials(userInformation.getUsername(), userInformation.getAuthToken());
            updateUserJobs();
            getUserBalance();
            updateUserFirebaseToken(FirebaseInstanceId.getInstance().getToken());
            Log.d("cpdebug", userInformation.getAuthToken());

        } else if (requestCode == CREATE_JOB_ACTIVITY_RESULT && resultCode == RESULT_OK) {
            updateUserJobs();
            Toast.makeText(MainActivity.this, "Job Created Successfully", Toast.LENGTH_SHORT).show();
        }
    }


    public void updateUserJobs() {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
        Call<String> call = client.getUserJobs(userInformation.getUsername());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                connectedToServer = true;
                try {
                    printJobListAdapter.clear();
                    JSONArray arrayJobInfo = new JSONArray(response.body());
                    for (int index = 0; index < arrayJobInfo.length(); index++) {
                        JSONObject jobInfo = arrayJobInfo.getJSONObject(index);
                        printJobListAdapter.add(new PrintJobInfo(jobInfo.getString("pdfJobName"),
                                jobInfo.getString("jobId"), jobInfo.getString("price"), jobInfo.getString("status")));
                        printJobListAdapter.notifyDataSetChanged();
                        Log.d("ARRAYJSON", arrayJobInfo.getJSONObject(index).toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.toString());
                String errorMsg = t.getMessage();
                if (errorMsg.contains("Failed to connect to")) {
                    Toast.makeText(MainActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                    connectedToServer = false;
                }
            }
        });
    }

    public void updateUserBalance() {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
        Call<String> call = client.addLoad(userInformation.getUsername());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tvUserBalance.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void getUserBalance() {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
        Call<String> call = client.getLoad(userInformation.getUsername());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tvUserBalance.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });

    }

    public void updateUserFirebaseToken(String firebaseToken) {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
        Call<String> call = client.updateFirebaseToken(userInformation.getUsername(), firebaseToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("cpdebug", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("cpdebug", t.getMessage());
            }

        });
    }

    public void authenticateUser() {
        if (accountPreference.getUsername() != null) { //if previously logged in check if the account is still in account manager
            Account selectedAccount = null;
            String username = accountPreference.getUsername();
            Account[] accounts = accountManager.getAccountsByType("com.gab.CrowdPrint");
            for (Account account : accounts) {
                if (account.name.equals(username)) {
                    selectedAccount = account;
                    userInformation.setUsername(selectedAccount.name);
                    userInformation.setAuthToken(accountManager.peekAuthToken(account, "CROWDPRINT_USER"));
                    printJobListAdapter.setCredentials(userInformation.getUsername(), userInformation.getAuthToken());
                    updateUserJobs();
                    getUserBalance();
                    updateUserFirebaseToken(FirebaseInstanceId.getInstance().getToken());

                    Log.d("cpdebug", userInformation.getAuthToken());
                    break;
                }
            }
            if (selectedAccount == null) { //if not in the account manager have user add an account
                Intent accountPicker = accountManager.newChooseAccountIntent(null, null, new String[]{"com.gab.CrowdPrint"}, null, null, null, null);
                startActivityForResult(accountPicker, ACCOUNT_PICKER_RESULT);
            }
        } else { //If no account was logged in before have user add an account
            Intent accountPicker = accountManager.newChooseAccountIntent(null, null, new String[]{"com.gab.CrowdPrint"}, null, null, null, null);
            startActivityForResult(accountPicker, ACCOUNT_PICKER_RESULT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver((receiver),
                new IntentFilter(CrowdPrintFCMService.FCM_NOTIFICATION)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserJobs();
        getUserBalance();
        tvWelcomeUser.setText(userInformation.getUsername());

    }
}
