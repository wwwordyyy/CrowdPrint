package lizares.gabriel.retrofittest.CreatePrintJobView;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Parcival on 10/13/2017.
 */

public class PrintJobSettings implements Serializable {
    private String stationName = "";
    private String printerName = "";
    private File jobFile = null;
    private String pageSize = "";

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    private float[] pageDimensions =null;
    private String inkType = "";

    public float[] getPageDimensions() {
        return pageDimensions;
    }

    public void setPageDimensions(float[] pageDimensions) {
        this.pageDimensions = pageDimensions;
    }

    public String getInkType() {
        return inkType;
    }

    public void setInkType(String inkType) {
        this.inkType = inkType;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public File getJobFile() {
        return jobFile;
    }

    public void setJobFile(File jobFile) {
        this.jobFile = jobFile;
    }

    public PrintJobSettings() {

    }
}
