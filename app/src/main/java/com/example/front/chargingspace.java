package com.example.front;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chargingspace{
    public static ArrayList<HashMap<String,String>> chargelistview=  new ArrayList<HashMap<String, String>>();
    public String fcltyNm;
    public String latitude;
    public String longitude;
    public String address;
    public static List<chargingspace> chargelist = new ArrayList();

    public String getfcltyNm() {
        return fcltyNm;
    }
    public String getlatitude() {return latitude; }
    public String getlongitude() {
        return longitude;
    }
    public String getaddress() {
        return address;
    }
    public void setfcltyNm(String fcltyNm) {
        this.fcltyNm = fcltyNm;
    }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setlongitude(String longitude) { this.longitude = longitude; }
    public void setaddress(String address) { this.address = address; }

    public void jsonParsing(String json)
    {

        try{
            JSONObject jsonObject = new JSONObject(json);


            //JSONObject라면 바로 jObject.getString("항목");
            JSONObject a = jsonObject.getJSONObject("response");
            JSONObject b = a.getJSONObject("body");
            JSONArray chargearray = b.getJSONArray("items");
            for(int i=0; i<chargearray.length(); i++)
            {
                JSONObject chargeObject = chargearray.getJSONObject(i);

                chargingspace charge = new chargingspace();

                charge.setfcltyNm(chargeObject.getString("fcltyNm"));
                charge.setlatitude(chargeObject.getString("latitude"));
                charge.setlongitude(chargeObject.getString("longitude"));
                charge.setaddress(chargeObject.getString("rdnmadr"));
                chargelist.add(charge);

                HashMap<String,String> itemlist = new HashMap<String, String>();

                itemlist.put("item1", chargeObject.getString("fcltyNm"));
                itemlist.put("item2", chargeObject.getString("rdnmadr"));
                chargelistview.add(itemlist);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

