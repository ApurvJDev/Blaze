package com.project.blaze.home.presentation;

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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blaze.GlideApp;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentReviewBinding;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;

public class ReviewFragment extends Fragment {

   private FragmentReviewBinding binding;
   private NavController navController;
   private FCardViewModel fCardViewModel;
   private FlashCardViewModel flashCardViewModel;
   private FlashcardModel flashcard;
   private boolean show = false;
   private String reviewRating = null;
   public static final String TAG = "ReviewFragment";
   public static final String HIDE = "Hide the answer";
   public static final String SHOW = "Show the answer";
   public static final String AGAIN = "Again";
   public static final String HARD = "Hard";
   public static final String GOOD = "Good";
   public static final String EASY = "Easy";

    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = FragmentReviewBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);
        setUpRating();


        binding.editFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashCardViewModel.setUpdateLive(true);
                navController.navigate(R.id.action_reviewFragment_to_cardCreationFragment2);
            }
        });

        fCardViewModel.getFlashcardLive().observe(getViewLifecycleOwner(), new Observer<FlashcardModel>() {
            @Override
            public void onChanged(FlashcardModel card) {
                flashcard = card;
                populateViews();
            }
        });

        binding.txtShowHideAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show = !show;
                if(show)
                    showAnswer();
                else hideAnswer();
            }
        });

        binding.fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(), reviewRating, Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void setUpRating() {
        binding.seekRating.setMax(100);
        binding.seekRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                setReviewRating(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setReviewRating(int progress) {
        String tag;
        switch (progress / 25) {
            case 0:
                tag = AGAIN;
                break;
            case 1:
                tag = HARD;
                break;
            case 2:
                tag = GOOD;
                break;
            default:
                tag = EASY;
                break;
        }
        reviewRating = tag;
        String rating = "Rating is: "+reviewRating;
        binding.txtRating.setVisibility(View.VISIBLE);
        binding.txtRating.setText(rating);
    }


    private void hideAnswer() {
        binding.imgAns.setVisibility(View.GONE);
        binding.txtAns.setVisibility(View.GONE);
        binding.txtShowHideAns.setText(SHOW);
    }

    private void showAnswer() {

        binding.txtShowHideAns.setText(HIDE);
        if(flashcard.isHasImage())
        {
            binding.imgAns.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .load(fCardViewModel.getImage())
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


    private void populateViews() {
        binding.txtQes.setText(flashcard.getQuestion());

        if(flashcard.isMcq())
        {
            showOptions(flashcard.getOptionsList());
        }
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