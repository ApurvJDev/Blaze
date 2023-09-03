package com.project.blaze.auth.domain;
//
public class ValidationResult {
    private boolean success = false;
    private String emailError = "";
    private String usernameError = "";
    private String passwordError = "";
    private String confirmPassError = "";

    public ValidationResult() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getEmailError() {
        return emailError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getConfirmPassError() {
        return confirmPassError;
    }

    public void setConfirmPassError(String confirmPassError) {
        this.confirmPassError = confirmPassError;
    }
}