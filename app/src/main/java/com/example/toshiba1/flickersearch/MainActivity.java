package com.example.toshiba1.flickersearch;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.toshiba1.flickersearch.Controller.CustomJsonGeRequest;
import com.example.toshiba1.flickersearch.Controller.RecyclerViewClickListener;
import com.example.toshiba1.flickersearch.models.FlickerData;
import com.example.toshiba1.flickersearch.models.LruBitmapCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ParentActivity implements RecyclerViewClickListener {

    private static final String TAG =MainActivity.class.getSimpleName() ;
    private RecyclerView dataRecyclerView;
    private LinearLayoutManager layoutManager;
    private List<FlickerData> resultData =new ArrayList<FlickerData>();
    private RecyclerView.Adapter adapter;
    static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText flickerSearchView = (EditText) findViewById(R.id.search_text_field);
        flickerSearchView.setVisibility(View.VISIBLE);

        final InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(null, 0);
        flickerSearchView.setGravity(Gravity.LEFT | Gravity.CENTER);
        flickerSearchView.requestFocus();
        inputMethodManager.showSoftInput(flickerSearchView, 0);

        if(isNetworkAvailable()) {
            //After finish writing in edit text and pressing Done
            flickerSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (flickerSearchView.getText().length() != 0) {
                            //Setting data from edit text to "SearchResultFragment"
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        searchResultShow(flickerSearchView.getText().toString());
                            flickerSearchView.getText().clear();
                        } else {
                            Toast.makeText(MainActivity.this, "Please Enter text to search for", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }else{
            Toast.makeText(MainActivity.this, "Please Check your connection to network !", Toast.LENGTH_LONG).show();

        }

        //To send data to recyclerView
        dataRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_search);
        dataRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        dataRecyclerView.setLayoutManager(layoutManager);
    }

    private void searchResultShow(String searchKey) {
        resultData.clear();

        //Initialize array if null
        if(null == resultData){
            resultData = new ArrayList<FlickerData>();
        }
        String FLICKER_URL="https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=f1bf03f4ac0c43a184cb7bd5c6e6af2d&tags="+searchKey+"&format=json&per_page=20&media=photos&nojsoncallback=1&privacy_filter=1";
        Log.v(TAG,"FLICKER_URL: "+FLICKER_URL);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                CustomJsonGeRequest getRequest = new CustomJsonGeRequest(FLICKER_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "RESPONSE OF getRequest: " + response);
                try {
                    Log.v(TAG, "response to retreive" + response.getJSONObject("photos").getJSONArray("photo"));
                    JSONArray photoDataArr=response.getJSONObject("photos").getJSONArray("photo");
                    for (int i=0;i < photoDataArr.length();i++){
                        Log.v(TAG, "response to data" + photoDataArr.get(i));
                        JSONObject obj=new JSONObject(photoDataArr.get(i).toString());

                        FlickerData data=new FlickerData();
                        data.setId(obj.getString("id"));
                        data.setServer(obj.getString("server"));
                        data.setOwner(obj.getString("owner"));
                        data.setSecret(obj.getString("secret"));
                        data.setFarm(obj.getString("farm"));
                        data.setOwner(obj.getString("owner"));
                        data.setIspublic(obj.getInt("ispublic"));
                        data.setTitle(obj.getString("title"));
                        data.setIsfriend(obj.getInt("isfriend"));
                        data.setIsfamily(obj.getInt("isfamily"));

                        getRelatedPhoto(data);
                        resultData.add(data);
                        //Here we set Data in the recyclerView Adapter
                        adapter=new DataResultRecyclerAdapter(getApplicationContext(),MainActivity.this,resultData);
                        dataRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "ERROR OF getRequest: " + error);


                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String json = null;
                            json = new String(error.networkResponse.data);

                                Toast.makeText(MainActivity.this, "ERROR: " + json.toString(), Toast.LENGTH_LONG).show();
//
                        }

                    }
                }
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequest.setPriority(Request.Priority.IMMEDIATE);
        queue.add(getRequest);
//        ImageLoader mImageLoader = new ImageLoader(queue, new LruBitmapCache(
//                LruBitmapCache.getCacheSize(this)));
    }

    protected void getRelatedPhoto(FlickerData photoData) {
        String FLICKER_PHOTO_URL="https://farm"+photoData.getFarm()+".staticflickr.com/"+photoData.getServer()+"/"+photoData.getId()+"_"+photoData.getSecret()+"_m.jpg";
        Log.v(TAG,"FLICKER_PHOTO_URL: "+FLICKER_PHOTO_URL);
        photoData.setURL(FLICKER_PHOTO_URL);
    }




    @Override
    public void recyclerViewListClicked(String id) {
       getPhotoInfo(id);
    }

    private void getPhotoInfo(String id) {
        String PHOTO_INFO_URL="https://api.flickr.com/services/rest/?method=flickr.photos.getInfo&api_key=f1bf03f4ac0c43a184cb7bd5c6e6af2d&photo_id="+id+"&format=json&media=photos&nojsoncallback=1&privacy_filter=1";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        CustomJsonGeRequest getPhotoInfo = new CustomJsonGeRequest(PHOTO_INFO_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "RESPONSE OF getPhotoInfo: " + response);
                try {
                    Log.v(TAG, "response to retreive getPhotoInfo" + response.getJSONObject("photo").getJSONObject("owner"));
                    JSONObject photoDataOnwer=response.getJSONObject("photo").getJSONObject("owner");
                    try {
                        Log.v(TAG, "response to data photoDataOnwer" + photoDataOnwer.getString("nsid"));
                        String ownerId=photoDataOnwer.getString("nsid");

                        //we will use ownerID to get his photos
                        openPhotosOfUser(ownerId);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                    //Here we set Data in the recyclerView Adapter
                    adapter=new DataResultRecyclerAdapter(getApplicationContext(),MainActivity.this,resultData);
                    dataRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "ERROR OF getRequest: " + error);


                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String json = null;
                            json = new String(error.networkResponse.data);

                            Toast.makeText(MainActivity.this, "ERROR: " + json.toString(), Toast.LENGTH_LONG).show();
//
                        }

                    }
                }
        );
        queue.add(getPhotoInfo);
    }

    private void openPhotosOfUser(String ownerId) {

        if (!ownerId.equals(null)) {
            Bundle bundle =new Bundle();
            bundle.putString("OWNER_ID", ownerId);
            UserPhotosFragment userPhotosFragment = new UserPhotosFragment();

            userPhotosFragment.setArguments(bundle);
            fragmentManager=getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container_List,userPhotosFragment).addToBackStack(null).commit();

        } else {
            Toast.makeText(this, R.string.no_data_view, Toast.LENGTH_SHORT).show();
        }
    }

}
