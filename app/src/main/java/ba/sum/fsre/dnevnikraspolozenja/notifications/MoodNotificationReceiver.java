package ba.sum.fsre.dnevnikraspolozenja.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ba.sum.fsre.dnevnikraspolozenja.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ba.sum.fsre.dnevnikraspolozenja.activities.DashboardActivity;

public class MoodNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "mood_notifications";

    // Poruke po satima
    private static final String[] morningMessages = {
            "Dobro jutro! Kako se osjećaš danas? ☀️",

            "Vrijeme je za jutarnji check-in! 📝",

            "Hej! Podijeli svoje raspoloženje za početak dana 😃",

            "Nova prilika, novo raspoloženje! 🌈",

            "Jutarnja energija! Kako je tvoje raspoloženje? 💛",

            "Hej, zabilježi svoje misli! 💭",

            "Vrijeme je za kratki dnevni update! ✍️",

            "Početak dana, osjećaš li se dobro? 😊",

            "Započni dan unosom raspoloženja! 💡",

            "Tvoj dnevni check-in je spreman! 🕗"
    };

    private static final String[] afternoonMessages = {
            "Hej! Kako ide dan? 🌞",

            "Vrijeme za poslijepodnevni check-in! 📝",

            "Podijeli svoje raspoloženje ovog popodneva! 😃",

            "Kako ti ide dan? 🌈",

            "Ne zaboravi unos raspoloženja! ⏰",

            "Jesi li već zabilježio/la svoje misli? 💭",

            "Vrijeme je za kratku pauzu i update! ✍️",

            "Tvoj popodnevni check-in je ovdje! 😊",

            "Kako se osjećaš sada? 💡",

            "Osvježi svoj dan unosom raspoloženja! 🕑"
    };

    private static final String[] eveningMessages = {
            "Večer je stigla! Kako se osjećaš? 🌙",

            "Vrijeme je za večernji check-in! 📝",

            "Podijeli svoje raspoloženje prije spavanja 😴",

            "Kako je prošao tvoj dan? 🌈",

            "Zabilježi svoje misli i osjećaje! ⏰",

            "Hej, vrijeme je za kraći dnevni update! 💭",

            "Tvoj večernji check-in čeka! ✍️",

            "Vrijeme je da zatvoriš dan unosom raspoloženja 😊",

            "Pripremi se za sutra s kratkim pregledom dana 💡",

            "Kako se osjećaš prije spavanja? 🌟"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("mood_prefs", Context.MODE_PRIVATE);
        String lastMoodDate = prefs.getString("last_mood_date", null);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        if (today.equals(lastMoodDate)) {
            // Mood je već unesen danas → ne šaljemo notifikaciju
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Mood Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        int hour = intent.getIntExtra("hour", 8);
        String message = getRandomMessage(hour);

        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, hour, dashboardIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Dnevnik raspoloženja")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private String getRandomMessage(int hour) {
        Random random = new Random();
        if (hour == 8) {
            return morningMessages[random.nextInt(morningMessages.length)];
        } else if (hour == 14) {
            return afternoonMessages[random.nextInt(afternoonMessages.length)];
        } else if (hour == 18) {
            return eveningMessages[random.nextInt(eveningMessages.length)];
        }
        else {
            String[] fallback = {"Vrijeme je za unos raspoloženja!"};
            return fallback[0];
        }
    }
}
