package com.example.yl.dfct;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  private LocationManager locationManager;
  private String provider;
  private TextView _text;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    _text = findViewById(R.id.location);

    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    List<String> list = locationManager.getProviders(true);

    if (list.contains(LocationManager.NETWORK_PROVIDER)) {

      provider = LocationManager.NETWORK_PROVIDER;
    }
    else if (list.contains(LocationManager.GPS_PROVIDER)) {

      provider = LocationManager.GPS_PROVIDER;
    }
    else {

      Toast.makeText(this, "请检查网络或GPS是否打开", Toast.LENGTH_LONG).show();

      return;
    }

    try {

      LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

          if (location != null) {

            String string = "纬度为" + location.getLatitude() + "经度为" + location.getLongitude();

            Log.d("位置为:", string);

            _text.setText(string);
          }
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

      locationManager.requestLocationUpdates(provider, 2000, 2, locationListener);

      Location location = locationManager.getLastKnownLocation(provider);

      if (location != null) {

        //获取当前位置，这里只用到了经纬度
        String string = "纬度为：" + location.getLatitude() + ",经度为：" + location.getLongitude();

        _text.setText(string);
      }
      else {

        _text.setText("没有获取到GPS位置");
      }
    }
    catch (SecurityException e) {

      Log.d("location service", e.getMessage());

      return;
    }

  }
}
