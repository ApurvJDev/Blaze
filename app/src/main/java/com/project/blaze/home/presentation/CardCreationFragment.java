package com.project.blaze.home.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentCardCreationBinding;
import com.project.blaze.home.presentation.adapters.ViewPagerAdapter;

import java.util.Objects;


public class CardCreationFragment extends Fragment {

private FragmentCardCreationBinding binding;
private NavController navController;
private ViewPagerAdapter viewPagerAdapter;

    public CardCreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCardCreationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);

        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager2.setAdapter(viewPagerAdapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                    case 1:
                        Objects.requireNonNull(binding.tabLayout.getTabAt(position)).select();

                }
                super.onPageSelected(position);
            }
        });
    }



}