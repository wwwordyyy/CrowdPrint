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
