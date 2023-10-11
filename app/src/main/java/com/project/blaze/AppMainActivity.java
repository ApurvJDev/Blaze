package com.project.blaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.blaze.databinding.ActivityAppMainBinding;
import com.project.blaze.global.domain.GlobalViewModel;
import com.project.blaze.global.repo.GlobalRepo;
import com.project.blaze.home.domain.DeckViewModel;
import com.project.blaze.home.presentation.CardCreationFragment;
import com.project.blaze.home.presentation.ReviewFragment;
import com.project.blaze.home.presentation.dialogs.AddDeckDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class AppMainActivity extends AppCompatActivity implements AddDeckDialog.DeckCreatedListener, GlobalRepo.OnDeckImportedListener, GlobalRepo.OnDeckGlobalisedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private NavController navController;

    private ActivityAppMainBinding binding;
    private boolean popStack = false;
    private DeckViewModel deckViewModel;
    public static final String TAG = "AppMainActivity";
    private GlobalViewModel globalViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        globalViewModel = new ViewModelProvider(this).get(GlobalViewModel.class);
        globalViewModel.setListeners(this,this);

        //set up bottom navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_navHost_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            NavigationUI.onNavDestinationSelected(item,navController);
            return true;
        });

        ///
        deckViewModel =  new ViewModelProvider(this).get(DeckViewModel.class);
    }

    @Override
    protected void onStart() {

        Log.d(TAG, "null");
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null)
        {
            Log.d(TAG, "null");
            startActivity(new Intent(AppMainActivity.this, AuthActivity.class));
            finish();
        }
        super.onStart();

    }

    @Override
    public void onDeckCreated(String deckName) {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String date = sdf.format(currentDate);
        int count = 0;
        deckViewModel.createDeck(deckName,date,count);
        Toast.makeText(this, "Deck created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeckGlobalisedListener(boolean success) {
        if(success) Toast.makeText(this, "Deck published!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Unable to publish deck", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeckImportedListener(boolean success) {
        if(success) Toast.makeText(this, "Deck imported!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Unable to import deck", Toast.LENGTH_SHORT).show();
    }
}