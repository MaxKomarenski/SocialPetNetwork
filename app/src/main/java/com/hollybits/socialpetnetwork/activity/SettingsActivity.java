package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.Account;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.User;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.back_in_settings_page)
    Button backButton;

    @BindView(R.id.logout_button_in_settings)
    ImageButton logoutButton;

    @BindView(R.id.settings_text_in_settings_page)
    TextView settingsTextView;

    @BindView(R.id.set_notifications_text)
    TextView setNotificationText;

    @BindView(R.id.sos_text_in_settings)
    TextView sosTextView;

    @BindView(R.id.privacy_text)
    TextView privacyText;

    @BindView(R.id.my_location_text_in_settings)
    TextView myLocationTextView;

    @BindView(R.id.data_text)
    TextView dataSection;

    @BindView(R.id.data_usage_text)
    TextView dataSectionTextView;
    @BindView(R.id.button_clear)
    Button clear;


    @BindView(R.id.sos_switch_compat_in_settings_activity)
    JellyToggleButton sosToggleButton;

    @BindView(R.id.my_location_switch_compat_in_settings_activity)
    JellyToggleButton myLocationToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Typeface mainFont = Typeface.createFromAsset(this.getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        Typeface anotherFont = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeueCyr.ttf");

        backButton.setTypeface(mainFont);
        settingsTextView.setTypeface(mainFont);
        sosTextView.setTypeface(anotherFont);
        privacyText.setTypeface(mainFont);
        myLocationTextView.setTypeface(anotherFont);
        setNotificationText.setTypeface(mainFont);
        dataSection.setTypeface(mainFont);
        dataSectionTextView.setTypeface(anotherFont);
        clear.setTypeface(mainFont);
        sosToggleButton.setRightBackgroundColor("#b6d9f5");
        sosToggleButton.setLeftBackgroundColor("#45b549");
        myLocationToggleButton.setRightBackgroundColor("#b6d9f5");
        myLocationToggleButton.setLeftBackgroundColor("#45b549");


    }


    @Override
    protected void onStart() {
        super.onStart();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FragmentDispatcher.class);
                startActivity(intent);
            }
        });
        clear.setText("Computing...");

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getFullCashSize();
            }
        });
        thread.start();

    }

    private void clearCache() {

        clear.setText("Cleaning...");
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME).getAllKeys()){
            Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(s);
        }
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).getAllKeys()){
            Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(s);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getFullCashSize();
            }
        });
        thread.start();


    }

    private void logOut(){

        sayServerThatAllFriendshipRequestIsDeletedFromCache();

        for(String s: Paper.book().getAllKeys()){
            Paper.book().delete(s);
        }
        for (String s: Paper.book(MainActivity.MESSAGE_BOOK).getAllKeys()){
            Paper.book(MainActivity.MESSAGE_BOOK).delete(s);
        }
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME).getAllKeys()){
            Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(s);
        }
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).getAllKeys()){
            Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(s);
        }
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void sayServerThatAllFriendshipRequestIsDeletedFromCache(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().allFriendshipRequestsIsDeletedFromCache(authorisationCode, currentUser.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("deleted from cache", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void getFullCashSize(){
        Log.d("PATH:",Paper.book(PhotoManager.PAPER_BOOK_NAME).getPath());
        Log.d("PATH:",Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).getPath());
        long sizeMyPhotos = getFolderSize(new File(Paper.book(PhotoManager.PAPER_BOOK_NAME).getPath()));
        long sizeFriendPhotos = getFolderSize(new File(Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).getPath()));
        long sum = sizeFriendPhotos+ sizeMyPhotos;

        String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = (int) (Math.log10(sum) / 3);
        double unitValue = 1 << (unitIndex * 10);
        try {
            String readableSize = new DecimalFormat("#,##0.#")
                    .format(sum / unitValue) + " "
                    + units[unitIndex];
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clear.setText("Clear "+readableSize);
                }
            });
        }catch (ArrayIndexOutOfBoundsException e){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clear.setText("Clear 0.0 KB");
                }
            });
        }


    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();

        int count = files.length;

        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }




}
