package com.hollybits.socialpetnetwork.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.back_in_settings_page)
    Button backButton;

    @BindView(R.id.settings_text_in_settings_page)
    TextView settingsTextView;

    @BindView(R.id.sos_text_in_settings)
    TextView sosTextView;

    @BindView(R.id.privacy_text)
    TextView privacyText;

    @BindView(R.id.my_location_text_in_settings)
    TextView myLocationTextView;

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





    }
}
