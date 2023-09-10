package com.project.blaze.global;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blaze.GlideApp;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentViewContentBinding;
import com.project.blaze.global.domain.GlobalViewModel;
import com.project.blaze.home.dto.FlashcardModel;

public class ViewContentFragment extends Fragment {


   private FragmentViewContentBinding binding;
   private NavController navController;
   private GlobalViewModel globalViewModel;
   private FlashcardModel flashcard;

    public ViewContentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewContentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        globalViewModel.getViewFlashcardLive().observe(getViewLifecycleOwner(), new Observer<FlashcardModel>() {
            @Override
            public void onChanged(FlashcardModel card) {
                if(card!=null)
                {
                    flashcard = card;
                    populateViews();

                }
            }
        });

    }

    private void populateViews() {
        binding.txtQes.setText(flashcard.getQuestion());

        if(flashcard.isMcq())
        {
            showOptions(flashcard.getOptionsList());
        }
        showAnswer();
    }
    private void showAnswer() {

        if(flashcard.isHasImage())
        {
            binding.imgAns.setVisibility(View.VISIBLE);

            GlideApp.with(this)
                    .load(globalViewModel.getImage(flashcard))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgAns);
            binding.imgAns.setVisibility(View.VISIBLE);
        }
        binding.txtAns.setVisibility(View.VISIBLE);
        if(flashcard.isMcq())
        {
            String ans = "The correct answer is: " + flashcard.getAnswer();
            binding.txtAns.setText(ans);

        }
        else binding.txtAns.setText(flashcard.getAnswer());

    }

    private void showOptions(String optionsList) {
        String[] words = optionsList.split(",");

        for (String word : words) {
            // Remove "#" prefix if present
            if (word.startsWith("#")) {
                word = word.substring(1); // Remove the "#" prefix
            }

            // Create a new radio button
            RadioButton option = new RadioButton(requireActivity());
            option.setText(word);
            int[][] states = {{android.R.attr.state_checked}, {}};
            int[] colors = {ContextCompat.getColor(requireActivity(),R.color.purple), ContextCompat.getColor(requireActivity(),R.color.black)}; // Checked and unchecked color (same in this example)
            CompoundButtonCompat.setButtonTintList(option, new ColorStateList(states, colors));
            // Add the radio button to the radio group
            binding.radioOptions.addView(option);
            binding.radioOptions.setVisibility(View.VISIBLE);
        }
    }

}