package org.techtown.letseat.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.letseat.MainActivity;
import org.techtown.letseat.R;
import org.techtown.letseat.util.AppHelper;
import org.techtown.letseat.util.PhotoSave;

import java.util.ArrayList;

public class RestaurantFragment extends Fragment {
    ArrayList<RestaurantItem> items = new ArrayList<>();
    ArrayList<Integer> resIdList = new ArrayList<>();
    public static RestaurantRecycleAdapter adapter = new RestaurantRecycleAdapter();
    int resId;
    String image;
    String ownerId = MainActivity.ownerId;
    Bitmap bitmap;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ownerId = MainActivity.ownerId;
        View view = inflater.inflate(R.layout.restaurant_fragment, container, false);
        Bundle bundle = this.getArguments();
        getResData();
        RecyclerView recyclerView = view.findViewById(R.id.store_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RestaurantRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // 액티비티 시작
                Intent intent = new Intent(getActivity(), RestaurantItemMain.class);
                RestaurantItemMain.resId = resIdList.get(pos);
                int rId = resIdList.get(pos);
                intent.putExtra("send_resId",rId);
                startActivity(intent);
            }
        });

        ExtendedFloatingActionButton store_register_button = view.findViewById(R.id.store_register_button);
        store_register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RestaurantRegisterActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    // 식당 리스트 가져오기
    void getResData() {
        String url = "http://125.132.62.150:8000/letseat/store/findOwner?ownerId="+ownerId;
        JSONArray getData = new JSONArray();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                getData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String restype, resName, location;
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                // 데이터 정보
                                resName = jsonObject.getString("resName");
                                image = jsonObject.getString("image");
                                resId = jsonObject.getInt("resId");
                                bitmap = PhotoSave.StringToBitmap(image);
                                RestaurantItem item = new RestaurantItem(bitmap, resName);
                                items.add(item);
                                resIdList.add(resId);

                            }
                            adapter.setItems(items);
                            adapter.notifyDataSetChanged();
                            Log.d("응답", response.toString());
                        } catch (JSONException e) {
                            Log.d("예외", e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("에러", error.toString());
                    }
                }
        );
        request.setShouldCache(false); // 이전 결과 있어도 새로 요청해 응답을 보내줌
        AppHelper.requestQueue = Volley.newRequestQueue(getActivity()); // requsetQueue 초기화
        AppHelper.requestQueue.add(request);
    }
}