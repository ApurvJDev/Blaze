package com.project.blaze.global;

import static com.project.blaze.global.repo.GlobalRepo.GLOBAL;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.blaze.R;
import com.project.blaze.databinding.FragmentGlobalBinding;
import com.project.blaze.global.domain.GlobalViewModel;
import com.project.blaze.global.presentation.adapters.GlobalDeckAdapter;
import com.project.blaze.home.dto.DeckModel;

import java.util.Objects;


public class GlobalFragment extends Fragment implements GlobalDeckAdapter.OnGlobalDeckClickListener {


    private FragmentGlobalBinding binding;
    public static final String TAG = "GlobalFragment";
    private NavController navController;
    private GlobalDeckAdapter adapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GlobalViewModel globalViewModel;

    public GlobalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.edtSearch.setText("");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGlobalBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.main_navHost_fragment);
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        globalViewModel.setEmail();



        binding.edtSearch.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                //search for deck
                String searchSeq = binding.edtSearch.getText().toString().toLowerCase().trim();
                if(!searchSeq.equals("") )
                {
                    setUpRv(searchSeq);
                }
                else Toast.makeText(requireActivity(), "Empty search ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

    }

    private void setUpRv(String searchSeq) {
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert email != null;
        CollectionReference globalDeckCollection  = db.collection(GLOBAL);
        Query query = globalDeckCollection.whereGreaterThanOrEqualTo("deckName", searchSeq)
                .whereLessThanOrEqualTo("deckName", searchSeq + "\uf8ff").orderBy("deckName");


        FirestoreRecyclerOptions<DeckModel> options = new FirestoreRecyclerOptions.Builder<DeckModel>()
                .setQuery(query,DeckModel.class)
                .build();

        adapter = new GlobalDeckAdapter(options);
        adapter.setListener(this);
        binding.rvGlobalDecks.setAdapter(adapter);
        binding.rvGlobalDecks.setLayoutManager(new GridLayoutManager(requireActivity(),2));
        binding.rvGlobalDecks.setHasFixedSize(true);
        adapter.startListening();
    }

    @Override
    public void onGlobalDeckClick(DocumentSnapshot snapshot) {
        globalViewModel.setViewDeckModelLive(snapshot.toObject(DeckModel.class));
        navController.navigate(R.id.action_globalFragment_to_viewFlashcardsFragment);
    }

    @Override
    public void onDeckImport(DocumentSnapshot snapshot) {
        globalViewModel.importDeck(snapshot.toObject(DeckModel.class));
    }
}