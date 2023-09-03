package com.project.blaze.auth.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.project.blaze.AppMainActivity;
import com.project.blaze.R;
import com.project.blaze.auth.domain.CheckUserValidity;
import com.project.blaze.auth.domain.ValidationResult;
import com.project.blaze.databinding.FragmentLoginBinding;

import java.util.Objects;


public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private NavController navController;
    private String email,password;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.txtRegPrompt.setOnClickListener(v ->
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
        );
        binding.btnLogin.setOnClickListener(v-> checkLoginCred());
    }

    //check credentials
    private void checkLoginCred() {
        email = Objects.requireNonNull(binding.edtLogEmail.getText()).toString();
        password = Objects.requireNonNull(binding.edtLogPass.getText()).toString();
        CheckUserValidity checkUserValidity = new CheckUserValidity(email, password);
        ValidationResult result = checkUserValidity.validate();
        binding.emailLayout.setHelperText(result.getEmailError());
        binding.passLayout.setHelperText(result.getPasswordError());
        if(result.isSuccess())
        {
            resetErrors();
            loginUser();
        }
        else  Toast.makeText(requireActivity(), "Please check your credentials", Toast.LENGTH_SHORT).show();
    }

    private void loginUser() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            Toast.makeText(requireActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireActivity(), AppMainActivity.class));
            requireActivity().finish();
        }).addOnFailureListener(e -> {
            Log.d(TAG, e.toString());
            Toast.makeText(requireActivity(), "Unable to login", Toast.LENGTH_SHORT).show();
        });
    }

    private void resetErrors() {
        binding.emailLayout.setHelperText("");
        binding.passLayout.setHelperText("");
    }
}