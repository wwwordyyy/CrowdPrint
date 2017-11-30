package lizares.gabriel.retrofittest.MainView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrintJobListAdapter extends ArrayAdapter<PrintJobInfo> {
    private static final String TAG = "cpdebug";
    private Context context;
    private String username;
    private String authToken;
    private ArrayList<PrintJobInfo> printJobs;

    public PrintJobListAdapter(Context context, ArrayList<PrintJobInfo> resource) {
        super(context, 0, resource);
        this.context = context;
        this.printJobs = resource;
    }

    public void setCredentials(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    public int getCount() {
        return printJobs.size();
    }

    @Nullable
    @Override
    public PrintJobInfo getItem(int position) {
        return printJobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        final PrintJobInfo printJob = getItem(position);
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.print_job_view, null);
        }
        final TextView tvJobName = v.findViewById(R.id.tvJobName);
        final TextView tvJobKey = v.findViewById(R.id.tvJobKey);
        TextView tvJobPrice = v.findViewById(R.id.tvJobPrice);
        TextView tvStatus = v.findViewById(R.id.tvStatus);
      //  Button btnPrint = v.findViewById(R.id.btnPrint);


        tvJobName.setText(printJob.getJobName());
        tvJobKey.setText(printJob.getJobKey());
        tvJobPrice.setText(printJob.getJobPrice());
        tvStatus.setText(printJob.getJobStatus());

        /*
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobKey = tvJobKey.getText().toString();
                Log.d(TAG, "username: " + username);
                Log.d(TAG, "authToken: " + authToken);
                //Toast.makeText(context,jobKey,Toast.LENGTH_LONG).show();
                CrowdPrintAPI client = ServiceGenerator.CreateService(CrowdPrintAPI.class, authToken);
                Call<String> call = client.startPrint(username, jobKey);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(context, response.body(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        */
        return v;
    }
}
