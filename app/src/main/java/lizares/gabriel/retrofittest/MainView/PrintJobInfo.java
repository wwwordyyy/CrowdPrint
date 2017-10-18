package lizares.gabriel.retrofittest.MainView;

/**
 * Created by Parcival on 9/24/2017.
 */

public class PrintJobInfo {
    private String jobName;
    private String jobKey;
    private String jobPrice;
    private String jobStatus;

    public PrintJobInfo(String jobName, String jobKey, String jobPrice, String jobStatus) {
        this.jobName = jobName;
        this.jobKey = jobKey;

        this.jobPrice = jobPrice;
        this.jobStatus = jobStatus;
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
