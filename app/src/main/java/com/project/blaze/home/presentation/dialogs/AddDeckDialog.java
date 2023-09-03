package com.project.blaze.home.presentation.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.project.blaze.R;

public class AddDeckDialog extends DialogFragment {
    public static final String UNTITLED = "Untitled";
    private DeckCreatedListener listener;
    public static final String TAG = "AddDeckDialog";

    public AddDeckDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view  = inflater.inflate(R.layout.add_deck_dialog,null);

        TextInputEditText edtDeckName = view.findViewById(R.id.edt_name);
        builder.setView(view)
                .setTitle("Create new Deck ")
                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                })
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    if(edtDeckName.getText().toString().trim().equals(""))
                    {
                        listener.onDeckCreated(UNTITLED);
                    }
                    else {
                        listener.onDeckCreated(edtDeckName.getText().toString());
                    }

                });
        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darker_purple));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darker_purple));
            }
        });

        return alertDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeckCreatedListener) context;
        }
        catch (ClassCastException e){
            Log.d(TAG, context +" must implement interface");
        }
    }

    public interface DeckCreatedListener{
        public void onDeckCreated(String deckName);
    }
}
