package com.hollybits.socialpetnetwork.validation;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.regex.Pattern;

/**
 * Created by Victor on 09.08.2018.
 */


public interface Validator {

    Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[\\\\w!#$%&'*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$");

    boolean validate(AppCompatActivity activity, int flag);

}
