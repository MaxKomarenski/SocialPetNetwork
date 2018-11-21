package com.hollybits.socialpetnetwork.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private static User currentUser;
    private static java.util.Map<String, String> authorisationCode;
    public static final String MAIN_PHOTO = "mainPhoto";
    public static final String REGULAR_PHOTO = "regularPhoto";
    public static final String PAPER_BOOK_NAME = "photos";
    public static final String PAPER_BOOK_NAME_FRIENDS = "photos";






    public PhotoManager(Fragment fragment) {
        this.fragment = fragment;
        if(currentUser == null){
            init();
        }
    }

    private static void init(){
        currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        authorisationCode = new ConcurrentHashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
    }

    public static void destroy(){
        currentUser = null;
        authorisationCode = null;
    }




    public void loadFriendPhoto(ImageView target, Long id, ProgressBar progressBar){

        byte[]  photoPath = Paper.book(PAPER_BOOK_NAME_FRIENDS).read(REGULAR_PHOTO+id);
        if(photoPath!=null){
            loadBitmapToImageView(target, photoPath, progressBar);
            return;
        }

        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(),id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        loadBitmapToImageView(target, content, progressBar);
                        Paper.book(PAPER_BOOK_NAME_FRIENDS).write(REGULAR_PHOTO+id, content);
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
        byte[]  photoPath = Paper.book(PAPER_BOOK_NAME).read(REGULAR_PHOTO+id);
        if(photoPath!=null){
            loadBitmapToImageView(target, photoPath, progressBar);
            return;
        }
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(), id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        loadBitmapToImageView(target, content, progressBar);
                        //ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                       // String path = saveToInternalStorage(bitmap);
                        Paper.book(PAPER_BOOK_NAME).write(REGULAR_PHOTO+id, content);
                    } catch (Exception e) {
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
        byte[]  photoPath = Paper.book(PAPER_BOOK_NAME).read(REGULAR_PHOTO+id);
        if(photoPath!=null){
            loadBitmapToImageView(target, photoPath);
            return;
        }
        MainActivity.getServerRequests().getPhoto(authorisationCode, currentUser.getId(), id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        loadBitmapToImageView(target, content);
                        //ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        // String path = saveToInternalStorage(bitmap);
                        Paper.book(PAPER_BOOK_NAME).write(REGULAR_PHOTO+id, content);
                    } catch (Exception e) {
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

       byte[]  photoPath  = Paper.book(PAPER_BOOK_NAME).read(MAIN_PHOTO);
       if(photoPath !=null){
           loadBitmapToImageView(target, photoPath);
           return;
       }
       MainActivity.getServerRequests().getMainPhoto(authorisationCode, currentUser.getId()).enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               byte[] content;
               if(response.body() != null){
                   try {
                       content = response.body().bytes();
                      // Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                       loadBitmapToImageView(target, content);
                      // String path = saveToInternalStorage(bitmap);
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

        byte[]  photoPath  = Paper.book(PAPER_BOOK_NAME_FRIENDS).read(MAIN_PHOTO+id);
        if(photoPath !=null){
            loadBitmapToImageView(target, photoPath);
            return;
        }

        MainActivity.getServerRequests().getMainPhoto(authorisationCode, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                       // Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0,content.length);
                        Log.d("PHOTOMANAGER", "LOADED");
                        loadBitmapToImageView(target, content);
                        Paper.book(PAPER_BOOK_NAME_FRIENDS).write(MAIN_PHOTO+id, content);
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

    public static void loadDirectlyUserMainPhoto(ImageView imageView, Activity activity){
        if(currentUser == null){
            init();
        }
        byte[] path  = Paper.book(PAPER_BOOK_NAME).read(MAIN_PHOTO);
        if(path!=null){
            GlideApp.with(activity)
                    .load(path)
                    .placeholder(R.drawable.test_photo)
                    .into(imageView);
            return;
        }

        MainActivity.getServerRequests().getMainPhoto(authorisationCode, currentUser.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                byte[] content;
                if(response.body() != null){
                    try {
                        content = response.body().bytes();
                        GlideApp.with(activity)
                                .load(content)
                                .placeholder(R.drawable.test_photo)
                                .into(imageView);
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


   private void loadBitmapToImageView(ImageView  imageView, byte[] bitmap, ProgressBar progressBar){
        try {
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
        }catch (Exception e){
            return;
        }
   }


    private void loadBitmapToImageView(ImageView  imageView, byte[] bitmap) {
        try {


            if (bitmap == null) {
                Log.d("LOADER", "NULL CONTENT SETTING DEFAULT");
                GlideApp.with(fragment)
                        .load(R.drawable.test_photo)
                        .into(imageView);
            }
            GlideApp.with(fragment)
                    .load(bitmap)
                    .placeholder(R.drawable.test_photo)
                    .into(imageView);
        }catch (NullPointerException e){
            return;
        }
    }





}
