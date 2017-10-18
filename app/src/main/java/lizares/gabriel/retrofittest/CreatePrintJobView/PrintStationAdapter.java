package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.R;

/**
 * Created by Parcival on 10/11/2017.
 */

public class PrintStationAdapter extends ArrayAdapter<PrintStationInfo> {

    public PrintStationAdapter(@NonNull Context context, ArrayList<PrintStationInfo> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        PrintStationInfo printStationInfo = getItem(position);

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.print_station_view, null);
        }
        TextView tvStationName = v.findViewById(R.id.tvStationName);
        TextView tvStationOwner = v.findViewById(R.id.tvStationOwner);

        tvStationName.setText(printStationInfo.getStationName());
        tvStationOwner.setText(printStationInfo.getStationOwner());
        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            PrintStationInfo printStationInfo = getItem(position);

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.print_station_view, null);
            }
            TextView tvStationName = v.findViewById(R.id.tvStationName);
            TextView tvStationOwner = v.findViewById(R.id.tvStationOwner);

            tvStationName.setText(printStationInfo.getStationName());
            tvStationOwner.setText(printStationInfo.getStationOwner());
            return v;
    }
}
