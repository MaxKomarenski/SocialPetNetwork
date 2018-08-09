package com.hollybits.socialpetnetwork.validation;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.RegistrationActivity;
import com.hollybits.socialpetnetwork.enums.ErrorType;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.Country;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Victor on 09.08.2018.
 */

public class RegistrationValidator implements Validator {



    private List<Country> countries;

    private List<Breed> breeds;

    private boolean validationState;

    private ErrorType errorType;

    private int petInfoProblem = 0;

    private int owerInfoProblem = 1;

    private  int actualProblem;



    @Override
    public boolean validate(AppCompatActivity activity, int flag) {

        if(!(activity instanceof RegistrationActivity)){
            Log.d("Validator", "You are validating wrong activity");
            return false;
        }
        validationState = true;
        errorType = ErrorType.NO_ERROR;


        AutoCompleteTextView breedInput = activity.findViewById(R.id.breed_edit_text_in_registration);
        AutoCompleteTextView countryInput = activity.findViewById(R.id.country_auto_complete_text_in_registration);
        EditText nameOfPet = activity.findViewById(R.id.name_edit_text_in_registration);
        EditText ageOfPet = activity.findViewById(R.id.age_edit_text_in_registration);
        EditText tagNumberOfPet = activity.findViewById(R.id.tag_number_edit_text_in_registration);
        EditText weightOfPet = activity.findViewById(R.id.weight_edit_text_in_registration);
        EditText ownerName = activity.findViewById(R.id.owner_name_edit_text_in_registration);
        EditText ownerSurname = activity.findViewById(R.id.owner_surname_edit_text_in_registration);
        EditText phoneNumber = activity.findViewById(R.id.phone_number_edit_text_in_registration);
        EditText cityEdit = activity.findViewById(R.id.city_edit_text_in_registration);
        EditText email = activity.findViewById(R.id.email_edit_text_in_registration);
        EditText password = activity.findViewById(R.id.password_edit_text_in_registration);

        if(flag == 0){
               validateNameOfPet(nameOfPet)
                    .than().validateBreedInput(breedInput)
                    .than().validateTagNumber(tagNumberOfPet)
                    .than().validateAgeOfPet(ageOfPet)
                    .than().validateWeightOfPet(weightOfPet);
        }
        else {
        validateName(ownerName)
                    .than().validateSurame(ownerSurname)
                    .than().validatePhone(phoneNumber)
                    .than().validateCountyInput(countryInput)
                    .than().validateCity(cityEdit)
                    .than().validateEmail(email)
                    .than().validatePassword(password);
        }
        return validationState;
    }


    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
    public void setBreeds(List<Breed> breeds) {
        this.breeds = breeds;
    }
    public int getPetInfoProblem() {
        return petInfoProblem;
    }

    public int getOwerInfoProblem() {
        return owerInfoProblem;
    }

    public int getActualProblem() {
        return actualProblem;
    }


    public ErrorType getErrorType() {
        return errorType;
    }


    private RegistrationValidator validateEmail(EditText emailEditText){
        if(!validationState)
            return this;
        String email = emailEditText.getText().toString().trim();

        Log.d("VALIDATING MAIL", email);


        //TODO better email validation it`s only for tests
        if(!email.contains("@")){
            validationState = false;
            actualProblem = owerInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            emailEditText.setError("Wrong formatted email");
            return this;

        }
        if(email.isEmpty()){
            validationState = false;
            actualProblem = owerInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            emailEditText.setError("Please enter your email");
            return this;
        }

        return this;
    }
    private RegistrationValidator validatePassword(EditText passwordEditText){
        if(!validationState)

            return this;
        String password = passwordEditText.getText().toString();


        if(password.length() < 6){
            validationState = false;
            errorType = ErrorType.INPUT_ERROR;
            actualProblem = owerInfoProblem;
            passwordEditText.setError("Password must contain at least 6 characters");
            return this;
        }


        return this;

    }


    private RegistrationValidator validatePhone(EditText phoneNumber){
        if(!validationState)
            return this;

        String phone = phoneNumber.getText().toString();

        if(phone.length() < 9){
            validationState = false;
            actualProblem = owerInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            phoneNumber.setError("Please enter valid phone number");
            return this;
        }

        for (char c: phone.toCharArray()){
            if(Character.isLetter(c)){
                validationState = false;
                errorType = ErrorType.INPUT_ERROR;
                phoneNumber.setError("Please enter valid phone number");
                return this;
            }
        }
        return this;

    }
    private RegistrationValidator validateName(EditText name){
        if(!validationState)
            return this;
        String usersName = name.getText().toString();

        if(usersName.isEmpty()){
            actualProblem = owerInfoProblem;
            validationState = false;
            errorType = ErrorType.INPUT_ERROR;
            name.setError("Please enter your name");
        }

        return this;
    }
    private RegistrationValidator validateSurame(EditText name){
        if(!validationState)
            return this;
        String usersName = name.getText().toString();

        if(usersName.isEmpty()){
            validationState = false;
            actualProblem = owerInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            name.setError("Please enter your surname");
        }

        return this;
    }

    private RegistrationValidator  validateCountyInput(AutoCompleteTextView  countryInput){
        if(!validationState)
            return this;

        if(countries == null){
            validationState = false;
            actualProblem = owerInfoProblem;
            errorType = ErrorType.SERVER_ERROR;
            return this;
        }
        String countryName = countryInput.getText().toString();

        validationState = false;
        actualProblem = owerInfoProblem;
        errorType = ErrorType.INPUT_ERROR;
        for (Country c: countries) {
            if(c.getName().equalsIgnoreCase(countryName)){
                validationState = true;
                errorType = ErrorType.NO_ERROR;
                return this;
            }
        }
        countryInput.setError("Please choose country from list");
        return this;
    }

    private RegistrationValidator validateBreedInput( AutoCompleteTextView breedInput){

        if(!validationState)
            return this;

        if(breeds == null){
            validationState = false;
            actualProblem = petInfoProblem;
            errorType = ErrorType.SERVER_ERROR;
            return this;
        }

        String breedName = breedInput.getText().toString();
        validationState = false;
        actualProblem = petInfoProblem;

        for (Breed b: breeds) {
            if(b.getName().equalsIgnoreCase(breedName)){
                validationState = true;
                errorType = ErrorType.NO_ERROR;
                return this;
            }
        }
        errorType = ErrorType.INPUT_ERROR;
        breedInput.setError("Please choose breed from list");
        return this;
    }

    private RegistrationValidator validateTagNumber(EditText tagNumberOfPet){
        if(!validationState)
            return this;
        if(tagNumberOfPet.getText().toString().isEmpty()){
            validationState = false;
            actualProblem = petInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            tagNumberOfPet.setError("Please enter Tag Number");
        }
        return this;
    }


    private RegistrationValidator validateNameOfPet(EditText editText){
        if(!validationState)
            return this;
        String name = editText.getText().toString();

        if(name.isEmpty()){
            actualProblem = petInfoProblem;
            validationState = false;
            errorType = ErrorType.INPUT_ERROR;
            editText.setError("Please enter name of your Pet");
        }

        return this;
    }
    private RegistrationValidator validateAgeOfPet(EditText editText) {

        if(!validationState)
            return this;
        String age = editText.getText().toString();

        if(age.isEmpty() || !isInteger(age)){
            validationState = false;
            actualProblem = petInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            editText.setError("Please enter valid age");
        }
        return this;

    }
    private RegistrationValidator validateWeightOfPet(EditText editText) {

        if(!validationState)
            return this;
        String weight = editText.getText().toString();

        if(weight.isEmpty() || !isInteger(weight)){
            validationState = false;
            actualProblem = petInfoProblem;
            errorType = ErrorType.INPUT_ERROR;
            editText.setError("Please enter valid weight");
        }
        return this;

    }


    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        if (str.isEmpty()) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }


    private  RegistrationValidator validateCity(EditText cityEditText){
        if(!validationState)
            return this;

        String cityName = cityEditText.getText().toString();

        if(cityName.isEmpty()){
            actualProblem = owerInfoProblem;
            validationState = false;
            errorType = ErrorType.INPUT_ERROR;
            cityEditText.setError("Please enter your city name");
        }
        return this;
    }

    private RegistrationValidator than(){
        return this;
    }

}
