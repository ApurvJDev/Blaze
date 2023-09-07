package com.project.blaze.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.project.blaze.databinding.FragmentQuestionBinding;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;
import com.project.blaze.home.repo.FlashcardRepo;


public class QuestionFragment extends Fragment implements FlashcardRepo.OnSuccessfulListener {

    private FlashCardViewModel flashCardViewModel;
    private FCardViewModel fCardViewModel;
    private FragmentQuestionBinding binding;
    private boolean isUpdate = false;
    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        flashCardViewModel.setOnSuccessfulListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuestionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);
        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);
        flashCardViewModel.setOnSuccessfulListener(this);

        flashCardViewModel.getIsUpdateLive().observe(getViewLifecycleOwner(), update -> isUpdate = update);

        fCardViewModel.getFlashcardLive().observe(getViewLifecycleOwner(), new Observer<FlashcardModel>() {
            @Override
            public void onChanged(FlashcardModel card) {
                if(card!=null)
                    binding.edtQuestion.setText(card.getQuestion());
            }
        });

        flashCardViewModel.actionSave().observe(getViewLifecycleOwner(), save -> {
            if(save) {
                updateOrCreateCard();
            }


        });
    }

    public void updateOrCreateCard()
    {
        flashCardViewModel.setQuestion(binding.edtQuestion.getText().toString());
        if(!flashCardViewModel.getAnswer().equals("") || !binding.edtQuestion.getText().toString().equals(""))
        {
            if(!isUpdate)
            {
                flashCardViewModel.createFlashcard();
            }
            else {
                fCardViewModel.updateFlashcard(flashCardViewModel.getFlashCard());
            }
        }
        else
        {
            Toast.makeText(requireActivity(), "Incomplete flashcard", Toast.LENGTH_SHORT).show();

        }
        flashCardViewModel.resetFlashcardSaved();
    }

    @Override
    public void onSuccess(boolean success) {
        if(success)
            Toast.makeText(requireActivity(), "Flashcard created successfully!!", Toast.LENGTH_SHORT).show();
    }


}