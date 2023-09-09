package com.project.blaze;

import static com.project.blaze.AppSetup.CHANNEL_1_ID;
import static com.project.blaze.home.presentation.ReviewFragment.FLASH_CARD;

import static com.project.blaze.home.helper.MyIntentBuilder.ANS;
import static com.project.blaze.home.helper.MyIntentBuilder.DECK_ID;
import static com.project.blaze.home.helper.MyIntentBuilder.EASE_FACTOR;
import static com.project.blaze.home.helper.MyIntentBuilder.GRADUATED;
import static com.project.blaze.home.helper.MyIntentBuilder.HAS_IMAGE;
import static com.project.blaze.home.helper.MyIntentBuilder.ID;
import static com.project.blaze.home.helper.MyIntentBuilder.MCQ;
import static com.project.blaze.home.helper.MyIntentBuilder.NEXT_REVIEW;
import static com.project.blaze.home.helper.MyIntentBuilder.OPTIONS;
import static com.project.blaze.home.helper.MyIntentBuilder.QUESTION;
import static com.project.blaze.home.helper.MyIntentBuilder.RECALL_ABILITY;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.helper.MyIntentBuilder;
import com.project.blaze.home.repo.ReviewRepo;

import java.util.HashMap;
import java.util.Map;

public class AlertReceiver extends BroadcastReceiver {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "AlertReceiver";
    private final ReviewRepo reviewRepo = new ReviewRepo();
    @Override
    public void onReceive(Context context, Intent i) {

       MyIntentBuilder intentBuilder = new MyIntentBuilder(context);
       FlashcardModel flashcard = intentBuilder.getFlashcardToSchedule(i);
        reviewRepo.addToQueue(flashcard);
        Log.d(TAG, "Received "+flashcard.getQuestion());

        showNotification(context);



    }

    private void showNotification(Context context) {
        Intent i = new Intent(context, AppMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setContentTitle("New card in queue!")
                .setContentText("New card added in queue")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        else
            notificationManager.notify(Integer.parseInt(String.valueOf(System.currentTimeMillis() % 10000)), notification);

    }
}
