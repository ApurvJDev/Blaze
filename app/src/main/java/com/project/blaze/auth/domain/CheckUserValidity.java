package com.project.blaze.auth.domain;
import android.util.Patterns;

public class CheckUserValidity {

    private final String email,password;
    private String confirmPass,username;
    private boolean valid = true;
    private final ValidationResult validationResult;

    public CheckUserValidity(String email, String password) {
        validationResult = new ValidationResult();
        this.email = email;
        this.password = password;
        check();
    }

    public CheckUserValidity(String email, String username,String password, String confirmPass) {
        validationResult = new ValidationResult();
        this.email = email;
        this.username = username;
        this.password = password;
        this.confirmPass = confirmPass;
        check();
        checkForRegister();
    }

    public ValidationResult validate()
    {
        validationResult.setSuccess(valid);
        return validationResult;
    }

    private void check() {

        if(email.equals(""))
        {
            validationResult.setEmailError("Empty Field");
            valid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            validationResult.setEmailError("Invalid email");
            valid = false;
        }

        if(password.equals(""))
        {
            validationResult.setPasswordError("Empty Field");
            valid = false;
        }
        else if(password.length()<6)
        {
            validationResult.setPasswordError(" At least 6 characters required");
            valid = false;
        }
    }
    private void checkForRegister() {
        if (username.equals(""))
        {
            validationResult.setUsernameError("No Username");
        }

        if(confirmPass.equals("")) {
            validationResult.setConfirmPassError("Empty Field");
            valid = false;
        }
        else if(confirmPass.length()<6)
        {
            validationResult.setConfirmPassError("At least 6 characters required");
            valid = false;
        }
        else if (!confirmPass.equals(password))
        {
            validationResult.setConfirmPassError("Password doesn't match");
            valid = false;
        }

    }
}

