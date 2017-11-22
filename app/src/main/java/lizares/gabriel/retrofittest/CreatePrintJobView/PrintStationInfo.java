package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Parcival on 10/13/2017.
 */

public class PrintStationInfo implements Serializable {
    private static final String TAG = "PrintStationInfo";

    private String stationName;
    private String stationOwner;
    private double latitude;
    private double longitude;
    ArrayList<StationPrinterInfo> stationPrinterList;


    public PrintStationInfo() {
        stationPrinterList = new ArrayList<>();
    }

    public void buildStationInfo(String response) {
        try {
            JSONObject station = new JSONObject(response);

            //get stationinfo
            JSONObject stationInfo = station.getJSONObject("stationInfo");
            this.stationName = stationInfo.getString("stationName");
            this.stationOwner = stationInfo.getString("stationOwner");
            this.latitude = stationInfo.getDouble("latitude");
            this.longitude = stationInfo.getDouble("longitude");

            //get printers on station
            JSONArray printerList = station.getJSONArray("printerList");
            Log.d(TAG, Integer.toString(printerList.length()));
            for (int index2 = 0; index2 < printerList.length(); index2++) {
                JSONObject printerInfo = printerList.getJSONObject(index2);

                String printerName = printerInfo.getString("printerName");
                String printerModel = printerInfo.getString("printerModel");
                String pageSize = printerInfo.getString("pageSize");
                String inkType = printerInfo.getString("inkType");
                if (!printerName.equals("PDF")) {
                    StationPrinterInfo stationPrinterInfo = new StationPrinterInfo(printerName, printerModel, pageSize, inkType);
                    this.stationPrinterList.add(stationPrinterInfo);
                }
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationOwner() {
        return stationOwner;
    }

    public void setStationOwner(String stationOwner) {
        this.stationOwner = stationOwner;
    }

    public ArrayList<StationPrinterInfo> getStationPrinterList() {
        return stationPrinterList;
    }

    public void setStationPrinterList(ArrayList<StationPrinterInfo> stationPrinterList) {
        this.stationPrinterList = stationPrinterList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
