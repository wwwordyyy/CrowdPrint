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


    Uri fileURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFile = (Button) findViewById(R.id.selectFile);
        uploadFile = (Button) findViewById(R.id.uploadFile);
        txtFileName = (TextView) findViewById(R.id.txtFileName);


        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJobWithFile(fileURI, txtFileName.getText().toString(), MainActivity.this);

            }
        });

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findFile();

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


    private File createTemporaryFile(Uri sourceURI,String fileName ) {


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


    private void createJobWithFile(Uri fileURI, String fileName, final Context context) {

        File theFile;
        CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class);

        theFile = createTemporaryFile(fileURI, fileName);

        RequestBody requestFile = RequestBody.create(MediaType.parse(theFile.toURI().toString()), theFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("printfile", theFile.getName(), requestFile);
        RequestBody theJobName = RequestBody.create(MultipartBody.FORM, theFile.getName());
        Call<ResponseBody> call = client.createJobWithFile(theJobName, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context,"Fail",Toast.LENGTH_SHORT).show();
                Log.d("Error", t.getMessage());
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

            String fileName = setFileName(fileURI, MainActivity.this);
            txtFileName.setText(fileName);
        }


    }
}
