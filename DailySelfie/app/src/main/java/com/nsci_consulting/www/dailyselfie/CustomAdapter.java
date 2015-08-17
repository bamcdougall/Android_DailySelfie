package com.nsci_consulting.www.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brendan on 8/16/2015.
 */
public class CustomAdapter  extends BaseAdapter {
    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    private ArrayList<PictureItem> pictureItemArrayList;
    private Context mContext;

    public CustomAdapter(ArrayList<PictureItem> pictureItemArrayList, Context c) {
        this.pictureItemArrayList = pictureItemArrayList;
        mContext = c;
    }

    @Override
    public int getCount() {
        return pictureItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return pictureItemArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = view;
        //LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            row = layoutInflater.inflate(R.layout.customlistview, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) row.findViewById(R.id.textView);
            viewHolder.image = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(viewHolder);
            //TextView textView = (TextView) row.findViewById(R.id.textView);
            //ImageView imageView = (ImageView) row.findViewById(R.id.imageView);
        }

        ViewHolder holder = (ViewHolder) row.getTag();
        holder.text.setText(pictureItemArrayList.get(i).getName().substring(0, pictureItemArrayList.get(i).getName().lastIndexOf('.')));
        holder.image.setImageBitmap(pictureItemArrayList.get(i).getImage());

        // hide file extension
        //textView.setText(pictureItemArrayList.get(i).getName().substring(0, pictureItemArrayList.get(i).getName().lastIndexOf('.')));
        //imageView.setImageBitmap(pictureItemArrayList.get(i).getImage());

        return row;
    }

    public void refresh(ArrayList<PictureItem> list){

        this.pictureItemArrayList.clear();

        for(PictureItem item: list){

            this.pictureItemArrayList.add(item);
        }

    }
}
