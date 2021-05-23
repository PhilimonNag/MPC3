package com.philimonnag.mpc;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class MyFunctions {
    public static final String GET_EVENT="https://sheet2api.com/v1/tlc9Q1VaTjmi/myevent";
    public  static  final String GET_BIBLE_VERSE ="https://labs.bible.org/api/?passage=random&type=json";
    public static final String GET_PRAYERS="https://script.google.com/macros/s/AKfycbyWBwOPMjvbTwV2R4B293X2jOjw6L60TycleQ1-ic-xtuA-57N6ZtFppDLzXuoLxqYRSQ/exec";
    public  static  final  String GET_PREACH = "https://script.google.com/macros/s/AKfycbxxQU1Am95MhzF2TOlO56iNyQQNsHBwDJL2-cEUYA_Xl5HFnKnDXJ9KkzlzLv-WmyI6/exec";

    Context context;


    public MyFunctions(Context context) {
        this.context = context;
    }

    public MyFunctions() {

    }
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(JSONArray response);
    }
    public void getBibleVerse(VolleyResponseListener volleyResponseListener) {
        RequestQueue requestQueue;
        String url=GET_BIBLE_VERSE;
        Cache cache = new DiskBasedCache(context.getCacheDir(),1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               volleyResponseListener.onResponse(response);
//                try {
//                    JSONObject jsonObject = response.getJSONObject(0);
//                    Toast.makeText(context, jsonObject.getString("bookname"), Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onError(error.toString());
                //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);



    }
}
