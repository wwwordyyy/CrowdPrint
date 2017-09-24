package lizares.gabriel.retrofittest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button selectFile, uploadFile;
    TextView txtFileName;

    Button setURL;
    EditText rootIP;
    TextView currentURL;

    Uri fileURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFile = (Button) findViewById(R.id.selectFile);
        uploadFile = (Button) findViewById(R.id.uploadFile);
        txtFileName = (TextView) findViewById(R.id.txtFileName);
        rootIP = (EditText) findViewById(R.id.rootIP);
        currentURL = (TextView) findViewById(R.id.currentURL);
        setURL = (Button) findViewById(R.id.setURL);

        currentURL.setText(ServiceGenerator.getRootURL().toString());

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtFileName.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                } else {
                    createJobWithFile();
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
                currentURL.setText(ServiceGenerator.getRootURL().toString());
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


    private File createTemporaryFile(Uri sourceURI, Context context) {


        //Create a temporary file in cache, open URI as inputstream and output to temporaryFile
        String fileName = setFileName(sourceURI, MainActivity.this);
        File temporaryFile = new File(getCacheDir(), fileName);
        txtFileName.setText(fileName);
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


    private void createJobWithFile() {

        File theFile = null;
        final String responseString;
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class);

        theFile = createTemporaryFile(fileURI, MainActivity.this);

        RequestBody requestFile = RequestBody.create(MediaType.parse(theFile.toURI().toString()), theFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("printfile", theFile.getName(), requestFile);
        RequestBody theJobName = RequestBody.create(MultipartBody.FORM, theFile.getName());
        Call<String> call = client.createJobWithFile(theJobName, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
               Toast.makeText(MainActivity.this,response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                Log.d("Error did not send", ""+t.getMessage());
            }
        });


    }

    private void findFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK) {
            txtFileName.setText(data.getData().getPath());
            fileURI = data.getData();
        }


    }
}
