package com.example.toshiba1.flickersearch;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.toshiba1.flickersearch.Controller.CustomJsonGeRequest;
import com.example.toshiba1.flickersearch.Controller.RecyclerViewClickListener;
import com.example.toshiba1.flickersearch.models.FlickerData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserPhotosFragment extends Fragment  implements RecyclerViewClickListener {
    private static final String TAG = UserPhotosFragment.class.getSimpleName() ;
    private List<FlickerData> resultDataPhoto=new ArrayList<FlickerData>();
    private RecyclerView dataRecyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    static FragmentManager fragmentManager;
    private String OWNER_ID;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.user_photo_fragment, container, false);

        fragmentManager=getFragmentManager();
//        final EditText flickerSearchView = (EditText) v.findViewById(R.id.search_text_field);
//        flickerSearchView.setVisibility(View.INVISIBLE);

        TextView title_search=(TextView)v.findViewById(R.id.search_text_view_title_default);
        title_search.setVisibility(View.VISIBLE);

        OWNER_ID=getArguments().getString("OWNER_ID");
        //Getting Data from the previous Fragment "FragmentSearch" & using it
        if(getArguments().getString("OWNER_ID") != null){




//            titleSearch = (TextView) v.findViewById(R.id.search_text_view_title_default);

        //To use back button in toolbar

            ImageView backButton = (ImageView) v.findViewById(R.id.back_arrow);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

            dataRecyclerView=(RecyclerView)v.findViewById(R.id.recycler_view_search);
            dataRecyclerView.setHasFixedSize(true);
            layoutManager=new LinearLayoutManager(getActivity());
            dataRecyclerView.setLayoutManager(layoutManager);


    }
        return  v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parseResult(OWNER_ID);

//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (i == KeyEvent.KEYCODE_BACK) {
//
//                    showSearchFragment();
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    //parsing resultedArray and Transfer to recyclerView ""
    public void parseResult(String ownerId) {

        resultDataPhoto.clear();
        //Initialize array if null
        if(null == resultDataPhoto){
            resultDataPhoto = new ArrayList<FlickerData>();
        }
        //we will use ownerID to get his photos
        String USER_PHOTO_URL="https://api.flickr.com/services/rest/?method=flickr.people.getPublicPhotos&api_key=f1bf03f4ac0c43a184cb7bd5c6e6af2d&user_id="+ownerId+"&format=json&media=photos&nojsoncallback=1";
        RequestQueue queue2 = Volley.newRequestQueue(getActivity().getApplicationContext());

        CustomJsonGeRequest getUserPhotos = new CustomJsonGeRequest(USER_PHOTO_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "RESPONSE OF getUserPhotos: " + response);
                try {
                    Log.v(TAG, "response to retreive getUserPhotos" + response.getJSONObject("photos").getJSONArray("photo"));
                    JSONArray photoOfUser=response.getJSONObject("photos").getJSONArray("photo");

                    for (int k=0;k < photoOfUser.length();k++) {
                        JSONObject photo=new JSONObject(photoOfUser.get(k).toString());

                        FlickerData userphoto = new FlickerData();
                        userphoto.setId(photo.getString("id"));
                        userphoto.setServer(photo.getString("server"));
                        userphoto.setOwner(photo.getString("owner"));
                        userphoto.setSecret(photo.getString("secret"));
                        userphoto.setFarm(photo.getString("farm"));
                        userphoto.setOwner(photo.getString("owner"));
                        userphoto.setIspublic(photo.getInt("ispublic"));
                        userphoto.setTitle(photo.getString("title"));
                        userphoto.setIsfriend(photo.getInt("isfriend"));
                        userphoto.setIsfamily(photo.getInt("isfamily"));

                        MainActivity main=new MainActivity();
                        main.getRelatedPhoto(userphoto);
                        resultDataPhoto.add(userphoto);


                        //Here we set Data in the recyclerView Adapter
                        adapter=new DataResultRecyclerAdapter(getActivity().getApplicationContext(),UserPhotosFragment.this,resultDataPhoto);
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

                            Toast.makeText(getActivity(), "ERROR: " + json.toString(), Toast.LENGTH_LONG).show();
//
                        }

                    }
                }
        );
        getUserPhotos.setRetryPolicy(new DefaultRetryPolicy(
                8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getUserPhotos.setPriority(Request.Priority.IMMEDIATE);
        queue2.add(getUserPhotos);


    }

    @Override
    public void recyclerViewListClicked(String id) {

    }
}
