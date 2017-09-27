package lizares.gabriel.retrofittest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //File uploading and printjob creation
    Button selectFile, uploadFile;
    TextView txtFileName;
    EditText etDestPrinter;
    Uri fileURI;

    //When the servers ip changes
    Button setURL;
    EditText rootIP;
    TextView currentURL;

    //User info for login and validation
    AccountManager accountManager;
    AccountPreference accountPreference;
    String accountName = null;
    String authToken = null;

    //Display username and Balance
    TextView tvWelcomeUser;
    TextView tvUserBalance;
    Button btnAddBalance;

    //For displaying the users jobs
    ListView lvJobList;
    PrintJobListAdapter printJobListAdapter = null;
    int ACCOUNT_PICKER_RESULT = 50;
    int FIND_FILE_RESULT = 42;

    //FCM receiver
    BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFile = (Button) findViewById(R.id.selectFile);
        uploadFile = (Button) findViewById(R.id.uploadFile);
        txtFileName = (TextView) findViewById(R.id.txtFileName);
        etDestPrinter = (EditText) findViewById(R.id.etDestPrinter);

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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Log.d("cpdebug", intent.getStringExtra("action"));
                    Log.d("cpdebug", intent.getStringExtra("printJobName"));
                    Log.d("cpdebug", intent.getStringExtra("status"));
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
        if (accountPreference.getUsername() != null) { //if previously logged in check if the account is still in account manager
            Account selectedAccount = null;
            String username = accountPreference.getUsername();
            Account[] accounts = accountManager.getAccountsByType("com.gab.CrowdPrint");
            for (Account account : accounts) {
                if (account.name.equals(username)) {
                    selectedAccount = account;
                    accountName = selectedAccount.name;
                    authToken = accountManager.peekAuthToken(account, "CROWDPRINT_USER");
                    printJobListAdapter.setCredentials(accountName, authToken);
                    updateUserJobs();
                    getUserBalance();
                    updateUserFirebaseToken(FirebaseInstanceId.getInstance().getToken());

                    Log.d("cpdebug", authToken);

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

        tvWelcomeUser.setText(accountName);

        currentURL.setText(ServiceGenerator.getRootURL());
        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserBalance();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtFileName.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                } else {
                    createJobWithFile(etDestPrinter.getText().toString());
                }
            }
        });

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findFile();
            }
        });

        setURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceGenerator.changeRootURL(rootIP.getText().toString());
                currentURL.setText(ServiceGenerator.getRootURL());
            }
        });

    }

    private String setFileName(Uri sourceURI, Context context) {
        File temp = new File(sourceURI.getPath());
        String unparsedFileName = temp.getName();
        String parsedFileName = unparsedFileName;
        //check if file name has an extension
        if (unparsedFileName.contains(".")) {
            if (unparsedFileName.lastIndexOf(".") != unparsedFileName.length() - 1) {
                parsedFileName = unparsedFileName.substring(0, unparsedFileName.lastIndexOf("."));
            }
        }
        parsedFileName = parsedFileName.replaceAll("\\s+", "");
        //Get the file extension of the source file given the content uri
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(contentResolver.getType(sourceURI));

        return parsedFileName + "." + type;
    }


    private File createTemporaryFile(Uri sourceURI, String fileName) {


        //Create a temporary file in cache, open URI as inputstream and output to temporaryFile
        File temporaryFile = new File(getCacheDir(), fileName);
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceURI);
            OutputStream outputStream = new FileOutputStream(temporaryFile);
            try {
                try {
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    outputStream.flush();
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return temporaryFile;
    }


    private void createJobWithFile(String destinationPrinter) {

        final File theFile;
        //CrowdPrintAPI client = ServiceGenerator.createService(CrowdPrintAPI.class);
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
        String fileName = setFileName(fileURI, MainActivity.this);
        theFile = createTemporaryFile(fileURI, fileName);
        txtFileName.setText(theFile.getName());

        RequestBody requestFile = RequestBody.create(MediaType.parse(theFile.toURI().toString()), theFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("printfile", theFile.getName(), requestFile);

        RequestBody theJobName = RequestBody.create(MultipartBody.FORM, theFile.getName());
        RequestBody jobOwner = RequestBody.create(MultipartBody.FORM, accountName);
        RequestBody printStation = RequestBody.create(MultipartBody.FORM, "Station1");
        RequestBody destPrinter = RequestBody.create(MultipartBody.FORM, destinationPrinter);

        Call<String> call = client.createJobWithFile(theJobName, jobOwner, printStation, destPrinter, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                updateUserJobs();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("cpdebug", "Error did not send: " + t.getMessage());
            }
        });


    }

    private void findFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FIND_FILE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FIND_FILE_RESULT && resultCode == RESULT_OK) {
            txtFileName.setText(data.getData().getPath());
            fileURI = data.getData();
        } else if (requestCode == ACCOUNT_PICKER_RESULT && resultCode == RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            accountPreference.setUsername(accountName);
            Account account = new Account(accountName, "com.gab.CrowdPrint");
            authToken = accountManager.peekAuthToken(account, "CROWDPRINT_USER");
            printJobListAdapter.setCredentials(accountName, authToken);
            updateUserJobs();
            getUserBalance();
            Log.d("cpdebug", authToken);

        }
    }


    public void updateUserJobs() {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
        Call<String> call = client.getUserJobs(accountName);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
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

            }
        });
    }

    public void updateUserBalance() {
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
        Call<String> call = client.addLoad(accountName);
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
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
        Call<String> call = client.getLoad(accountName);
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
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
        Call<String> call = client.updateFirebaseToken(accountName, firebaseToken);
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
        tvWelcomeUser.setText(accountName);

    }
}
