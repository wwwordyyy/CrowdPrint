package lizares.gabriel.retrofittest;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Parcival on 9/24/2017.
 */

public class PrintJobListAdapter extends ArrayAdapter<PrintJobInfo> {

    public PrintJobListAdapter(Context context,  ArrayList<PrintJobInfo> resource) {
        super(context,0, resource);
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
        TextView tvJobKey = (TextView) v.findViewById(R.id.tvJobKey);
        TextView tvJobPrice = (TextView) v.findViewById(R.id.tvJobPrice);

        tvJobName.setText(printJob.getJobName());
        tvJobKey.setText(printJob.getJobKey());
        tvJobPrice.setText(printJob.getJobPrice());

        return v;
    }
}
