package com.example.dry.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dry.GpsTracker;
import com.example.dry.Item.LocationDTO;
import com.example.dry.Item.LocationItem;
import com.example.dry.NetworkStatus;
import com.example.dry.R;
import com.example.dry.RequestHttpURLConnection;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.RetrofitBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Location extends AppCompatActivity implements NaverMap.OnMapClickListener, OnMapReadyCallback, NaverMap.OnCameraChangeListener, NaverMap.OnCameraIdleListener, Overlay.OnClickListener {

    private String TAG = "Location";
    private ImageView home, board, location, chat, profile;

    private AlertDialog.Builder builder;
    private String[] select;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;
    private NaverMap naverMap;
    private InfoWindow infoWindow;
    private Geocoder geocoder;
    private List<Marker> markerList = new ArrayList<>();
    private List<PolylineOverlay> polylineOverlayList = new ArrayList<>();
    private boolean isCameraAnimated = false;
    private String url;
    private String laundry_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.
                getMapAsync(this);
        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        Log.e(TAG, "?????? : 1" );

        menu();
    }

    @Override
    public void onMapReady(@NonNull @NotNull NaverMap naverMap) {
        Log.e(TAG, "?????? : 2");
        this.naverMap = naverMap;
        geocoder = new Geocoder(this);

        naverMap.setLocationSource(mLocationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnCameraChangeListener(this);
        naverMap.addOnCameraIdleListener(this);
        naverMap.setOnMapClickListener(this);

        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @NotNull
            @Override
            protected View getContentView(@NonNull @NotNull InfoWindow infoWindow) {
                Log.e(TAG, "getContentView : 2");
                Marker marker = infoWindow.getMarker();
                LocationItem item = (LocationItem) marker.getTag();
                url = item.getPlace_url();
                laundry_name = item.getAddress_name();
                View view = View.inflate(Location.this, R.layout.view_info, null);

                TextView name = view.findViewById(R.id.location_infoName);

                name.setText(item.getPlace_name());

                return view;
            }
        });

        infoWindow.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull @NotNull Overlay overlay) {
                select = getResources().getStringArray(R.array.location_select);
                builder = new AlertDialog.Builder(Location.this);
                builder.setItems(select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                MapSearchTask mapSearchTask = new MapSearchTask();
                                mapSearchTask.execute();
                                break;

                            case 1:
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(i);
                                break;
                        }
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });

        LatLng mapCenter = naverMap.getCameraPosition().target;
        fetchInfo(mapCenter.latitude, mapCenter.longitude, 5000);

    }

    public class MapSearchTask extends AsyncTask<Void, Void, String> {

        List<Address> addressList = null;

        @Override
        protected String doInBackground(Void... voids) {

            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

            if(status == NetworkStatus.TYPE_MOBILE){
                Log.e(TAG, "doInBackground : lte ??????" );
            }else if(status == NetworkStatus.TYPE_WIFI){
                Log.e(TAG, "doInBackground : wifi ??????" );
            }else{
                Log.e(TAG, "doInBackground : ????????? ?????? ??????" );
            }

            try {
                // editText??? ????????? ?????????(??????, ??????, ?????? ???)??? ?????? ????????? ????????? ??????
                addressList = geocoder.getFromLocationName(laundry_name, 3);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // ????????? ???????????? split
            String []splitStr = addressList.get(0).toString().split(",");
            String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // ??????
            System.out.println(address);

            String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // ??????
            String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // ??????
            System.out.println(latitude);
            System.out.println(longitude);

            String result = latitude+","+longitude;

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            String[] latlong = result.split(",");
//            System.out.println("?????? ???????????? ????????? ????????? ????????? ?????? "+latlong[0]);
            double lat = Double.parseDouble(latlong[0]);
            double lon = Double.parseDouble(latlong[1]);
//            System.out.println("?????? ???????????? ????????? ????????? ????????? ?????? "+lat);
//            System.out.println("?????? ???????????? ????????? ????????? ????????? ?????? "+lon);


            //????????? ????????? ???????????????
            LatLng endPoint = new LatLng(lat, lon);


            // ?????? ??????
            Marker marker = new Marker();
            marker.setPosition(endPoint);
            // ?????? ??????
            marker.setMap(naverMap);



            //??????????????? ????????? ??????
            GpsTracker gpsTracker = new GpsTracker(Location.this);
            double currentLatitude = gpsTracker.getLatitude();
            double currentLongitude = gpsTracker.getLongitude();

            LatLng startPoint = new LatLng(currentLatitude,currentLongitude);

            //?????? ???????????? ????????? ????????? ???????????? ????????? ??????????????? ?????????
            //LatLng startPoint=getCurrentPosition(naverMap);


            //????????? ????????? ?????? ????????? ????????? url??? ????????? ??????.
            String url=TMapWalkerTrackerURL(startPoint, endPoint);

            //????????? url??? ????????? ???????????? ????????????
            NetworkTask networkTask = new NetworkTask(url, null);
            networkTask.execute();

//            //????????? ????????? ????????? ??????
//            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(endPoint);
//            naverMap.moveCamera(cameraUpdate);
        }
    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;
        ArrayList<LatLng> latLngArrayList=new ArrayList<LatLng>();
        Marker marker = new Marker();
        //TextView totalDistanceText = findViewById(R.id.totalDistance);
       // TextView totalTimeText = findViewById(R.id.totalTime);
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            // ?????? ????????? ????????? ??????.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            // ?????? URL??? ?????? ???????????? ????????????.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()??? ?????? ????????? ?????? onPostExecute()??? ??????????????? ??????????????? s??? ????????????.

            resetPolyLineList();

            Log.e(TAG, "check result : "+s);

            try {
                //?????? ???????????? ????????? ????????? ??????
                JSONObject root = new JSONObject(s);
                System.out.println("?????? ?????? "+root);


                //??? ?????? ?????? featuresArray??? ??????
                JSONArray featuresArray = root.getJSONArray("features");

                for (int i = 0; i < featuresArray.length(); i++){
                    JSONObject featuresIndex = (JSONObject) featuresArray.get(i);
//                    System.out.println("?????? ?????? ????"+featuresIndex);
                    JSONObject geometry =  featuresIndex.getJSONObject("geometry");

                    String type =  geometry.getString("type");

                    //type??? LineString??? ?????? ???????????? ????????? ????????? ???????????? ????????? ??????.
                    //?????? ????????? ??????????????? ???????????????.
                    //type??? Point??? ???????????? ?????????, ?????????, ???????????? ??? ????????? ?????????
                    //???????????? ???????????? ????????? properties??? pointType?????? ?????? ????????????.

                    if(type.equals("LineString")){


                        JSONArray coordinatesArray = geometry.getJSONArray("coordinates");

//                        System.out.println("????????? ????????????"+coordinatesArray);

                        for(int j=0; j<coordinatesArray.length(); j++){

//                            System.out.println(coordinatesArray.get(j).getClass().getName());

                            JSONArray pointArray = (JSONArray) coordinatesArray.get(j);
                            double longitude =Double.parseDouble(pointArray.get(0).toString());
                            double latitude =Double.parseDouble(pointArray.get(1).toString());

                            latLngArrayList.add(new LatLng(latitude, longitude));
                            System.out.println("LineString??? ?????? ");
//                            System.out.println("???????????? ????????????  "+latLngArrayList);
//                            System.out.println("???????????? ????????? ?????????"+latLngArrayList.size());
                        }
                    }

                    if(type.equals("Point")){
                        JSONObject properties =  featuresIndex.getJSONObject("properties");
                        try{
                            double totalDistance = Integer.parseInt(properties.getString("totalDistance"));


                            //totalDistanceText.setText("??? ?????? :"+totalDistance/1000+" km");

                            int totalTime = Integer.parseInt(properties.getString("totalTime"));
                            //totalTimeText.setText("??? ?????? :"+ totalTime/60+"???");

                        }catch (Exception e){

                        }

                       /* String pointType = properties.getString("pointType");


                        double longitude =  Double.parseDouble(geometry.getJSONArray("coordinates").get(0).toString());
                        double latitude =  Double.parseDouble(geometry.getJSONArray("coordinates").get(1).toString());
//                        System.out.println("Point??? ?????? ");
//                        latLngArrayList.add(new LatLng(latitude, longitude));

                        if(pointType.equals("SP")){
                            System.out.println("??????????????????");

                        }
                        else if(pointType.equals("GP")){

                            System.out.println("??????????????????");
                        }
                        else if(pointType.equals("EP")){

                            System.out.println("???????????????");

                        }*/
//                        marker.setPosition(new LatLng(latitude, longitude));
//                        System.out.println(latitude+","+longitude);
//                        marker.setMap(naverMap);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PolylineOverlay polylineOverlay = new PolylineOverlay();
            polylineOverlay.setCoords(latLngArrayList);
            polylineOverlay.setWidth(10);
            polylineOverlay.setColor(getResources().getColor(R.color.green));
            polylineOverlay.setPattern(10,0);
            polylineOverlay.setCapType(PolylineOverlay.LineCap.Round);
            polylineOverlay.setJoinType(PolylineOverlay.LineJoin.Round);
            polylineOverlay.setMap(naverMap);
            polylineOverlayList.add(polylineOverlay);
        }
    }
    private void resetPolyLineList(){
        if(polylineOverlayList != null && polylineOverlayList.size()>0){
            for (PolylineOverlay polylineOverlay1 : polylineOverlayList){
                polylineOverlay1.setMap(null);
            }
            polylineOverlayList.clear();
        }
    }
    public String TMapWalkerTrackerURL(LatLng startPoint, LatLng endPoint){

        String url = null;


        try {
            String appKey = "l7xxf19e1d0d64de455ea809b298dd5f7bad";


            String startX = new Double(startPoint.longitude).toString();
            String startY = new Double(startPoint.latitude).toString();
            String endX = new Double(endPoint.longitude).toString();
            String endY = new Double(endPoint.latitude).toString();

            String startName = URLEncoder.encode("?????????", "UTF-8");

            String endName = URLEncoder.encode("?????????", "UTF-8");
            url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=result&appKey=" + appKey
                    + "&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY
                    + "&startName=" + startName + "&endName=" + endName;


        } catch ( UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return url;
    }

    @Override
    public void onMapClick(@NonNull @NotNull PointF pointF, @NonNull @NotNull LatLng latLng) {
        if(infoWindow.getMarker() != null){
            infoWindow.close();
        }
    }

    @Override
    public boolean onClick(@NonNull @NotNull Overlay overlay) {
        Log.e(TAG, "onClick : 1" );
        if(overlay instanceof Marker) {
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null) {
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }

    private void fetchInfo(double latitude, double longitude, int i) {

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);
        String m = String.valueOf(i);

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);
                Call<LocationDTO> call = service.locationApi(lat, lng, m);
                call.enqueue(new Callback<LocationDTO>() {
                    @Override
                    public void onResponse(Call<LocationDTO> call, Response<LocationDTO> response) {
                        LocationDTO documents = response.body();
                        if(response.isSuccessful() && response.body() != null){
                            Log.e(TAG, "LocationDTO : " +documents.getDocuments().size());
                            updateMapMarkers(documents);
                        }
                    }

                    @Override
                    public void onFailure(Call<LocationDTO> call, Throwable t) {
                        Log.e(TAG, "onFailure : " +t.getMessage());
                    }
                });
            }

    private void updateMapMarkers(LocationDTO result) {
        resetMarkerList();
        for (LocationItem locationItem : result.getDocuments()){
            Marker marker = new Marker();
            marker.setTag(locationItem);
            marker.setPosition(new LatLng(Double.parseDouble(locationItem.getY()), Double.parseDouble(locationItem.getX())));
            marker.setAnchor(new PointF(0.5f, 1/0f));
            marker.setOnClickListener(this);
            marker.setMap(naverMap);
            Log.e(TAG, "updateMapMarkers1 : " +locationItem.getPlace_name());
            Log.e(TAG, "updateMapMarkers 2: " +locationItem.getX());
            Log.e(TAG, "updateMapMarkers :3 " +locationItem.getY());
            markerList.add(marker); //list ??? ????????? ????????? ?????????
        }

    }

    private void resetMarkerList(){
        if(markerList != null && markerList.size()>0){
            for (Marker marker : markerList){
                marker.setMap(null);
            }
            markerList.clear();
        }
    }
    @Override
    public void onCameraChange(int reason, boolean animated) {
        isCameraAnimated = animated;
    }

    @Override
    public void onCameraIdle() {
        if(isCameraAnimated){
            LatLng mapCenter = naverMap.getCameraPosition().target;
            fetchInfo(mapCenter.latitude, mapCenter.longitude, 5000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
          case LOCATION_PERMISSION_REQUEST_CODE:
              mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
              return;
        }

    }

    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Location.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Location.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Location.this, ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Location.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }
    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }else{
            Toast.makeText(this, "???????????? ????????? ?????? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}