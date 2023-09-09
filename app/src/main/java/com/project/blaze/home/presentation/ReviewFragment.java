package com.project.blaze.home.presentation;

import static com.project.blaze.queue.presentation.QueueFragment.MODIFY_INTERVAL;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blaze.GlideApp;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentReviewBinding;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.domain.ReviewViewModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.helper.MyIntentBuilder;
import com.project.blaze.home.repo.ReviewRepo;

import java.util.Calendar;

public class ReviewFragment extends Fragment implements ReviewRepo.OnScheduleListener {

   private FragmentReviewBinding binding;
   public static final String FLASH_CARD = "Flashcard";
   private NavController navController;
   private AlarmManager alarmManager;
   private FCardViewModel fCardViewModel;
   private FlashCardViewModel flashCardViewModel;
   private ReviewViewModel reviewViewModel;
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

   private boolean modifyInterval = true;

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
    public void onDetach() {
        super.onDetach();
        if(reviewViewModel!=null)
            reviewViewModel.setFlashcardFromQueueLive(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);

        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);

        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        reviewViewModel.setListener(this);

        if(getArguments()!=null)
        {
            modifyInterval = getArguments().getBoolean(MODIFY_INTERVAL);
        }
        reviewViewModel.setModifyInterval(modifyInterval);

        if(modifyInterval)
        {
            binding.editFlashcard.setVisibility(View.VISIBLE);
        }
        else {
            binding.editFlashcard.setVisibility(View.INVISIBLE);
        }
        setUpRating();

        //flashcard from queue
        reviewViewModel.getFlashcardFromQueueLive().observe(getViewLifecycleOwner(), new Observer<FlashcardModel>() {
            @Override
            public void onChanged(FlashcardModel card) {
                if(card!=null)
                {
                    Log.d(TAG, "Flashcard from queue");
                    flashcard = card;
                    populateViews();
                }
            }
        });

        //flashcard from flashcards fragment
        fCardViewModel.getFlashcardLive().observe(getViewLifecycleOwner(), card -> {
            if(card!=null)
            {
                Log.d(TAG, "Flashcard from flashcards fragment");
                flashcard = card;
                populateViews();
            }

        });


        binding.editFlashcard.setOnClickListener(v -> {
            flashCardViewModel.setUpdateLive(true);
            Bundle bundleNav = new Bundle();
            bundleNav.putBoolean("EDIT",true);
            navController.navigate(R.id.action_reviewFragment_to_cardCreationFragment2,bundleNav);
        });



        binding.txtShowHideAns.setOnClickListener(v -> {
            show = !show;
            if(show)
                showAnswer();
            else hideAnswer();
        });

        binding.fabDone.setOnClickListener(v -> {
            if(binding.txtRating.getVisibility() == View.VISIBLE)
            {
                setupReview();

            }
            else {
                Toast.makeText(requireActivity(), "Please review the card !", Toast.LENGTH_SHORT).show();
            }


        });




    }

    private void setupReview() {
        reviewViewModel.setEmail();
        reviewViewModel.setRecallRating(reviewRating);
        reviewViewModel.setDeckId(flashcard.getDeckId());
        reviewViewModel.beginReview(flashcard);
        reviewViewModel.setContext(requireActivity());
        if (!modifyInterval) {

            reviewViewModel.deleteFromQueue(flashcard);
            navController.navigate(R.id.action_reviewFragment_to_queueFragment);
        }
        else {
            navController.navigate(R.id.action_reviewFragment_to_flashcardsFragment);
        }

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
        reviewRating = switch (progress / 25) {
            case 0 -> AGAIN;
            case 1 -> HARD;
            case 2 -> GOOD;
            default -> EASY;
        };
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


    @Override
    public void onScheduleReady(Long nextInterval, boolean graduated) {
        FlashcardModel updatedFlashcard = reviewViewModel.getFlashcard();
        Log.d(TAG, "Scheduled "+ updatedFlashcard.getQuestion());
        if(!graduated)
        {
            Calendar c = Calendar.getInstance();
            int min = (int) (c.get(Calendar.MINUTE)+nextInterval);
            c.set(Calendar.MINUTE,min);
            c.set(Calendar.SECOND,0);
            startAlarm(c,updatedFlashcard);
        }
        else
        {
            // Calculate days and remaining minutes
            long days = nextInterval / 1440;
            long remainingMinutes = nextInterval % 1440;

            Calendar c = Calendar.getInstance();

            int day = c.get(Calendar.DAY_OF_MONTH) + (int) days;
            int hour = (int) (remainingMinutes / 60);
            int minute = (int) (remainingMinutes % 60);

            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            startAlarm(c,updatedFlashcard);
        }
    }


    private void startAlarm(Calendar c,FlashcardModel card) {
        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        MyIntentBuilder intentBuilder = new MyIntentBuilder(requireActivity());
        Intent intent = intentBuilder.buildIntentWithExtras(card);
        int pid = Integer.parseInt(String.valueOf(System.currentTimeMillis()%10000));
        card.setPid(pid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(),pid,intent,0);
        reviewViewModel.saveMetadata(card);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }


}