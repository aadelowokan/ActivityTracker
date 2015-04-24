package com.example.adedayoadelowokan.assignment03;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    TraceView traceView;
    TextView time, speed, currentAverage, overallAverage;
    Button timer;
    ArrayList<Location> locations;
    LocationManager locationManager;
    LocationListener locationListener;
    long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locations = new ArrayList<Location>();

        locationListener = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                locations.add(location);
                traceView.setTrace(locations);
                updateUI();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        time = (TextView) findViewById(R.id.tvTime);
        speed = (TextView) findViewById(R.id.tvSpeed);
        currentAverage = (TextView) findViewById(R.id.tvCurrentAverage);
        overallAverage = (TextView) findViewById(R.id.tvOverallAverage);
        traceView = (TraceView) findViewById(R.id.traceView);
        timer = (Button) findViewById(R.id.bTimer);
        setTimerListener();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTimerListener()
    {
        timer.setOnClickListener(new View.OnClickListener() {
            boolean started = false;
            @Override
            public void onClick(View v) {
                if(!started)
                {
                    timer.setText("Stop Tracking");
                    startTracking();
                    started = true;
                    startTime = System.currentTimeMillis();
                }
                else
                {
                    timer.setText("Start Tracking");
                    stopTracking();
                    started = false;
                    endTime = System.currentTimeMillis();
                }
            }
        });
    }

    public void setLocationListener()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener );
    }

    public void startTracking()
    {
        traceView.trace.clear();
        traceView.reset();
        locations.clear();
        setLocationListener();
    }

    public void stopTracking()
    {
        locationManager.removeUpdates(locationListener);
        endTime = System.currentTimeMillis();
    }

    public void updateUI()
    {
        endTime = System.currentTimeMillis();
        endTime = (endTime - startTime) / 1000;
        time.setText("Time(s): " + endTime);
        speed.setText("Speed(km/h): " + traceView.trace.get(traceView.trace.size() - 1).y);
        currentAverage.setText("Current Average(km/h): " + traceView.currentAverageSpeed);
        overallAverage.setText("Overall Average(km/h): " + traceView.averageSpeed);
        traceView.update();
    }
}
