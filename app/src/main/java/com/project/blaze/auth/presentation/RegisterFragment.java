package com.project.blaze.auth.presentation;

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
import com.project.blaze.R;
import com.project.blaze.auth.domain.CheckUserValidity;
import com.project.blaze.auth.domain.CreateUserProfile;
import com.project.blaze.auth.domain.OnUserInit;
import com.project.blaze.auth.domain.ValidationResult;
import com.project.blaze.databinding.FragmentRegisterBinding;

import java.util.Objects;


public class RegisterFragment extends Fragment implements OnUserInit {

    private  static final String TAG = "RegisterFragment";
    public static final String USER_EXISTS = "The email address is already in use by another account.";
    private String email,password,username;
    private FragmentRegisterBinding binding;
    private NavController navController;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        binding.btnRegister.setOnClickListener(v-> checkForFormValidity());
    }
    private void checkForFormValidity() {
        email = Objects.requireNonNull(binding.edtRegEmail.getText()).toString();
        password = Objects.requireNonNull(binding.edtRegPass.getText()).toString();
        username = Objects.requireNonNull(binding.edtRegUsername.getText()).toString();

        String confirmPass = Objects.requireNonNull(binding.edtConfirmPass.getText()).toString();
        CheckUserValidity checkUserValidity = new CheckUserValidity(email,username, password, confirmPass);
        ValidationResult result = checkUserValidity.validate();
        binding.rEmail.setHelperText(result.getEmailError());
        binding.rUsername.setHelperText(result.getUsernameError());
        binding.rPass.setHelperText(result.getPasswordError());
        binding.rConfirmPass.setHelperText(result.getConfirmPassError());
        if(result.isSuccess()) {

            resetErrors();
            createAccount();
        }
        else Toast.makeText(requireActivity(), "Please check your credentials", Toast.LENGTH_SHORT).show();

    }

    private void resetErrors() {
        binding.rEmail.setHelperText("");
        binding.rUsername.setHelperText("");
        binding.rPass.setHelperText("");
        binding.rConfirmPass.setHelperText("");
    }

    private void createAccount()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            Toast.makeText(requireActivity(), "Successfully Registered!", Toast.LENGTH_SHORT).show();
            CreateUserProfile createUserProfile = new CreateUserProfile(this);
            createUserProfile.init(email,username);


        }).addOnFailureListener(e -> {
            Log.d(TAG, e.toString());
            if(Objects.equals(e.getMessage(), USER_EXISTS))
                Toast.makeText(requireActivity(), "Account already exists!", Toast.LENGTH_SHORT).show();

            else
                Toast.makeText(requireActivity(), "An error occurred", Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public void userRegistered() {
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }
}