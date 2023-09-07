package com.project.blaze.home.presentation;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.DeckRepo.DECKS;
import static com.project.blaze.home.repo.FlashcardRepo.FLASHCARDS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentFlashcardsBinding;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.presentation.adapters.DecksAdapter;
import com.project.blaze.home.presentation.adapters.FlashcardAdapter;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;

import java.util.Objects;


public class FlashcardsFragment extends Fragment implements FlashcardAdapter.FlashCardClickListener, FlashCardRetrieveRepo.OnFlashcardRetrievedListener {

    private NavController navController;
    public static final String TAG = "FlashcardsFragment";
    public static final String UPDATE_NAV = "Update navigation";
    private FragmentFlashcardsBinding binding;

    private FlashcardAdapter adapter;
    private String deckId;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FCardViewModel fCardViewModel;
    private FlashCardViewModel flashCardViewModel;

    public FlashcardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFlashcardsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);
        fCardViewModel.setListener(this);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);
        deckId = fCardViewModel.getDeckId();
        setUpRV();
        binding.fbCreateFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // indicate that the flashcard is being created
                flashCardViewModel.setUpdateLive(false);
                navController.navigate(R.id.action_flashcardsFragment_to_cardCreationFragment);

            }
        });
    }

    public void setUpRV(){
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert email != null;
        CollectionReference flashcardsRef  = db.collection(USERS).document(email).collection(DECKS).document(deckId)
                .collection(FLASHCARDS);
        FirestoreRecyclerOptions<FlashcardModel> options = new FirestoreRecyclerOptions.Builder<FlashcardModel>()
                .setQuery(flashcardsRef.orderBy("question"),FlashcardModel.class)
                .build();
        Log.d(TAG, flashcardsRef.toString());

        adapter = new FlashcardAdapter(options);
        adapter.setListener(this);
        binding.rvFlashcards.setAdapter(adapter);
        binding.rvFlashcards.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvFlashcards.setHasFixedSize(true);
        adapter.startListening();

    }

    @Override
    public void onCardClick(DocumentSnapshot snapshot) {

    }

    @Override
    public void onEditClick(DocumentSnapshot snapshot) {
        // indicate that the flashcard is being updated
        fCardViewModel.setEmail();
        flashCardViewModel.setUpdateLive(true);

        fCardViewModel.setFlashcardId(snapshot.getId());
        fCardViewModel.retrieveFlashcard();


    }

    @Override
    public void onRetrieved(FlashcardModel card) {
        fCardViewModel.setFlashcardLive(card);
        navController.navigate(R.id.action_flashcardsFragment_to_cardCreationFragment);
    }

    @Override
    public void onUpdate(boolean success) {
        Toast.makeText(requireActivity(), "Flashcard Updated!", Toast.LENGTH_SHORT).show();
    }
}