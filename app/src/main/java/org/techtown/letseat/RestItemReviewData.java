package org.techtown.letseat;

import android.graphics.Bitmap;

import org.techtown.letseat.R;

import java.util.ArrayList;

public class RestItemReviewData {

    ArrayList<RestItemReviewItem> items = new ArrayList<>();

    RestItemReviewItem rest[] = new RestItemReviewItem[100];

    int p = 0;

    public ArrayList<RestItemReviewItem> getItems(ArrayList list) {

        for(int i = 0; i < list.size()/5; i++){
            rest[i+1] = new RestItemReviewItem(list.get(p).toString(),(float)list.get(p+1),list.get(p+2).toString(),
                    (Bitmap)list.get(p+3),list.get(p+4).toString());
            items.add(rest[i+1]);
            p += 5;
        }


        return items;
    }
}


