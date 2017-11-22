package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parcival on 10/13/2017.
 */

public class StationPrinterInfo implements Serializable {
    private static final String TAG = "StationPrinterInfo";
    private String printerName;
    private String printerModel;
    private List<String> pageSize = new ArrayList<>();
    private List<float[]> pageDimension = new ArrayList<>();
    private String inkType;


    public String getInkType() {
        return inkType;
    }

    public StationPrinterInfo(String printerName, String printerModel, String pageSize, String inkType) {
        this.printerName = printerName;
        this.printerModel = printerModel;

        try {
            JSONArray arrPageSizes = new JSONArray(pageSize);
            for(int index = 0; index<arrPageSizes.length();index++){
                String size = arrPageSizes.getString(index);
                String[] arrSizeInfo = size.split(",");
                this.pageSize.add(arrSizeInfo[0]);
                Log.d(TAG,Float.parseFloat(arrSizeInfo[1])+","+Float.parseFloat(arrSizeInfo[2]));
                this.pageDimension.add(new float[]{Float.parseFloat(arrSizeInfo[1]),Float.parseFloat(arrSizeInfo[2])});


            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        this.inkType = inkType;

    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
    }

    public List<String> getPageSize() {
        return pageSize;
    }

    public List<float[]> getPageDimension() {
        return pageDimension;
    }
}

