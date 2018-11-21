package com.hollybits.socialpetnetwork.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptThatYourPetIsLostDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Geocoder locationInfoSupplier;
    private User currentUser;
    private java.util.Map<String, String> code;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button yes;
    private Button no;
    private Context a;

    public void initialise(Geocoder locationInfoSupplier,
                           User currentUser,
                           java.util.Map<String, String> code,
                           FusedLocationProviderClient mFusedLocationClient){
        this.locationInfoSupplier = locationInfoSupplier;
        this.currentUser = currentUser;
        this.code = code;
        this.mFusedLocationClient = mFusedLocationClient;
    }


    public AcceptThatYourPetIsLostDialog(@NonNull Context context) {
        super(context);
        a = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sos_dialog);
        yes =  findViewById(R.id.accept_button);
        no =   findViewById(R.id.decline_button);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        TextView sos = findViewById(R.id.lost_pet_text);
        TextView send  = findViewById(R.id.send_sos_text);


        Typeface avenirNextCyr_regular = Typeface.createFromAsset(a.getAssets(), "fonts/AvenirNextCyr-Regular.ttf");
        Typeface gotic = Typeface.createFromAsset(a.getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        sos.setTypeface(gotic);
        send.setTypeface(avenirNextCyr_regular);
        yes.setTypeface(avenirNextCyr_regular);
        no.setTypeface(avenirNextCyr_regular);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept_button:
                sendSignal();
                break;
            case R.id.decline_button:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    @SuppressLint("MissingPermission")
    public void sendSignal(){
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("LOCATOR", "STARTING SOS!!");
                try {
                    List<Address> addresses = locationInfoSupplier.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() == 1) {
                        System.err.println(addresses.get(0));
                        MainActivity.getServerRequests().sos(code, currentUser.getId(),
                                currentUser.getPets().get(0).getId(),
                                addresses.get(0)).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.code() == 202) {
                                    Log.d("SOS", "OK");
                                    //bottomSheet.dismiss();
                                } else {
                                    Log.d("SOS", "FAIL: " + response.code());
                                    //bottomSheet.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                t.printStackTrace();
                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
}
}
