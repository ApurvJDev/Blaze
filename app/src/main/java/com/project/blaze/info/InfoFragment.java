package com.project.blaze.info;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.project.blaze.AuthActivity;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentInfoBinding;


public class InfoFragment extends Fragment {


    private FragmentInfoBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentInfoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(v ->{

            startActivity(new Intent(requireActivity(), AuthActivity.class));
            mAuth.signOut();
            requireActivity().finish();

        });
    }
}