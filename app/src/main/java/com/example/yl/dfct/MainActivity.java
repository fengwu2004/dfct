package com.example.yl.dfct;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

  private LocationManager locationManager;
  private String provider;
  private TextView _text;
  private BeaconManager beaconManager;
  final private int MY_REQUEST_FINE_LOCATION = 1;
  final private int MY_REQUEST_COARSE_LOCATION = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    _text = findViewById(R.id.location);

    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_FINE_LOCATION);
    }
    else {

      onReceiveGPSPermission();
    }
  }

  private void onReceiveLocationPermission() {

    beaconManager = BeaconManager.getInstanceForApplication(this);

    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

    beaconManager.setForegroundScanPeriod(1000);

    beaconManager.bind(this);
  }

  private void onReceiveGPSPermission() {

    provider = LocationManager.GPS_PROVIDER;

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

        Log.d("MainActive:", "GPS可用");
      }

      @Override
      public void onProviderDisabled(String provider) {

      }
    };

    try {

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

      Log.d("Location", "权限出错");
    }

    onReceiveLocationPermission();
  }

  @Override
  public void onBeaconServiceConnect() {

    beaconManager.addRangeNotifier(new RangeNotifier() {
      @Override
      public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if (collection.size() > 0) {

          Log.i("MainActive", "蓝牙数量为" + collection.size());
        }
      }
    });

    try {

      beaconManager.startRangingBeaconsInRegion(new Region("SASASA", null, null, null));
    }
    catch (RemoteException e) {

      Log.d("MainActivity", "蓝牙启动失败");
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    switch (requestCode) {
      case MY_REQUEST_FINE_LOCATION: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          onReceiveGPSPermission();
        }
        break;
      }
    }
  }
}