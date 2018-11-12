package com.hollybits.socialpetnetwork.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.Fragments.PhotoPage;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.helper.SquareImageView;

import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;


public class PhotoGridAdapter extends BaseAdapter {

    private Context context;
    private final List<Long> images;
    private PhotoManager photoManager;
    private GalleryMode galleryMode;
    View view;
    LayoutInflater layoutInflater;

    public PhotoGridAdapter(Context context, List<Long> images, android.support.v4.app.Fragment fragment, GalleryMode galleryMode) {
        this.context = context;
        this.images = images;
        Collections.reverse(images);
        this.galleryMode = galleryMode;
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

            if(galleryMode == GalleryMode.USERS_MODE)
                photoManager.loadUsersPhoto(imageView, id, progressBar);
            else if(galleryMode == GalleryMode.FRIENDS_MODE)
                photoManager.loadFriendPhoto(imageView, id,progressBar);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Paper.book().write("Current choice", images.get(position));
                    FragmentDispatcher.launchFragment(PhotoPage.class);
                }
            });

        }

        return view;
    }



    public void addItem(Long id){
        images.add(id);
        Collections.reverse(images);
    }


}
