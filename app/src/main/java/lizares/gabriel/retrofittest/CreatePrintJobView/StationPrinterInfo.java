package lizares.gabriel.retrofittest.CreatePrintJobView;

import java.io.Serializable;

/**
 * Created by Parcival on 10/13/2017.
 */

public class StationPrinterInfo implements Serializable {
    private String printerName;
    private String printerModel;

    public StationPrinterInfo(String printerName, String printerModel) {
        this.printerName = printerName;
        this.printerModel = printerModel;
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
}
