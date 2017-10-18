package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.CreatePrintJobView.PrintStationAdapter;
import lizares.gabriel.retrofittest.CreatePrintJobView.PrintStationInfo;
import lizares.gabriel.retrofittest.CreatePrintJobView.StationPrinterAdapter;
import lizares.gabriel.retrofittest.CreatePrintJobView.StationPrinterInfo;
import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Parcival on 10/18/2017.
 */

public class StationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "StationInfoWindow";
    private View view;
    private UserInformation userInformation;
    private Context context;
    CrowdPrintAPI client;
    StationPrinterAdapter stationPrinterAdapter = null;


    public StationInfoWindowAdapter(Context context, UserInformation userInformation) {
        this.context = context;
        this.userInformation = userInformation;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        this.view = layoutInflater.inflate(R.layout.station_info_window, null);
        client = ServiceGenerator.CreateService(CrowdPrintAPI.class, userInformation.getAuthToken());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if(marker.getTag()!=null){
            TextView tvStationName = view.findViewById(R.id.tvStationName);
            TextView tvStationOwner = view.findViewById(R.id.tvStationOwner);
            ListView lvStationPrinter = view.findViewById(R.id.lvStationPrinter);

            ArrayList<StationPrinterInfo> printerInfoList = new ArrayList<>();
            stationPrinterAdapter = new StationPrinterAdapter(context, printerInfoList);
            lvStationPrinter.setAdapter(stationPrinterAdapter);

            PrintStationInfo printStationInfo = (PrintStationInfo) marker.getTag();
            tvStationName.setText(printStationInfo.getStationName());
            tvStationOwner.setText(printStationInfo.getStationOwner());
            stationPrinterAdapter.addAll(printStationInfo.getStationPrinterList());
            stationPrinterAdapter.notifyDataSetChanged();
        }


        return view;
    }
}
