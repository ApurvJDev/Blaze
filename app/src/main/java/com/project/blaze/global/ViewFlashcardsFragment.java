package com.project.blaze.global;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.global.repo.GlobalRepo.PUBLISHED;
import static com.project.blaze.home.repo.DeckRepo.DECKS;
import static com.project.blaze.home.repo.FlashcardRepo.FLASHCARDS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentViewFlashcardsBinding;
import com.project.blaze.global.domain.GlobalViewModel;
import com.project.blaze.global.presentation.adapters.ViewFlashcardsAdapter;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.presentation.adapters.FlashcardAdapter;

import java.util.Objects;

public class ViewFlashcardsFragment extends Fragment implements ViewFlashcardsAdapter.OnViewFCardClick {

   private FragmentViewFlashcardsBinding binding;
   public static final String TAG = "ViewFlashcardsFragment";
   private NavController navController;
   private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
   private final FirebaseFirestore db = FirebaseFirestore.getInstance();
   private ViewFlashcardsAdapter adapter;
   private GlobalViewModel globalViewModel;


    public ViewFlashcardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            {
                adapter.stopListening();
                globalViewModel.setViewDeckModelLive(null);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewFlashcardsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        globalViewModel.getViewDeckModelLive().observe(getViewLifecycleOwner(), new Observer<DeckModel>() {
            @Override
            public void onChanged(DeckModel deckModel) {
                if(deckModel!=null)
                    setUpRv(deckModel);
                assert deckModel != null;
                globalViewModel.setCreatorEmail(deckModel.getEmail());
            }
        });

    }

    private void setUpRv(DeckModel deckModel) {
        CollectionReference flashcardsRef  = db.collection(USERS).document(deckModel.getEmail()).collection(PUBLISHED).document(deckModel.getDeckId()).collection(FLASHCARDS);
        FirestoreRecyclerOptions<FlashcardModel> options = new FirestoreRecyclerOptions.Builder<FlashcardModel>()
                .setQuery(flashcardsRef.orderBy("question"),FlashcardModel.class)
                .build();
        Log.d(TAG, flashcardsRef.toString());

        adapter = new ViewFlashcardsAdapter(options);
        adapter.setListener(this);
        binding.rvFlashcards.setAdapter(adapter);
        binding.rvFlashcards.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvFlashcards.setHasFixedSize(true);
        adapter.startListening();
    }

    @Override
    public void onViewFCardClicked(DocumentSnapshot snapshot) {

        globalViewModel.setViewFlashcardLive(snapshot.toObject(FlashcardModel.class));
        navController.navigate(R.id.action_viewFlashcardsFragment_to_viewContentFragment);
    }
}