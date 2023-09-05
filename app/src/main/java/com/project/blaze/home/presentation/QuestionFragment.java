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
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.repo.FlashcardRepo;


public class QuestionFragment extends Fragment implements FlashcardRepo.OnSuccessfulListener {

    private FlashCardViewModel flashCardViewModel;
    private FragmentQuestionBinding binding;
    public QuestionFragment() {
        // Required empty public constructor
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
        flashCardViewModel.setOnSuccessfulListener(this);
        flashCardViewModel.actionSave().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean save) {
                if(save)
                {

                    flashCardViewModel.setQuestion(binding.edtQuestion.getText().toString());
                    if(!flashCardViewModel.getAnswer().equals("") || !binding.edtQuestion.getText().toString().equals(""))
                    {
                        flashCardViewModel.createFlashcard();
                    }
                    else
                    {
                        Toast.makeText(requireActivity(), "Incomplete flashcard", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    @Override
    public void onSuccess(boolean success) {
        Toast.makeText(requireActivity(), "Flashcard created successfully!!", Toast.LENGTH_SHORT).show();
    }
}