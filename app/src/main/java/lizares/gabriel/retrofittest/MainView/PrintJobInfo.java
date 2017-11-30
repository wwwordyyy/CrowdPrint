package lizares.gabriel.retrofittest.MainView;

import java.io.Serializable;

/**
 * Created by Parcival on 9/24/2017.
 */

public class PrintJobInfo implements Serializable{
    private String jobName;
    private String jobKey;
    private String jobPrice;
    private String jobStatus;
    private double pageDimensionX;
    private double pageDimensionY;
    private String inkType;

    public double getPageDimensionX() {
        return pageDimensionX;
    }

    public void setPageDimensionX(double pageDimensionX) {
        this.pageDimensionX = pageDimensionX;
    }

    public double getPageDimensionY() {
        return pageDimensionY;
    }

    public void setPageDimensionY(double pageDimensionY) {
        this.pageDimensionY = pageDimensionY;
    }

    public String getInkType() {
        return inkType;
    }

    public void setInkType(String inkType) {
        this.inkType = inkType;
    }

    public PrintJobInfo(String jobName, String jobKey, String jobPrice, String jobStatus,
                        double pageDimensionX, double pageDimensionY, String inkType) {
        this.jobName = jobName;
        this.jobKey = jobKey;
        this.jobPrice = jobPrice;
        this.jobStatus = jobStatus;
        this.pageDimensionX = pageDimensionX;
        this.pageDimensionY = pageDimensionY;
        this.inkType = inkType;

    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobPrice() {
        return jobPrice;
    }

    public void setJobPrice(String jobPrice) {
        this.jobPrice = jobPrice;
    }
}
