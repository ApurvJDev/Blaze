package com.project.blaze.queue.presentation;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.ReviewRepo.QUEUE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentQueueBinding;
import com.project.blaze.home.domain.ReviewViewModel;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.queue.presentation.adapters.QueueAdapter;

import java.util.Objects;

public class QueueFragment extends Fragment implements QueueAdapter.OnFlashCardClickListener {

    private FragmentQueueBinding binding;
    public static final String TAG =  "QueueFragment";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;
    private QueueAdapter adapter;
    public static final String MODIFY_INTERVAL = "Modify Interval";
    private ReviewViewModel reviewViewModel;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQueueBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(),R.id.main_navHost_fragment);
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        setUpRV();

    }

    public void setUpRV(){
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert email != null;
        CollectionReference flashcardsRef  = db.collection(USERS).document(email).collection(QUEUE);
        FirestoreRecyclerOptions<FlashcardModel> options = new FirestoreRecyclerOptions.Builder<FlashcardModel>()
                .setQuery(flashcardsRef.orderBy("question"),FlashcardModel.class)
                .build();
        Log.d(TAG, flashcardsRef.toString());

        adapter = new QueueAdapter(options);
        adapter.setListener(this);
        binding.rvFlashcards.setAdapter(adapter);
        binding.rvFlashcards.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvFlashcards.setHasFixedSize(true);
        adapter.startListening();

    }

    @Override
    public void onFlashCardClick(DocumentSnapshot documentSnapshot) {
        //navigate to review fragment
        reviewViewModel.setFlashcardFromQueueLive(documentSnapshot.toObject(FlashcardModel.class));
        Toast.makeText(requireActivity(), Objects.requireNonNull(documentSnapshot.toObject(FlashcardModel.class)).getQuestion(), Toast.LENGTH_SHORT).show();
        Bundle bundleNav = new Bundle();
        bundleNav.putBoolean(MODIFY_INTERVAL,false);
        navController.navigate(R.id.action_queueFragment_to_reviewFragment,bundleNav);
    }
}