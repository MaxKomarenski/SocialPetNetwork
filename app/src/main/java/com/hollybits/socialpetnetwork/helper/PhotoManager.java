package com.hollybits.socialpetnetwork.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 09.09.2018.
 */

public class PhotoManager {


    private Fragment fragment;
    private static PhotoCasheManager photoCasheManager;
    private static User currentUser;
    private static java.util.Map<String, String> authorisationCode;
    public static final String MAIN_PHOTO = "mainPhoto";
    public static final String REGULAR_PHOTO = "regularPhoto";
    public static final String PAPER_BOOK_NAME = "photos";

    private static Map<String, String> photoPathes;


    public PhotoManager(Fragment fragment) {
        this.fragment = fragment;
        if(currentUser == null) {
            currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            authorisationCode = new ConcurrentHashMap<>();
            photoPathes = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        }
    }




    public void loadFriendPhoto(ImageView target, Long id, ProgressBar progressBar){
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(),id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        loadBitmapToImageView(target, bitmap, progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public void loadFriendPhoto(ImageView target, Long id){
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(),id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        loadBitmapToImageView(target, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    public void loadUsersPhoto(ImageView target, Long id){
        byte[] photoBytes = Paper.book(PAPER_BOOK_NAME).read(REGULAR_PHOTO+id);
        if(photoBytes!=null){
            Bitmap photo = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
            loadBitmapToImageView(target, photo);
            return;
        }
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(), id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        loadBitmapToImageView(target, bitmap);
                        Paper.book(PAPER_BOOK_NAME).write(REGULAR_PHOTO+id, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }





    public void loadUsersPhoto(ImageView target, Long id, ProgressBar progressBar){
        byte[] photoBytes = Paper.book(PAPER_BOOK_NAME).read(REGULAR_PHOTO+id);
        if(photoBytes!=null){
            Bitmap photo = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
            loadBitmapToImageView(target, photo, progressBar);
            return;
        }
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(), id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        loadBitmapToImageView(target, bitmap, progressBar);
                        Paper.book(PAPER_BOOK_NAME).write(REGULAR_PHOTO+id, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

   public void loadUsersMainPhoto(ImageView target){

       byte[] photoBytes = Paper.book(PAPER_BOOK_NAME).read(MAIN_PHOTO);
       if(photoBytes!=null){
           Bitmap photo = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
           loadBitmapToImageView(target, photo);
           return;
       }

       MainActivity.getServerRequests().getMainPhoto(authorisationCode, currentUser.getId()).enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               byte[] content;
               if(response.body() != null){
                   try {
                       content = response.body().bytes();
                       Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                       loadBitmapToImageView(target, bitmap);
                       Paper.book(PAPER_BOOK_NAME).write(MAIN_PHOTO, content);
                   } catch (IOException e) {
                       Log.d("PHOTOMANAGER", "ERROR");
                       e.printStackTrace();
                   }
               }
           }
           @Override
           public void onFailure(Call<ResponseBody> call, Throwable t) {
               t.printStackTrace();
           }
       });

   }
    public void loadFriendsMainPhoto(ImageView target,  Long id){

        MainActivity.getServerRequests().getMainPhoto(authorisationCode, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        loadBitmapToImageView(target, bitmap);
                    } catch (IOException e) {
                        Log.d("PHOTOMANAGER", "ERROR");
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


   private void loadBitmapToImageView(ImageView  imageView, Bitmap bitmap, ProgressBar progressBar){
       GlideApp.with(fragment)
               .load(bitmap)
               .listener(new RequestListener<Drawable>() {
                   @Override
                   public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                       progressBar.setVisibility(View.GONE);
                       return false;
                   }

                   @Override
                   public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                       progressBar.setVisibility(View.GONE);
                       return false;
                   }
               })
               .into(imageView);
   }

    private void loadBitmapToImageView(ImageView  imageView, Bitmap bitmap){
        GlideApp.with(fragment)
                .load(bitmap)
                .placeholder(R.drawable.test_photo)
                .into(imageView);
    }


}
