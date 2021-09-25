package org.techtown.letseat.restaurant.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.letseat.R;
import org.techtown.letseat.restaurant.info.RestInfoRecycleItem;

import java.util.ArrayList;

public class RestListAdapter extends RecyclerView.Adapter<RestListAdapter.ViewHolder>
        implements OnRestaurantItemClickListner
{

    private ArrayList<RestListRecycleItem> items = new ArrayList<>();
    private ArrayList<RestInfoRecycleItem> RestInfoRecycleItems = new ArrayList<>();

    OnRestaurantItemClickListner listner;

    @NonNull
    @Override
    public RestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_recycle, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestListAdapter.ViewHolder viewHolder, int position) {

        RestListRecycleItem item = items.get(position);

        viewHolder.ivRest.setImageResource(item.getSrc());
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvAdress.setText(item.getAdress());
        viewHolder.tvGenre.setText(item.getGenre());

    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public void setItems(ArrayList<RestListRecycleItem> items) {
        this.items = items;
    }

    public void OnItemClick(ViewHolder holder, View view, int position){
        if(listner != null){
            listner.OnItemClick(holder,view,position);
        }
    }

    public void setItemClickListner(OnRestaurantItemClickListner listner){
        this.listner = listner;
    }

    public RestListRecycleItem getItemData(int num){
        return items.get(num);
    }


    public RestListRecycleItem getItem(int position){
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivRest;
        TextView tvName, tvAdress, tvGenre;

        ViewHolder(View itemView) {
            super(itemView);

            ivRest = itemView.findViewById(R.id.iv_item_res);

            tvName = itemView.findViewById(R.id.tv_item_res_name);
            tvAdress = itemView.findViewById(R.id.tv_item_res_adress);
            tvGenre = itemView.findViewById(R.id.tv_item_food_genre);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(listner !=null){
                        listner.OnItemClick(ViewHolder.this,v,position);
                    }

                }
            });

        }
    }
}
