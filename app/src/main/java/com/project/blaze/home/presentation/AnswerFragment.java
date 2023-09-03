package com.project.blaze.home.presentation;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.blaze.R;
import com.project.blaze.databinding.FragmentAnswerBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class AnswerFragment extends Fragment {


    private FragmentAnswerBinding binding;
    private NavController navController;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    public AnswerFragment() {
        // Required empty public constructor
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
        binding.fbAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        binding.imgAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show();
            }
        });

        binding.edtAnswer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyCode == KeyEvent.KEYCODE_DEL)
                {
                    if(Objects.equals(binding.edtAnswer.getText().toString(), "") && binding.imgAns.getVisibility() == View.VISIBLE)
                    {
                        binding.imgAns.setImageDrawable(null);
                    }
                    return true;
                }

                return false;
            }
        });
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null)
        {
            mImageUri = data.getData();
            binding.imgAns.setVisibility(View.VISIBLE);
            Picasso.get().load(mImageUri).into(binding.imgAns);
        }
    }
}