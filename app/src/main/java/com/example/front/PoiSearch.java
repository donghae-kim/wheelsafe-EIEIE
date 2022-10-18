package com.example.front;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PoiSearch {

    public String latitude;
    public String longitude;
    public static String totalDistance;
    public static double totalTime;
    public String type;

    public static List<PoiSearch> poisearchlist = new ArrayList();

    public String getlatitude() {return latitude; }
    public String getlongitude() {
        return longitude;
    }
    public String getTotalDistance() { return totalDistance;  }
    public double getTotalTime() { return totalTime;  }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setlongitude(String longitude) { this.longitude = longitude; }
    public void setTotalDistance(String totalDistance) { this.totalDistance = totalDistance; }
    public void setTotalTime(double totalTime) { this.totalTime = totalTime; }

    public void jsonParsing(String json)
    {
        poisearchlist.clear();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray pointarray = jsonObject.getJSONArray("features");

            JSONObject properties = pointarray.getJSONObject(0);
            JSONObject pro = properties.getJSONObject("properties");
            totalDistance= pro.getString("totalDistance");
            totalTime= pro.getDouble("totalTime");
            System.out.println("거리"+totalDistance+" "+totalTime);



            for(int i=0; i<pointarray.length(); i++)
            {
                JSONObject chargeObject = pointarray.getJSONObject(i);
                JSONObject geo = chargeObject.getJSONObject("geometry");
                JSONArray arr = geo.getJSONArray("coordinates");
                PoiSearch poisearch = new PoiSearch();

                for(int a=0; a<arr.length(); a++) {
                    if(geo.getString("type").equals("Point")){
                        continue;
                    }

                    JSONArray xy = arr.getJSONArray(a);
                    System.out.println("++++++"+xy.get(1)+" "+xy.get(0));
                    poisearch.setlatitude(String.valueOf(xy.get(0)));
                    poisearch.setlongitude(String.valueOf(xy.get(1)));
                    poisearchlist.add(poisearch);
                    System.out.println(poisearchlist.size());
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
