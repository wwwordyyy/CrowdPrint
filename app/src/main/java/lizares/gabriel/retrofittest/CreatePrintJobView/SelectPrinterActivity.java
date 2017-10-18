package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;

public class SelectPrinterActivity extends AppCompatActivity {

    ListView lvPrinterList;
    StationPrinterAdapter stationPrinterAdapter;
    TextView tvStationName;

    UserInformation userInformation;
    PrintJobSettings printJobSettings;
    PrintStationInfo printStationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_printer);

        lvPrinterList = (ListView) findViewById(R.id.lvPrinterList);
        tvStationName = (TextView) findViewById(R.id.tvStationName);

        printJobSettings = (PrintJobSettings) getIntent().getSerializableExtra("printJobSettings");
        printStationInfo =(PrintStationInfo) getIntent().getSerializableExtra("station");
        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");

        ArrayList<StationPrinterInfo> printerList = printStationInfo.getStationPrinterList();
        if(printerList!=null){
            stationPrinterAdapter = new StationPrinterAdapter(SelectPrinterActivity.this,printerList);
            lvPrinterList.setAdapter(stationPrinterAdapter);
        }

        lvPrinterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                printJobSettings.setPrinterName(stationPrinterAdapter.getItem(i).getPrinterName());
                Intent intent = new Intent(SelectPrinterActivity.this,FinalizeJobActivity.class);
                intent.putExtra("printJobSettings",printJobSettings);
                intent.putExtra("userInformation",userInformation);
                startActivity(intent);
            }
        });
    }
}
