package com.example.front;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class pair implements Comparable<pair>{
    double x;
    double y;
    double distance;
    String name;
    pair(double distance, double x, double y,String name){
        this.distance = distance;
        this.x=x;
        this.y=y;
        this.name = name;
    }
    @Override
    public int compareTo(pair p) {
        return (int)(this.distance-p.distance);
    }
}
public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    String API_Key = "l7xxbf43a180f6e14ab69c7c179238f21d85";

    // T Map View
    TMapView tMapView = null;

    ArrayList<HashMap<String,String>> listviewlist=  new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String,Double>> listview_xy=  new ArrayList<HashMap<String, Double>>();
    ArrayList<Listviewitem> listviewsearchedlist= new ArrayList<>();

    ArrayList<String> listviewtmp;
    ArrayList<pair> al = new ArrayList<>();

    SimpleAdapter Adapter;
    // T Map GPS
    TMapGpsManager tMapGPS = null;
    EditText keywordView_start;
    EditText keywordView_end;
    double latitude;
    double longitude;
    double startLatitude, startLongitude, destLatitude, destLongitude;
    boolean isStart = false;
    boolean editState = false;
    boolean listviewchecker = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keywordView_start = (EditText) findViewById(R.id.searchBar_start);
        keywordView_end = (EditText) findViewById(R.id.searchBar_end);
        // T Map View
        tMapView = new TMapView(this);

        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        //초기 설정
        //줌 깊이
        tMapView.setZoomLevel(16);
        //현위치 아이콘 표시
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // T Map View Using Linear Layout
        RelativeLayout RelativeLayoutTmap = (RelativeLayout)findViewById(R.id.map_view);
        RelativeLayoutTmap.addView(tMapView);

        //GPS 요청 권한 없으면 안됨
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // GPS using T Map
        tMapGPS = new TMapGpsManager(this);

        // 초기 설정
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);


        //네트워크 기반으로 위치 제공(실제휴대폰)
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        //gps 기반으로 위치 제공(에뮬레이터에서씀)
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);

        tMapGPS.OpenGps();
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_currenticon);
        tMapView.setIcon(bitmap);

        // 옵션메뉴
        ImageButton optionButton = (ImageButton)findViewById(R.id.optionButton);
        ImageView optionButtonBackground = (ImageView)findViewById(R.id.OptionButtonBackground);
        optionButtonBackground.bringToFront();
        optionButton.bringToFront();

        //searchBar 객체 생성 & 최상단으로 가져오기
        EditText searchBar = (EditText) findViewById(R.id.searchBar_start);
        searchBar.bringToFront();
        EditText searchBar_end = (EditText) findViewById(R.id.searchBar_end);
        searchBar_end.bringToFront();
        //searchbarLayout 객체 생성 & 최상단으로 가져오기
        FrameLayout searchbarLayout = (FrameLayout) findViewById(R.id.searchbarLayout_start);
        searchbarLayout.bringToFront();
        FrameLayout searchbarLayout_end = (FrameLayout) findViewById(R.id.searchbarLayout_end);
        searchbarLayout_end.bringToFront();

        //리스트뷰객체
        ListView list = (ListView)findViewById(R.id.SearchListListView);
        ListView listend = (ListView)findViewById(R.id.SearchListEndListView);
        ListView charge = (ListView)findViewById(R.id.charging);

        //현위치로 돌아오는 버튼 객체 생성 & 클릭 이벤트
        ImageButton CurrentLocation = (ImageButton)findViewById(R.id.CurrentLocate);
        ImageView CurrentLocationBackground = (ImageView)findViewById(R.id.CurrentLocateBackground);
        CurrentLocationBackground.bringToFront();
        CurrentLocation.bringToFront();
        CurrentLocation.setBackgroundResource(R.drawable.ic_gps);
        CurrentLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tMapView.setTrackingMode(true);
                tMapView.setSightVisible(true);
            }
        });


        //출발지 선택시 searchbar 나타내며 수행할 것
        EditText start_edit = (EditText) findViewById(R.id.edit_start);
        start_edit.bringToFront();
        SlidingUpPanelLayout slidingView = (SlidingUpPanelLayout) findViewById(R.id.slidingView);
        start_edit.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view , MotionEvent event){

                editState = false;
                isStart = true;
                listviewchecker=true;

                list.setVisibility(View.VISIBLE);
                list.bringToFront();
                hide(slidingView,0);
                slidingView.setPanelHeight(0);
                listend.setVisibility(View.GONE);

                return true;
            }
        });
        Button backToMain_start = (Button) findViewById(R.id.backToMain_start);
        backToMain_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editState = false;
                listviewchecker=true;
                nonhide(slidingView,0);

                bindList(1,1);
                tMapView.removeAllMarkerItem();
                searchedlist();

                ListView HistoryListView = (ListView)findViewById(R.id.HistoryListView);
                HistoryListView.bringToFront();
                HistoryListView.setVisibility(View.VISIBLE);


            }
        });
        //searchbar에서 MAIN으로 돌아오는 BACKTO MAIN 객체 생성 & 클릭 이벤트
        searchBar.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode,KeyEvent event){
                switch(keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        al.clear();
                        search(1);
                        slidingView.setPanelHeight(300);
                        break;
                    case KeyEvent.KEYCODE_DEL:
                        list.setVisibility(View.INVISIBLE);
                        int length = searchBar.getText().length();
                        if (length > 0) {
                            searchBar.getText().delete(length -1, length);
                        }
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        list.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        LinearLayout dragView = (LinearLayout) findViewById(R.id.dragView);

        //도착지 선택시 searchbar 나타내며 수행할 것
        EditText edit_end = (EditText) findViewById(R.id.edit_end);
        int Height_end = slidingView.getPanelHeight();
        edit_end.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view , MotionEvent event){
                System.out.println("여긴듯");
                //slidingView.setPanelHeight(0);
                editState = false;
                isStart = false;
                listviewchecker=false;

                hide(slidingView,1);
                slidingView.setPanelHeight(0);

                listend.bringToFront();
                tMapView.removeAllMarkerItem();

                System.out.println(dragView.getVisibility());

                return true;
            }
        });
        //searchbar에서 MAIN으로 돌아오는 BACKTO MAIN 객체 생성 & 클릭 이벤트
        Button backToMain_end = (Button) findViewById(R.id.backToMain_end);
        backToMain_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editState = false;
                listviewchecker=false;

                nonhide(slidingView,1);
                bindList(1,0);

            }
        });
        searchBar_end.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode,KeyEvent event){
                switch(keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        al.clear();
                        search(2);
                        slidingView.setPanelHeight(300);

                        break;
                    case KeyEvent.KEYCODE_DEL:
                        int length = searchBar_end.getText().length();
                        if (length > 0) {
                            searchBar_end.getText().delete(length -1, length);
                        }
                        break;
                }
                return true;
            }
        });

        Button backToMain = (Button) findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ConstraintLayout navigation = (ConstraintLayout) findViewById(R.id.Navigation);
                navigation.setVisibility(View.GONE);
                listviewchecker=false;
                editState = false;
                nonhide(slidingView,2);
                tMapView.removeTMapPath();
                charge.setVisibility(View.GONE );
                tMapView.removeTMapPolyLine("경로");
                tMapView.removeAllMarkerItem();
            }
        });

        Button backToMain_charge = (Button) findViewById(R.id.backToMain_charge);
        backToMain_charge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FrameLayout searchbarLayout_charge = (FrameLayout) findViewById(R.id.searchbarLayout_charge);
                searchbarLayout_charge.setVisibility(View.INVISIBLE);
                editState = false;
                listviewchecker=false;
                nonhide(slidingView,1);
                bindList(1,0);
                charge.setVisibility(View.GONE );
            }
        });

        //지도 눌렀을때 주소 읽어옴
        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {

            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint point) {
                Log.d("editState : ",String.valueOf(editState));
                Log.d("isStart : ",String.valueOf(isStart));
                if(editState == false)
                    return;
                TMapMarkerItem tItem = new TMapMarkerItem();
                tItem.setTMapPoint(point);
                tItem.setName("위치");
                tItem.setVisible(TMapMarkerItem.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon);
                tItem.setIcon(bitmap);
                tMapView.addMarkerItem("marker",tItem);
                TMapData tmapdata = new TMapData();
                // point.getLatitude(), point.getLongitude() 각각 위 경도,
                tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(),
                        new TMapData.ConvertGPSToAddressListenerCallback() {
                            @Override
                            public void onConvertToGPSToAddress(String address) {

//                                Log.d("Start ",editText.toString());
//                                if(editText.getText().toString().equals("") || editText.getText().toString() == null) {
                                if(isStart == true){
                                    Log.d("tag","start");
                                    Log.d("address : ",address);
//                                    EditText editStart = (EditText) findViewById(R.id.edit_start);
                                    EditText searchBarStart = (EditText) findViewById(R.id.searchBar_start);
//                                    editStart.setHint(null);
//                                    editStart.setText(address);
                                    searchBarStart.setText(address);
//                                    Log.d("editStart : ",editStart.getText().toString());
                                    Log.d("searchBarStart : ",searchBarStart.getText().toString());
                                    startLatitude = point.getLatitude();
                                    startLongitude =  point.getLongitude();
                                }
                                else{
                                    Log.d("tag","end");
                                    Log.d("address : ",address);
//                                    EditText editEnd = (EditText) findViewById(R.id.edit_end);
                                    EditText searchBarEnd = (EditText) findViewById(R.id.searchBar_end);
//                                    editEnd.setHint(null);
//                                    editEnd.setText(address);
                                    searchBarEnd.setText(address);
//                                    Log.d("editEnd : ",editEnd.getText().toString());
                                    Log.d("searchBarEnd : ",searchBarEnd.getText().toString());
                                    destLatitude = point.getLatitude();
                                    destLongitude =  point.getLongitude();
                                }
                            }
                        });
            }

        });

        //현재 위치를 시작 주소로
        setStartCurrent();


        //리스트뷰 클릭이벤트
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int position, long a_id) {
                searchBar.setText(al.get(position).name);


                //listviewsearchedlist.add(0,al.get(position).name);
                Listviewitem a=new Listviewitem(R.drawable.ic_icon,al.get(position).name,R.drawable.ic_cancel_x);
                listviewsearchedlist.add(a);

                searchedlist();
                Adapter.notifyDataSetChanged();
                startLatitude = al.get(position).x;
                startLongitude = al.get(position).y;
                Toast.makeText(MainActivity.this, (al.get(position).name)+ " 선택하였습니다.", Toast.LENGTH_SHORT).show();
                tMapView.removeAllMarkerItem();
                addMarker(startLatitude,startLongitude,al.get(position).name);
                TMapMarkerItem markeritem = new TMapMarkerItem();
                tMapView.addMarkerItem("select", markeritem);

            }
        });

        listend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int position, long a_id) {
                searchBar_end.setText(al.get(position).name);
               //********************************** listviewsearchedlist.add(0,al.get(position).name);
                Listviewitem a=new Listviewitem(R.drawable.ic_icon,al.get(position).name,R.drawable.ic_cancel_x);
                listviewsearchedlist.add(a);
                Adapter.notifyDataSetChanged();
                destLatitude = al.get(position).x;
                destLongitude = al.get(position).y;
                Toast.makeText(MainActivity.this, (al.get(position).name)+ " 선택하였습니다.", Toast.LENGTH_SHORT).show();
                tMapView.removeAllMarkerItem();
                addMarker(startLatitude,startLongitude,al.get(position).name);
                TMapMarkerItem markeritem = new TMapMarkerItem();
                tMapView.addMarkerItem("select", markeritem);
                confirmClick(listend);
                slidingView.setPanelHeight(0);
                searchedlist();
                listend.setVisibility(View.GONE);
            }
        });

        charge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int position, long a_id) {

                chargingspace cs = new chargingspace();
                searchBar_end.setText(cs.chargelist.get(position).fcltyNm);

                Adapter.notifyDataSetChanged();
                System.out.println(cs.chargelist.get(position).longitude);

                destLatitude = (Double.parseDouble(cs.chargelist.get(position).latitude));
                destLongitude = Double.parseDouble(cs.chargelist.get(position).longitude);
                Toast.makeText(MainActivity.this, (cs.chargelist.get(position).fcltyNm)+ " 선택하였습니다.", Toast.LENGTH_SHORT).show();
                tMapView.removeAllMarkerItem();
                addMarker(startLatitude,startLongitude,cs.chargelist.get(position).fcltyNm);
                TMapMarkerItem markeritem = new TMapMarkerItem();
                tMapView.addMarkerItem("select", markeritem);
                confirmClick(charge);
                slidingView.setPanelHeight(0);
                searchedlist();
                charge.setVisibility(View.GONE);
            }
        });
    }

    public static void httpGetConnection(String UrlData, String ParamData, int index) {

        //http 요청 시 url 주소와 파라미터 데이터를 결합하기 위한 변수 선언
        String totalUrl = "";
        if(ParamData != null && ParamData.length() > 0 &&
                !ParamData.equals("") && !ParamData.contains("null")) { //파라미터 값이 널값이 아닌지 확인
            totalUrl = UrlData.trim().toString() + "?" + ParamData.trim().toString();
        }
        else {
            totalUrl = UrlData.trim().toString();
        }

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        String returnData = "";

        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");

            System.out.println("호출1");
            //http 요청 실시
            conn.connect();
            System.out.println("호출2");
            System.out.println("http 요청 방식 : "+"GET");
            System.out.println("http 요청 타입 : "+"application/json");
            System.out.println("http 요청 주소 : "+UrlData);
            System.out.println("http 요청 데이터 : "+ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();

            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : "+responseCode);
            System.out.println("http 응답 데이터 : "+returnData);

            if(index==1) {
                chargingspace cs = new chargingspace();
                cs.jsonParsing(returnData);
            }
            else if(index==2){
                PoiSearch PS = new PoiSearch();
                PS.jsonParsing(returnData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void hide(SlidingUpPanelLayout slidingView,int check){ // 0 : start, 1 : end ,2 : map button
        LinearLayout dragview1 = (LinearLayout)findViewById(R.id.dragview1);
        LinearLayout dragview2 = (LinearLayout)findViewById(R.id.dragview2);
        ListView HistoryListView = (ListView)findViewById(R.id.HistoryListView);
        slidingView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        Log.d("sliding view : ",String.valueOf(slidingView.getPanelState()));
        ImageButton CurrentLocation = (ImageButton)findViewById(R.id.CurrentLocate);
        ImageButton optionButton = (ImageButton)findViewById(R.id.optionButton);
        FrameLayout searchbarLayoutStart = (FrameLayout) findViewById(R.id.searchbarLayout_start);
        FrameLayout searchbarLayoutEnd = (FrameLayout) findViewById(R.id.searchbarLayout_end);
        ImageView CurrentLocationBackground = (ImageView)findViewById(R.id.CurrentLocateBackground);
        ImageView OptionButtonBackground = (ImageView)findViewById(R.id.OptionButtonBackground);
        ListView list = (ListView)findViewById(R.id.SearchListListView);
        ListView listend = (ListView)findViewById(R.id.SearchListEndListView);

        if(listviewchecker)
            list.setVisibility(View.VISIBLE);

        else
            listend.setVisibility(View.VISIBLE);

        CurrentLocationBackground.setVisibility(View.GONE);
        OptionButtonBackground.setVisibility(View.GONE);
        CurrentLocation.setVisibility(View.GONE);
        optionButton.setVisibility(View.GONE);

        if(check == 0) {
            EditText searchBarStart = (EditText) findViewById(R.id.searchBar_start);
            EditText editStart = (EditText) findViewById(R.id.edit_start);
            searchBarStart.setText(editStart.getText().toString());
            searchbarLayoutStart.setVisibility(View.VISIBLE);
            dragview1.setVisibility(View.GONE);
            dragview2.setVisibility(View.GONE);
            HistoryListView.setVisibility(View.GONE);
        }
        else {
            EditText searchBarEnd = (EditText) findViewById(R.id.searchBar_end);
            EditText editEnd = (EditText) findViewById(R.id.edit_end);
            searchBarEnd.setText(editEnd.getText().toString());
            searchbarLayoutEnd.setVisibility(View.VISIBLE);
            dragview1.setVisibility(View.GONE);
            dragview2.setVisibility(View.GONE);
            HistoryListView.setVisibility(View.GONE);
            if(check == 2) {
                Button confirmButton = (Button) findViewById(R.id.confirm_button);
                confirmButton.setVisibility(View.VISIBLE);
                slidingView.setPanelHeight(0);
            }

        }

    }

    private void bindList(int delete,int index) {
        if(delete==1) {
            listviewlist.clear();
            listview_xy.clear();
            return;
        }
        ListView list = (ListView) findViewById(R.id.SearchListListView);
        ListView listend = (ListView) findViewById(R.id.SearchListEndListView);
        ListView charge = (ListView) findViewById(R.id.charging);
        if(index==1) {
            listend.setVisibility(View.GONE);
            charge.setVisibility(View.GONE);
            Log.d("adapter확인","시작지");
            Adapter = new SimpleAdapter(this, listviewlist, android.R.layout.simple_list_item_2, new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
            Adapter.notifyDataSetChanged();
            list.bringToFront();
            list.setAdapter(Adapter);
            list.setVisibility(View.VISIBLE);
        }
        else if(index==9){

            listend.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
            chargingspace cs = new chargingspace();
            Adapter = new SimpleAdapter(this, cs.chargelistview, android.R.layout.simple_list_item_2, new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
            Adapter.notifyDataSetChanged();
            charge.bringToFront();
            charge.setAdapter(Adapter);
            charge.setVisibility(View.VISIBLE);
        }
        else{
            list.setVisibility(View.GONE);
            charge.setVisibility(View.GONE);
            Log.d("adapter확인","도착지");
            Adapter = new SimpleAdapter(this, listviewlist, android.R.layout.simple_list_item_2, new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
            Adapter.notifyDataSetChanged();
            listend.bringToFront();
            listend.setAdapter(Adapter);
            listend.setVisibility(View.VISIBLE);
        }
    }

    private void searchedlist() {
        ListView list = (ListView) findViewById(R.id.HistoryListView);
        ListviewAdapter adapter=new ListviewAdapter(this,R.layout.listviewitem,listviewsearchedlist);
        list.setAdapter(adapter);
    }

    private void nonhide(SlidingUpPanelLayout slidingView,int check){ // 0 : start, 1 : end, 2 : map button

        ListView HistoryListView = (ListView)findViewById(R.id.HistoryListView);
        LinearLayout dragview1 = (LinearLayout)findViewById(R.id.dragview1);
        LinearLayout dragview2 = (LinearLayout)findViewById(R.id.dragview2);
        ImageButton CurrentLocation = (ImageButton)findViewById(R.id.CurrentLocate);
        ImageButton optionButton = (ImageButton)findViewById(R.id.optionButton);
        FrameLayout searchbarLayoutStart = (FrameLayout) findViewById(R.id.searchbarLayout_start);
        FrameLayout searchbarLayoutEnd = (FrameLayout) findViewById(R.id.searchbarLayout_end);
        ImageView CurrentLocationBackground = (ImageView)findViewById(R.id.CurrentLocateBackground);
        ImageView OptionButtonBackground = (ImageView)findViewById(R.id.OptionButtonBackground);
        ListView list = (ListView)findViewById(R.id.SearchListListView);
        ListView listend = (ListView)findViewById(R.id.SearchListEndListView);
        EditText et = (EditText) findViewById(R.id.searchbarLayout_charge_edit);
        if(listviewchecker)
            listend.setVisibility(View.GONE);
        else
            list.setVisibility(View.GONE);

        CurrentLocationBackground.setVisibility(View.VISIBLE);
        OptionButtonBackground.setVisibility(View.VISIBLE);
        slidingView.setPanelHeight(340);
        CurrentLocation.setVisibility(View.VISIBLE);
        optionButton.setVisibility(View.VISIBLE);

        if(check == 0) {
            EditText searchBarStart = (EditText) findViewById(R.id.searchBar_start);
            EditText editStart = (EditText) findViewById(R.id.edit_start);
            Log.v("searchBarStart : ",searchBarStart.getText().toString());
            editStart.setText(searchBarStart.getText().toString());
            searchbarLayoutStart.setVisibility(View.GONE);
            dragview1.setVisibility(View.VISIBLE);
            dragview2.setVisibility(View.VISIBLE);
            HistoryListView.setVisibility(View.VISIBLE);
        }
        else if(check == 1) {
            EditText searchBarEnd = (EditText) findViewById(R.id.searchBar_end);
            EditText editEnd = (EditText) findViewById(R.id.edit_end);
            editEnd.setText(searchBarEnd.getText().toString());
            searchbarLayoutEnd.setVisibility(View.GONE);
            dragview1.setVisibility(View.VISIBLE);
            dragview2.setVisibility(View.VISIBLE);
            HistoryListView.setVisibility(View.VISIBLE);
        }
        else{
            TextView searchBarStart = (TextView) findViewById(R.id.searchBar_start2);
            EditText editStart = (EditText) findViewById(R.id.edit_start);
            editStart.setText(searchBarStart.getText().toString());

            TextView searchBarEnd = (TextView) findViewById(R.id.searchBar_end2);
            EditText editEnd = (EditText) findViewById(R.id.edit_end);
            editEnd.setText(searchBarEnd.getText().toString());

            FrameLayout searchbarLayout = (FrameLayout) findViewById(R.id.searchbarLayout);
            searchbarLayout.setVisibility(View.GONE);
            et.setVisibility(View.GONE);

            dragview1.setVisibility(View.VISIBLE);
            dragview2.setVisibility(View.VISIBLE);
            HistoryListView.setVisibility(View.VISIBLE);

        }
        //확인 버튼
        Button confirmButton = (Button)findViewById(R.id.confirm_button);
        confirmButton.setVisibility(View.GONE);
    }
    private void search(int index){
        TMapPoint tpoint = tMapView.getLocationPoint();
        latitude = tpoint.getLatitude();
        longitude = tpoint.getLongitude();
        TMapData tmapdata = new TMapData();
        String keyword;
        if(index==1)
            keyword = keywordView_start.getText().toString();
        else
            keyword = keywordView_end.getText().toString();
        TMapView tmapView = new TMapView(this);
        tmapView.setCenterPoint(longitude, latitude, false);
        tmapView.setLocationPoint(longitude, latitude);
        listviewtmp = new ArrayList<>();
        listviewlist.clear();
        listview_xy.clear();
        al.clear();
        tmapdata.findAllPOI( keyword, new TMapData.FindAllPOIListenerCallback()
        {
            @Override
            public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) return;
                tMapView.removeAllMarkerItem();
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    for (TMapPOIItem poi : poiItem) {
                        addMarker(poi);
                        //System.out.println(itemlist.containsValue(item.getPOIName().toString()));
                        if(!listviewtmp.contains(item.getPOIName().toString())) {
                            HashMap<String,String> itemlist = new HashMap<String, String>();
                            HashMap<String,Double> xylist = new HashMap<String, Double>();
                            listviewtmp.add(item.getPOIName().toString());
                            itemlist.put("item1", item.getPOIName().toString());
                            itemlist.put("item2", String.valueOf(Math.round(item.getDistance(tpoint)))+"m");


                            al.add(new pair(item.getDistance(tpoint),item.getPOIPoint().getLatitude(),item.getPOIPoint().getLongitude(),item.getPOIName().toString()));

                            xylist.put(item.getPOIName().toString()+"x",item.getPOIPoint().getLatitude());
                            xylist.put(item.getPOIName().toString()+"y",item.getPOIPoint().getLongitude());

                            listview_xy.add(xylist);
                            //listviewlist.add(itemlist);

                        }
                    }

                    if(!al.isEmpty())
                        Collections.sort(al);



                   /* if(!listviewlist.isEmpty())
                    Collections.sort(listviewlist, new Comparator<HashMap<String, String>>() {
                        @Override
                        public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                            String name1 = (String) o1.get("item2");
                            String name2 = (String) o2.get("item2");
                            if(name1.length()>name2.length()) {
                                return name2.compareTo(name1);
                            }
                            else{
                                return name1.compareTo(name2);
                            }
                        }
                    });*/

                    /*for(int z=0; z<listviewlist.size();z++){
                        System.out.println("C"+listviewlist.get(z));
                    }*/

                }
                listviewlist.clear();
                for(int z=0; z<al.size();z++){
                    String name = al.get(z).name;
                    double distance = Math.round(al.get(z).distance);
                    HashMap<String,String> a = new HashMap<String, String>();
                    a.put("item1",name);
                    a.put("item2",String.valueOf(distance)+"m");
                    listviewlist.add(a);
                }

            }

        });


        if(index==1)
            bindList(0,index);
        else{
            bindList(0,index);
        }

    }

    public void addMarker(TMapPOIItem poi) {
        //point 객체
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(poi.getPOIPoint());

        item.setTMapPoint(item.getTMapPoint()); //마커 위치
        item.setName(poi.getPOIName()); //마커 이름
        item.setCalloutTitle(poi.getPOIName()); //main message
        item.setCalloutSubTitle(poi.getPOIContent());//sub message
        item.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon);//비트맵을 marker 아이콘 설정
        item.setIcon(bitmap);// 아이콘 적용
        item.setCanShowCallout(true);

        tMapView.addMarkerItem(poi.getPOIID(),item);
    }
    public void addMarker(double lat, double lng, String title) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);
        item.setTMapPoint(point);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon);//비트맵을 marker 아이콘 설정
        item.setIcon(bitmap);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(title);
        item.setCalloutSubTitle("sub " + title);
        item.setCanShowCallout(true);
        tMapView.addMarkerItem("m", item);
    }
    public void addMarker(List<chargingspace> a,int i) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint( Double.parseDouble(a.get(i).latitude), Double.parseDouble(a.get(i).longitude));
        item.setTMapPoint(point);


        item.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon);//비트맵을 marker 아이콘 설정
        item.setIcon(bitmap);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(a.get(i).fcltyNm);
        item.setCanShowCallout(true);

        tMapView.addMarkerItem(a.get(i).fcltyNm,item);


    }

    public void popClick(View view){
        PopupMenu popM = new PopupMenu(this,view);
        popM.getMenuInflater().inflate(R.menu.main_menu,popM.getMenu());

        Intent temp = getIntent();
        boolean islogin = temp.getBooleanExtra("isLogin",false);
        MenuItem item = popM.getMenu().findItem(R.id.menu2);
        if(islogin) {
            item.setTitle("로그아웃");
        }
        else{
            item.setTitle("로그인");
        }
        popM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.menu1:
                        intent = new Intent(MainActivity.this,FAQActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu2:
                        if(!islogin) { // 로그인
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else{ // 로그 아웃
                            logout();
                        }
                        return true;
                    case R.id.menu3:
                        intent = new Intent(MainActivity.this,ReportActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popM.show();
    }

    private void logout() {

        //retrofit 생성
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        initLogoutApi initLogoutApi = RetrofitClient.getLogoutInterface();
        Call<String> call = initLogoutApi.getLogoutResponse();

        call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("isSuccessful", String.valueOf(response.isSuccessful()));
                    Log.d("body", String.valueOf(response.body()));
                    Log.d("response", String.valueOf(response.code()));
                    //통신 성공

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("isLogin", false);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    t.printStackTrace();
                    builder.setTitle("알림")
                            .setMessage("예기치 못한 오류가 발생하였습니다.\n 고객센터에 문의바랍니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
            });

    }

    public void confirmClick(View view){
        editState = false;
        FrameLayout searchbarLayoutStart = (FrameLayout) findViewById(R.id.searchbarLayout_start);
        FrameLayout searchbarLayoutEnd = (FrameLayout) findViewById(R.id.searchbarLayout_end);
        FrameLayout searchbarLayout = (FrameLayout) findViewById(R.id.searchbarLayout);
        searchbarLayoutStart.setVisibility(View.GONE);
        searchbarLayoutEnd.setVisibility(View.GONE);

        EditText edit_start= (EditText) findViewById(R.id.edit_start);
        TextView searchBar_start2 = (TextView) findViewById(R.id.searchBar_start2);
        EditText searchBar_end = (EditText) findViewById(R.id.searchBar_end);
        TextView searchBar_end2 = (TextView) findViewById(R.id.searchBar_end2);
        View divider = (View) findViewById(R.id.divider);

        searchBar_start2.setText(edit_start.getText().toString());
        searchBar_end2.setText(searchBar_end.getText().toString());
        searchBar_start2.bringToFront();
        divider.bringToFront();
        searchBar_end2.bringToFront();
        searchbarLayout.bringToFront();


        searchbarLayout.setVisibility(View.VISIBLE);

        tMapView.removeMarkerItem("marker");

        ConstraintLayout navigation = (ConstraintLayout) findViewById(R.id.Navigation);
        navigation.setVisibility(View.VISIBLE);
        navigation.bringToFront();
        drawRoute();//확인 버튼
        Button confirmButton = (Button)findViewById(R.id.confirm_button);
        confirmButton.setVisibility(View.GONE);
    }
    private void drawRoute(){
        TextView searchBar_start = (TextView) findViewById(R.id.searchBar_start2);
        TextView searchBar_end = (TextView) findViewById(R.id.searchBar_end2);
        String startAddress = searchBar_start.getText().toString();
        String destAddress = searchBar_end.getText().toString();
        Log.d("시작 주소 : ",startAddress);
        Log.d("도착 주소 : ",destAddress);
        TMapData tmapdata = new TMapData();
        TMapPoint startpoint = new TMapPoint(startLatitude, startLongitude);
        TMapPoint destpoint = new TMapPoint(destLatitude, destLongitude);

        Thread work = new Thread(){
            public void run()
            {
                try{
                    System.out.println("호출시작");
                    String url = "http://18.207.245.34:3000/users/poisearch";

                    TMapPoint tpoint = tMapView.getLocationPoint();
                    latitude = tpoint.getLatitude();
                    longitude = tpoint.getLongitude();
                /*latitude = 37.497195;
                longitude = 127.027926;*/
                    String data = "fromLon=" + startLongitude + "&" + "toLon=" + destLongitude+ "&" + "fromLat=" + startLatitude+ "&" + "toLat=" + destLatitude+ "&" + "startName=" +"시작"+ "&"+"endName="+"종료";
                    System.out.println(data);
                    //메소드 호출 실시
                    httpGetConnection(url, data,2);
                    Thread.sleep(1);
                }
                catch(InterruptedException a){
                }
            }
        };
        work.start();
        try{
            work.join();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();

        PoiSearch ps = new PoiSearch();
        for(int i=0; i<ps.poisearchlist.size(); i++){
            System.out.println(i);
            alTMapPoint.add( new TMapPoint(Double.parseDouble(ps.poisearchlist.get(i).longitude), Double.parseDouble(ps.poisearchlist.get(i).latitude) ));
            System.out.println("-------"+ps.poisearchlist.get(i).longitude+" "+ps.poisearchlist.get(i).latitude);
        }
        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);
        TMapPolygon tpolygon = new TMapPolygon();
       //tpolygon.setLineColor(getResources().getColorStateList(R.color.purple_500));

        Bitmap start = BitmapFactory.decodeResource(getResources(),R.drawable.ic_current);
        Bitmap end = BitmapFactory.decodeResource(getResources(),R.drawable.ic_icon);
        tMapView.setTMapPathIcon(start, end);

        TMapPoint start1 = new TMapPoint(startLatitude, startLongitude);
        TMapMarkerItem start2 = new TMapMarkerItem();
        start2.setIcon(start); // 마커 아이콘 지정
        start2.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        start2.setTMapPoint( start1 ); // 마커의 좌표 지정
        start2.setName("시작지"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1", start2); // 지도에 마커 추가

        TMapPoint end1 = new TMapPoint(destLatitude, destLongitude);
        TMapMarkerItem end2 = new TMapMarkerItem();
        end2.setIcon(end); // 마커 아이콘 지정
        end2.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        end2.setTMapPoint( end1 ); // 마커의 좌표 지정
        end2.setName("도착지"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem2", end2); // 지도에 마커 추가

        for( int i=0; i<alTMapPoint.size(); i++ ) {
            tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
        }
        tMapView.addTMapPolyLine("경로", tMapPolyLine);

        TextView timeTaken = (TextView) findViewById(R.id.time_taken);
        timeTaken.setText(String.valueOf(Math.round((ps.totalTime/60)*100)/100+1)+"분");

        TextView routeLength = (TextView) findViewById(R.id.route_length);
        routeLength.setText(String.valueOf(ps.totalDistance)+"m");

       /* tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,startpoint,destpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(10);
                        path.setLineColor(Color.rgb(7,125,238));
                        // 경로 길이
                        TextView routeLength = (TextView) findViewById(R.id.route_length);
                        int routeLen = (int) path.getDistance();
                        routeLength.setText(Integer.toString(routeLen)+"m");
                        getTimeTaken(path.getDistance());
                        tMapView.addTMapPath(path);
                        Bitmap start = BitmapFactory.decodeResource(getResources(),R.drawable.ic_current);
                        Bitmap end = BitmapFactory.decodeResource(getResources(),R.drawable.ic_icon);
                        tMapView.setTMapPathIcon(start, end);
                    }
                });
            }
        });*/
    }
    public void getTimeTaken(Double routeLen){
        TextView timeTaken = (TextView) findViewById(R.id.time_taken);
        Double timeT = routeLen / 50;
        int timeTT = (int) Math.ceil(timeT);
        timeTaken.setText(Integer.toString(timeTT)+"분");
    }

    public void switchClick(View view){
        TextView searchBar_start = (TextView) findViewById(R.id.searchBar_start2);
        TextView searchBar_end = (TextView) findViewById(R.id.searchBar_end2);
        String tmp = searchBar_start.getText().toString();
        searchBar_start.setText(searchBar_end.getText().toString());
        searchBar_end.setText(tmp);
        TMapData tmapdata = new TMapData();

        double temp = startLatitude;
        startLatitude = destLatitude;
        destLatitude = temp;

        temp = startLongitude;
        startLongitude = destLongitude;
        destLongitude = temp;

        drawRoute();
    }

    public void ChargingClick(View view) {
        SlidingUpPanelLayout slidingView = (SlidingUpPanelLayout) findViewById(R.id.slidingView);

        LinearLayout dragview1 = (LinearLayout) findViewById(R.id.dragview1);
        LinearLayout dragview2 = (LinearLayout) findViewById(R.id.dragview2);
        ListView HistoryListView = (ListView) findViewById(R.id.HistoryListView);
        slidingView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        Log.d("sliding view : ", String.valueOf(slidingView.getPanelState()));
        ImageButton CurrentLocation = (ImageButton) findViewById(R.id.CurrentLocate);
        ImageButton optionButton = (ImageButton) findViewById(R.id.optionButton);
        FrameLayout searchbarLayout_charge = (FrameLayout) findViewById(R.id.searchbarLayout_charge);
        ImageView CurrentLocationBackground = (ImageView) findViewById(R.id.CurrentLocateBackground);
        ImageView OptionButtonBackground = (ImageView)findViewById(R.id.OptionButtonBackground);
        ListView list = (ListView) findViewById(R.id.SearchListListView);
        ListView listend = (ListView) findViewById(R.id.SearchListEndListView);
        ListView charge = (ListView) findViewById(R.id.charging);
        EditText et = (EditText) findViewById(R.id.searchbarLayout_charge_edit);

        list.setVisibility(View.INVISIBLE);
        listend.setVisibility(View.INVISIBLE);
        charge.setVisibility(View.VISIBLE);
        charge.bringToFront();

        CurrentLocationBackground.setVisibility(View.GONE);
        OptionButtonBackground.setVisibility(View.GONE);
        CurrentLocation.setVisibility(View.GONE);
        optionButton.setVisibility(View.GONE);

        searchbarLayout_charge.bringToFront();
        searchbarLayout_charge.setVisibility(View.VISIBLE);
        et.setVisibility(View.VISIBLE);
        dragview1.setVisibility(View.GONE);
        dragview2.setVisibility(View.GONE);
        HistoryListView.setVisibility(View.GONE);

        Thread work = new Thread(){
        public void run()
            {
                try{
                String url = "http://18.207.245.34:3000/users/electric";

            TMapPoint tpoint = tMapView.getLocationPoint();
            latitude = tpoint.getLatitude();
            longitude = tpoint.getLongitude();
                /*latitude = 37.497195;
                longitude = 127.027926;*/
                String data = "lon=" + longitude + "&" + "lat=" + latitude;
                System.out.println("호출");
                //메소드 호출 실시
                httpGetConnection(url, data,1);
                System.out.println("호출성공");
                Thread.sleep(1);
            }
                catch(InterruptedException a){
                }
            }
        };

        work.start();
        try{
            work.join();
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        bindList(0, 9);
        chargingspace cs = new chargingspace();
        for(int i=0; i<cs.chargelist.size(); i++) {
            addMarker(cs.chargelist,i);
            System.out.println(Double.parseDouble(cs.chargelist.get(i).latitude)+" "+Double.parseDouble(cs.chargelist.get(i).longitude)+" "+cs.chargelist.get(i).fcltyNm);
        }


    }

    public void mapClick(View view){
        SlidingUpPanelLayout slidingView = (SlidingUpPanelLayout) findViewById(R.id.slidingView);
        editState = true;
        isStart = false;
        hide(slidingView,2);
        slidingView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void setStartCurrent(){
        TMapPoint tpoint = tMapView.getLocationPoint();
        EditText start_edit = (EditText) findViewById(R.id.edit_start);
        EditText searchBar_start = (EditText) findViewById(R.id.searchBar_start);
        TextView searchBar_start2 = (TextView) findViewById(R.id.searchBar_start2);
        TMapData tmapdata = new TMapData();
        startLatitude = tpoint.getLatitude();
        startLongitude = tpoint.getLongitude();
        tmapdata.convertGpsToAddress(tpoint.getLatitude(), tpoint.getLongitude(),
                new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String strAddress) {
                        start_edit.setText(strAddress);
                        searchBar_start.setText(strAddress);
                        searchBar_start2.setText(strAddress);
                    }
                });
    }

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        setStartCurrent();
    }
}