package lizares.gabriel.retrofittest;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Parcival on 10/11/2017.
 */

public class PrintStationAdapter extends ArrayAdapter<String> {
    private Context context;
    private String username;
    private String authToken;

    public PrintStationAdapter(@NonNull Context context, ArrayList<String> resource) {
        super(context, 0, resource);
        this.context = context;
    }

    public void setCredentials(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
