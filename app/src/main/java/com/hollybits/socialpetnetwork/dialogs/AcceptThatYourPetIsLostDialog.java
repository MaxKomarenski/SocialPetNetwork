package com.hollybits.socialpetnetwork.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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

public class AcceptThatYourPetIsLostDialog extends AppCompatDialogFragment {

    private Geocoder locationInfoSupplier;
    private User currentUser;
    private java.util.Map<String, String> code;
    private FusedLocationProviderClient mFusedLocationClient;

    public void initialise(Geocoder locationInfoSupplier,
                           User currentUser,
                           java.util.Map<String, String> code,
                           FusedLocationProviderClient mFusedLocationClient){
        this.locationInfoSupplier = locationInfoSupplier;
        this.currentUser = currentUser;
        this.code = code;
        this.mFusedLocationClient = mFusedLocationClient;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_accept_los_pet, null);
        return builder.setView(view).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        try {
                            List<Address> addresses = locationInfoSupplier.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if(addresses.size()==1){
                                System.err.println(addresses.get(0));
                                MainActivity.getServerRequests().sos(code, currentUser.getId(),
                                        currentUser.getPets().get(0).getId(),
                                        addresses.get(0)).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.code() == 202){
                                            Log.d("SOS", "OK");
                                            //bottomSheet.dismiss();
                                        }
                                        else {
                                            Log.d("SOS", "FAIL: "+ response.code());
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
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel
            }
        }).create();
    }
}
