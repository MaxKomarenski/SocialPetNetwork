package com.hollybits.socialpetnetwork.adapters;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.Fragments.PhotoPage;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.helper.SquareImageView;

import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PhotoGridAdapter extends BaseAdapter {

    private Context context;
    private final List<Long> images;
    private PhotoManager photoManager;
    View view;
    LayoutInflater layoutInflater;

    public PhotoGridAdapter(Context context, List<Long> images, android.support.v4.app.Fragment fragment) {
        this.context = context;
        this.images = images;
        photoManager = new PhotoManager(fragment);
    }

    @Override
    public int getCount() {
        return images.size();
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
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.imageProgressBar);
            Long id = images.get(position);
            photoManager.loadUsersPhoto(imageView, id, progressBar);





            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.err.println("id------------------>    " + images.get(position));
                    Paper.book().write("Current choice", images.get(position));
                    FragmentDispatcher.launchFragment(PhotoPage.class);
                }
            });

        }

        return view;
    }
}
