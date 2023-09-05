package com.project.blaze.home.presentation;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.project.blaze.R;
import com.project.blaze.databinding.FragmentAnswerBinding;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.repo.FlashcardRepo;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class AnswerFragment extends Fragment implements FlashcardRepo.OnSuccessfulListener {


    private FragmentAnswerBinding binding;
    private NavController navController;
    private FlashCardViewModel flashCardViewModel;

    //anim
    private  Animation rotateOpen;
    private  Animation rotateClose;
    private  Animation fromBottom;
    private  Animation toBottom;

    private boolean actionClicked = false;
    private String correctOption; // correctOption is the word followed by "#"

    public AnswerFragment() {
        // Required empty public constructor
    }

    // Intent for image picking
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                        flashCardViewModel.setLiveImgUri(uri);

                    }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //reset the image uri to null on detach
        flashCardViewModel.setLiveImgUri(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.fbAddImage.setVisibility(View.INVISIBLE);
        binding.fbSave.setVisibility(View.INVISIBLE);
        actionClicked = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnswerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rotateOpen = AnimationUtils.loadAnimation(requireActivity(),R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(requireActivity(),R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(requireActivity(),R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(requireActivity(),R.anim.to_bottom_anim);

        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);

        flashCardViewModel.getLiveImgUri().observe(getViewLifecycleOwner(), uri -> {
            // restore image state
            if(uri != null)
            {
                flashCardViewModel.setImage();
                binding.imgAns.setVisibility(View.VISIBLE);
                Picasso.get().load(uri).into(binding.imgAns);

            }

        });



        binding.fbAction.setOnClickListener(v -> initiateAction());

        binding.fbAddImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        binding.fbSave.setOnClickListener(v->{
            boolean isMCQ = binding.mcqSwitch.isChecked();
            flashCardViewModel.setMCQ(isMCQ);
            if(isMCQ)
            {
                flashCardViewModel.setMcqOptions(binding.edtAnswer.getText().toString());
                if(findAnswer(binding.edtAnswer.getText().toString()))
                {
                    // if correct format
                    flashCardViewModel.setAnswer(correctOption);
                    flashCardViewModel.setFlashcardSaved();
                }
                else {
                    Toast.makeText(requireActivity(), "Invalid Format", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                flashCardViewModel.setAnswer(binding.edtAnswer.getText().toString());
                flashCardViewModel.setFlashcardSaved();
            }

        });

        binding.imgAns.setOnClickListener(v -> Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show());

        binding.edtAnswer.setOnKeyListener((v, keyCode, keyEvent) -> {

            if(keyCode == KeyEvent.KEYCODE_DEL)
            {
                if(Objects.equals(binding.edtAnswer.getText().toString(), "") && binding.imgAns.getVisibility() == View.VISIBLE)
                {
                    binding.imgAns.setImageDrawable(null);
                    flashCardViewModel.setLiveImgUri(null);
                    binding.imgAns.setVisibility(View.GONE);

                }
                return true;
            }

            return false;
        });



    }

    private boolean findAnswer(String input) {
        // Split the input string into words using comma as the delimiter
        String[] words = input.split(",");

        // Initialize a flag to keep track of whether a "#" prefix is found
        boolean foundHashPrefix = false;

        // Iterate through the words and check if one of them has "#" prefix
        for (String word : words) {
            word = word.trim(); // Remove leading and trailing spaces
            if (word.startsWith("#")) {
                // If a "#" prefix is found and it's the first one, set the flag
                if (!foundHashPrefix) {
                    correctOption = word;
                    foundHashPrefix = true;
                } else {
                    // If more than one "#" prefix is found, the format is invalid
                    correctOption = "";
                    return false;
                }
            }
        }

        // Check if at least one "#" prefix was found and there are no other issues
        return foundHashPrefix;

    }


    private void initiateAction() {
        actionClicked = !actionClicked;
        setAnimation(actionClicked);
        setVisibility(actionClicked);

    }

    private void setVisibility(boolean actionClicked) {
        if(actionClicked)
        {
            binding.fbAddImage.setVisibility(View.VISIBLE);
            binding.fbSave.setVisibility(View.VISIBLE);

        }
        else
        {
            binding.fbAddImage.setVisibility(View.INVISIBLE);
            binding.fbSave.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean actionClicked) {
        if(actionClicked)
        {
            binding.fbAction.startAnimation(rotateOpen);
            binding.fbSave.startAnimation(fromBottom);
            binding.fbAddImage.startAnimation(fromBottom);
        }
        else {
            binding.fbAction.startAnimation(rotateClose);
            binding.fbSave.startAnimation(toBottom);
            binding.fbAddImage.startAnimation(toBottom);
        }
    }


    @Override
    public void onSuccess(boolean success) {
        Toast.makeText(requireActivity(), "Flashcard created!!", Toast.LENGTH_SHORT).show();
    }
}