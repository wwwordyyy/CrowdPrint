package lizares.gabriel.retrofittest.CreatePrintJobView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import lizares.gabriel.retrofittest.R;
import lizares.gabriel.retrofittest.Retrofit.CrowdPrintAPI;
import lizares.gabriel.retrofittest.Retrofit.ServiceGenerator;
import lizares.gabriel.retrofittest.UserAuthentication.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectPrinterActivity extends AppCompatActivity {
    private static final String TAG = "SelectPrinterActivity";
    ListView lvPrinterList;
    StationPrinterAdapter stationPrinterAdapter;
    TextView tvStationName;
    Spinner spPageSizes;
    Spinner spInkType;
    Button btnSubmit;
    PageSizeAdapter pageSizeAdapter;
    InkTypeAdapter inkTypeAdapter;
    List<float[]> pageDimensions = null;

    UserInformation userInformation;
    PrintJobSettings printJobSettings;
    PrintStationInfo printStationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_printer);

        lvPrinterList = (ListView) findViewById(R.id.lvPrinterList);
        tvStationName = (TextView) findViewById(R.id.tvStationName);
        spPageSizes = (Spinner) findViewById(R.id.spPageSize);
        spInkType = (Spinner) findViewById(R.id.spInkType);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        printJobSettings = (PrintJobSettings) getIntent().getSerializableExtra("printJobSettings");
        printStationInfo = (PrintStationInfo) getIntent().getSerializableExtra("station");
        userInformation = (UserInformation) getIntent().getSerializableExtra("userInformation");

        pageSizeAdapter = new PageSizeAdapter(this, new ArrayList<String>());
        spPageSizes.setAdapter(pageSizeAdapter);

        inkTypeAdapter = new InkTypeAdapter(this, new ArrayList<String>());
        spInkType.setAdapter(inkTypeAdapter);


        ArrayList<StationPrinterInfo> printerList = printStationInfo.getStationPrinterList();
        if (printerList != null) {
            stationPrinterAdapter = new StationPrinterAdapter(SelectPrinterActivity.this, printerList);
            lvPrinterList.setAdapter(stationPrinterAdapter);
        }

        lvPrinterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                printJobSettings.setPrinterName(stationPrinterAdapter.getItem(i).getPrinterName());
                StationPrinterInfo stationPrinterInfo = printStationInfo.getStationPrinterList().get(i);
                List<String> pageSizesList = stationPrinterInfo.getPageSize();
                pageSizeAdapter.clear();
                pageSizeAdapter.addAll(pageSizesList);
                pageSizeAdapter.notifyDataSetChanged();
                printJobSettings.setPageDimensions(stationPrinterInfo.getPageDimension().get(i));
                printJobSettings.setPageSize(stationPrinterInfo.getPageSize().get(i));
                pageDimensions = stationPrinterInfo.getPageDimension();

                inkTypeAdapter.clear();
                String strInkType = stationPrinterInfo.getInkType();
                if(strInkType.equals("color") || strInkType.equals("Color") ){
                    inkTypeAdapter.addAll("Color","Black and White");
                    inkTypeAdapter.notifyDataSetChanged();
                    printJobSettings.setInkType("Color");

                }else{
                    inkTypeAdapter.add("Black and White");
                    printJobSettings.setInkType("Black and White");
                }
                inkTypeAdapter.notifyDataSetChanged();

            }
        });

        spPageSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                printJobSettings.setPageSize(pageSizeAdapter.getItem(i));
                printJobSettings.setPageDimensions(pageDimensions.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spInkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                printJobSettings.setInkType(inkTypeAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!printJobSettings.getInkType().equals("") && printJobSettings.getPageDimensions()!=null) {
                    Intent intent = new Intent(SelectPrinterActivity.this, FinalizeJobActivity.class);
                    intent.putExtra("printJobSettings", printJobSettings);
                    intent.putExtra("userInformation", userInformation);
                    startActivity(intent);
                }else{
                    Toast.makeText(SelectPrinterActivity.this, "Please select a printer",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
