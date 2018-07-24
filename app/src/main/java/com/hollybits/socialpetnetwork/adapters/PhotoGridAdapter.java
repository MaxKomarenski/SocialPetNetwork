package com.hollybits.socialpetnetwork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hollybits.socialpetnetwork.FragmentDispatcher;
import com.hollybits.socialpetnetwork.Fragments.PhotoPage;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.helper.SquareImageView;

import io.paperdb.Paper;


public class PhotoGridAdapter extends BaseAdapter {

    private Context context;
    private final Integer[] images;
    View view;
    LayoutInflater layoutInflater;

    public PhotoGridAdapter(Context context, Integer[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            view = new View(context);
            view = layoutInflater.inflate(R.layout.single_photo, null);
            SquareImageView imageView = (SquareImageView) view.findViewById(R.id.single_photo_in_photo_grid);
            imageView.setImageResource(images[position]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write("Current choice", images[position]);
                    FragmentDispatcher.launchFragment(PhotoPage.class);
                }
            });

        }

        return view;
    }
}
