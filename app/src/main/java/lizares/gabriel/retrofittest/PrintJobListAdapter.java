package lizares.gabriel.retrofittest;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Parcival on 9/24/2017.
 */

public class PrintJobListAdapter extends ArrayAdapter<PrintJobInfo> {

    private Context context;
    private String username;
    private String authToken;
    public PrintJobListAdapter(Context context,  ArrayList<PrintJobInfo> resource) {
        super(context,0, resource);
        this.context = context;
    }

    public void setCredentials(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        PrintJobInfo printJob = getItem(position);
        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.print_job_view,null);
        }
        TextView tvJobName= (TextView) v.findViewById(R.id.tvJobName);
        final TextView tvJobKey = (TextView) v.findViewById(R.id.tvJobKey);
        TextView tvJobPrice = (TextView) v.findViewById(R.id.tvJobPrice);
        TextView tvStatus =(TextView) v.findViewById(R.id.tvStatus);
        Button btnPrint = (Button) v.findViewById(R.id.btnPrint);


        tvJobName.setText(printJob.getJobName());
        tvJobKey.setText(printJob.getJobKey());
        tvJobPrice.setText(printJob.getJobPrice());
        tvStatus.setText(printJob.getJobStatus());
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobKey = tvJobKey.getText().toString();
                Log.d("cpdebug","username: "+username);
                Log.d("cpdebug","authToken: "+authToken);
                //Toast.makeText(context,jobKey,Toast.LENGTH_LONG).show();
                CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class,authToken);
                Call<String> call = client.startPrint(username,jobKey);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(context,response.body(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        return v;
    }
}
