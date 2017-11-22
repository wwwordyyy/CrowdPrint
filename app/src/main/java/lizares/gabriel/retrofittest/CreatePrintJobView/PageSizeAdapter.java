package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lizares.gabriel.retrofittest.R;

/**
 * Created by Parcival on 11/22/2017.
 */

public class PageSizeAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> pageSizes;
    public PageSizeAdapter(@NonNull Context context, List<String> resource) {
        super(context,0,resource);
        this.context = context;
        this.pageSizes = resource;
    }

    @NonNull
    public View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String pageSize = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.page_size_view, null);
        }
        TextView tvPageSize = convertView.findViewById(R.id.tvPageSize);
        tvPageSize.setText(pageSize);
        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }
}
