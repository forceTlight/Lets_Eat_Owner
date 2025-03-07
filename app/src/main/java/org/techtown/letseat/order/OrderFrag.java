package org.techtown.letseat.order;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.letseat.MainActivity;
import org.techtown.letseat.R;
import org.techtown.letseat.login.Login;
import org.techtown.letseat.util.AppHelper;

import java.util.ArrayList;

public class OrderFrag extends Fragment {
    ProgressBar progressBar;
    private final ArrayList<OrderData> items = new ArrayList<>();
    public static ArrayList<Integer> resIdList = new ArrayList<>();
    public static OrderAdapter adapter = new OrderAdapter();
    private String ownerId = Login.ownerId;
    private int num;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    JSONObject jso = new JSONObject();
    ArrayList<String> MenuNameList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ownerId = Login.ownerId;
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        progressBar = view.findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = this.getArguments();
        recyclerView = view.findViewById(R.id.add_menu_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        DatabaseReference myRef = database.getReference("ownerId_"+ownerId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 값이 변했을 때마다 작동, text 안에 받아온 데이터 문자열을 넣어줌
                try{
                    num = dataSnapshot.getValue(Integer.class);
                    items.clear();
                    resIdList = new ArrayList<>();
                    getRestaurantList();
                    Log.d("ds","ds");
                }catch(Exception e){
                    myRef.setValue(0);
                    Log.d("ds","ds");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러가 날 때 작동
            }
        });
        return view;
    }
    // Owner Id로 레스토랑 리스트 받기
    void getRestaurantList() {
        String url = "http://125.132.62.150:8000/letseat/store/findOwner?ownerId=" + ownerId;
        Log.d("ds","ds");
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int resId = jsonObject.getInt("resId");
                                resIdList.add(resId);
                            }
                            getWatingOrderList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("에러","에러");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
        request.setShouldCache(false); // 이전 결과 있어도 새로 요청해 응답을 보냄
        AppHelper.requestQueue.add(request);
    }
    // 현재 주문확인 대기중인 주문리스트 받기
    void getWatingOrderList() {
        for (int i = 0; i < resIdList.size(); i++) {
            int resId = resIdList.get(i);
            recyclerView.removeAllViewsInLayout();
            String url = "http://125.132.62.150:8000/letseat/order/list/restaurant?resId=" + resId;
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            int size = items.size();
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    String menus = "";
                                    int sum = 0;
                                    JSONObject jsonObject = (JSONObject) response.get(i);
                                    ArrayList<String> menuList = new ArrayList<String>();
                                    ArrayList<Integer> menuPrice = new ArrayList<Integer>();
                                    String orderTime = jsonObject.getString("orderTime");
                                    String tableNumber = jsonObject.getString("tableNumber");
                                    String request = jsonObject.getString("request");
                                    String orderCheck = jsonObject.getString("checkYN");
                                    String servingCheck = jsonObject.getString("servingYN");
                                    int orderId = jsonObject.getInt("orderId");
                                    JSONArray resMenus = jsonObject.getJSONArray("resMenus");
                                        for (int j = 0; j < resMenus.length(); j++) {
                                            JSONObject menu = resMenus.getJSONObject(j);
                                            String menu_name = menu.getString("name");
                                            int menu_price = menu.getInt("price");
                                            menuList.add(menu_name);
                                            menuPrice.add(menu_price);
                                        }
                                        JSONArray orderMenus = jsonObject.getJSONArray("orderMenus");
                                        for (int j = 0; j < orderMenus.length(); j++) {
                                            JSONObject orderMenu = orderMenus.getJSONObject(j);
                                            int amount = orderMenu.getInt("amount");
                                            int price = menuPrice.get(j);
                                            if (amount == 1) {
                                                sum += price;
                                            } else if (amount >= 2) {
                                                sum += price * amount;
                                            }
                                            if (j == 0) {
                                                menus = menuList.get(j) + " " + amount + "개";
                                            } else {
                                                menus += ", " + menuList.get(j) + " " + amount + "개 ";
                                            }
                                        }
                                    if (servingCheck.equals("N")) {
                                        OrderData orderData = new OrderData(orderTime, tableNumber, menus, request, sum + "원 결제", orderCheck, orderId);
                                        items.add(orderData);
                                    } else {
                                        Log.d("응답", response.toString());
                                    }
                                }
                                start();
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
            request.setShouldCache(false); // 이전 결과 있어도 새로 요청해 응답을 보냄
            AppHelper.requestQueue.add(request);
        }
    }
    public void start() {
        adapter.setItems(items);
        if(items.size() != 0){
            PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),0,
                    new Intent(getActivity(),MainActivity.class),0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("주문");
            builder.setContentText("새로운 주문이 들어왔습니다.");
            builder.setContentIntent(contentIntent);
            builder.setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            }
            notificationManager.notify(1, builder.build());
        }
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
    }
}