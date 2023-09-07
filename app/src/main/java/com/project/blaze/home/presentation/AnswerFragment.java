package com.project.blaze.home.presentation;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blaze.GlideApp;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentAnswerBinding;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;
import com.project.blaze.home.repo.FlashcardRepo;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;


public class AnswerFragment extends Fragment  {


    private FragmentAnswerBinding binding;
    public static final String TAG = "AnswerFragment";
    private NavController navController;
    private FlashCardViewModel flashCardViewModel;
    private FCardViewModel fCardViewModel;

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
        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);

        fCardViewModel.getFlashcardLive().observe(getViewLifecycleOwner(), new Observer<FlashcardModel>() {
            @Override
            public void onChanged(FlashcardModel card) {
                if(card!=null)
                    populateViews(card);
            }
        });

        flashCardViewModel.getLiveImgUri().observe(getViewLifecycleOwner(), uri -> {
            // restore image state
            if(uri != null)
            {
                flashCardViewModel.setImage();
                binding.imgAns.setVisibility(View.VISIBLE);
                Picasso.get().load(uri).into(binding.imgAns);


            }
            fCardViewModel.setImageUri(uri);


        });



        binding.fbAction.setOnClickListener(v -> initiateAction());

        binding.fbAddImage.setOnClickListener(v ->{
            mGetContent.launch("image/*");

        });

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
                if(binding.edtAnswer.getSelectionStart()==0 && binding.imgAns.getVisibility() == View.VISIBLE)
                {
                    binding.imgAns.setImageDrawable(null);
                    flashCardViewModel.setHasImage(false);
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
                    correctOption = word.substring(1);// remove first character "#"
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




    // when its update the populate the views
    private void populateViews(FlashcardModel card) {
        binding.mcqSwitch.setChecked(card.isMcq());
        if(card.isMcq())binding.edtAnswer.setText(card.getOptionsList());
        else binding.edtAnswer.setText(card.getAnswer());
        if(card.isHasImage())
        {
            flashCardViewModel.setLiveImgUri(null);
            Log.d(TAG, fCardViewModel.getImage().toString());
            //show image using glide and skip caching
            GlideApp.with(this)
                    .load(fCardViewModel.getImage())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imgAns);
            binding.imgAns.setVisibility(View.VISIBLE);

        }

    }
}