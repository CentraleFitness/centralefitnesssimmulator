package fitness.centrale.com.centralefitnesssimulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Boolean canRun = new Boolean(true);
    Integer barValue = new Integer(0);
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView value = (TextView) findViewById(R.id.Value);
        final SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
        bar.setMax(10);
        final Switch switch1 = (Switch) findViewById(R.id.switch1);
        value.setText(String.valueOf(bar.getProgress()));
        final EditText txt = (EditText) findViewById(R.id.Address);

        System.out.println("Test");

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Event catché");
                if (switch1.isChecked()) {
                    System.out.println("Switch activé");
                    address = txt.getText().toString();
                    canRun = true;
                    barValue = bar.getProgress();
                    new ThreadSender().start();
                } else {
                    System.out.println("Switch desactivé");
                    canRun = false;
                }
            }
        });
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(String.valueOf(progress));
                barValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public class ThreadSender extends Thread {


        @Override
        public void run() {
            while (canRun) {

                URL url = null;
                try {
                    url = new URL("http://10.20.86.69:8080/triX");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    con.setDoOutput(true);
                    DataOutputStream wr;
                    try {
                        wr  = new DataOutputStream(con.getOutputStream());
                    }catch (ConnectException e){
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("trix", String.valueOf(barValue.intValue()));
                    wr.writeBytes(new GsonBuilder().create().toJson(map));
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode != 200){
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
