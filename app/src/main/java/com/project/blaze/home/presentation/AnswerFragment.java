package com.project.blaze.home.presentation;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.project.blaze.R;
import com.project.blaze.databinding.FragmentAnswerBinding;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class AnswerFragment extends Fragment {


    private FragmentAnswerBinding binding;
    private NavController navController;
    private FlashCardViewModel flashCardViewModel;
    private boolean save = false;

    public AnswerFragment() {
        // Required empty public constructor
    }
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if(uri!=null)
                    {
                        flashCardViewModel.setLiveImgUri(uri);
                    }

                }
            });

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

        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);

        flashCardViewModel.getLiveImgUri().observe(requireActivity(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                // restore image state
                if(uri != null)
                {
                    save = true;
                    flashCardViewModel.setImage();
                    binding.imgAns.setVisibility(View.VISIBLE);
                    Picasso.get().load(uri).into(binding.imgAns);
                    binding.fbAddImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.baseline_save_24, requireContext().getTheme()));
                    Toast.makeText(requireActivity(), "Save ready", Toast.LENGTH_SHORT).show();

                }
                else {


                    binding.fbAddImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.baseline_photo_camera_back_24, requireContext().getTheme()));
                }

            }
        });

        flashCardViewModel.getIsSuccess().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(requireActivity(), "Flashcard created!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.fbAddImage.setOnClickListener(v -> {
            if(!save)
                mGetContent.launch("image/*");
            else {
                boolean isMCQ = binding.mcqSwitch.isChecked();
                flashCardViewModel.setMCQ(isMCQ);
                if(isMCQ)
                {
                    flashCardViewModel.setMcqOptions(binding.edtAnswer.getText().toString());
                }
                else {
                    flashCardViewModel.setAnswer(binding.edtAnswer.getText().toString());
                }
                flashCardViewModel.setFlashcardSaved();
                Toast.makeText(requireActivity(), "Action Save", Toast.LENGTH_SHORT).show();
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

//    private String getFileExtension(Uri uri)
//    {
//        ContentResolver cR = requireActivity().getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cR.getType(uri));
//    }




}