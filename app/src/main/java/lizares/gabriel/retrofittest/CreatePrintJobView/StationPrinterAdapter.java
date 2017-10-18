package lizares.gabriel.retrofittest.CreatePrintJobView;

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

import lizares.gabriel.retrofittest.R;

/**
 * Created by Parcival on 10/13/2017.
 */

public class StationPrinterAdapter extends ArrayAdapter<StationPrinterInfo> {

    public StationPrinterAdapter(@NonNull Context context, ArrayList<StationPrinterInfo> resource) {
        super(context, 0, resource);
    }

    @NonNull
    public View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StationPrinterInfo stationPrinterInfo = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.station_printer_view, null);
        }
        TextView tvPrinterName = convertView.findViewById(R.id.tvPrinterName);
        TextView tvPrinterModel = convertView.findViewById(R.id.tvPrinterModel);

        tvPrinterName.setText(stationPrinterInfo.getPrinterName());
        tvPrinterModel.setText(stationPrinterInfo.getPrinterModel());
        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
