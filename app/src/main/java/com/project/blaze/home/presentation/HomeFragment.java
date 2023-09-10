package com.project.blaze.home.presentation;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.DeckRepo.DECKS;

import android.content.ContentResolver;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.project.blaze.databinding.FragmentHomeBinding;
import com.project.blaze.global.domain.GlobalViewModel;
import com.project.blaze.home.domain.FCardViewModel;
import com.project.blaze.home.domain.FlashCardViewModel;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.presentation.adapters.DecksAdapter;
import com.project.blaze.home.presentation.dialogs.AddDeckDialog;

import java.util.Objects;


public class HomeFragment extends Fragment implements DecksAdapter.DeckClickListener {


    public static final String TAG = "HomeFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FragmentHomeBinding binding;
    private NavController navController;

    private DecksAdapter adapter;
    private FlashCardViewModel flashCardViewModel;
    private FCardViewModel fCardViewModel;
    private GlobalViewModel globalViewModel;


    public HomeFragment() {
        // Required empty public constructor
    }

//    private final ActivityResultLauncher<String> mGetPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
//            new ActivityResultCallback<Boolean>() {
//        @Override
//        public void onActivityResult(Boolean result) {
//            if(!result)
//            {
//                Toast.makeText(requireActivity(), "Please enable notifications ", Toast.LENGTH_SHORT).show();
//            }
//        }
//    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ask for notification request

        navController = Navigation.findNavController(requireActivity(), R.id.main_navHost_fragment);
        flashCardViewModel = new ViewModelProvider(requireActivity()).get(FlashCardViewModel.class);
        fCardViewModel = new ViewModelProvider(requireActivity()).get(FCardViewModel.class);

        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        globalViewModel.setEmail();


        binding.fbAddDeck.setOnClickListener(v -> {
            AddDeckDialog creationDialog = new AddDeckDialog();
            creationDialog.show(requireActivity().getSupportFragmentManager(),"Create and Add Deck");
        });
        setUpRV();
    }

    public void setUpRV(){
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert email != null;
        CollectionReference deckListRef  = db.collection(USERS).document(email).collection(DECKS);
        FirestoreRecyclerOptions<DeckModel> options = new FirestoreRecyclerOptions.Builder<DeckModel>()
                .setQuery(deckListRef.orderBy("deckName"),DeckModel.class)
                .build();
        Log.d(TAG, deckListRef.toString());

        adapter = new DecksAdapter(options);
        adapter.setListener(this);
        binding.rvDeckList.setAdapter(adapter);
        binding.rvDeckList.setLayoutManager(new GridLayoutManager(requireActivity(),2));
        binding.rvDeckList.setHasFixedSize(true);
        adapter.startListening();

    }

    @Override
    public void onDeckClick(DocumentSnapshot snapshot) {
        flashCardViewModel.setDeckId(snapshot.getId());
        fCardViewModel.setDeckId(snapshot.getId());
        navController.navigate(R.id.action_homeFragment_to_flashcardsFragment);
    }

    @Override
    public void onPublishDeck(DocumentSnapshot snapshot) {

        DeckModel deckModel = snapshot.toObject(DeckModel.class);
        assert deckModel != null;
        if(deckModel.getFlashcardCount() > 0)
        {
            globalViewModel.publishAndGlobalise(snapshot.toObject(DeckModel.class));

        }
        else Toast.makeText(requireActivity(), "Add flashcards first", Toast.LENGTH_SHORT).show();


    }
}