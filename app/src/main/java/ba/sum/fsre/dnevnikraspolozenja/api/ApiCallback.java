package ba.sum.fsre.dnevnikraspolozenja.api;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    private static final String TAG = "ApiCallback";

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Log.d(TAG, "==================== API RESPONSE ====================");
        Log.d(TAG, "URL: " + call.request().url());
        Log.d(TAG, "Method: " + call.request().method());
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Response Message: " + response.message());

        // Prihvati 200-299 kao success (uključujući 204)
        if (response.isSuccessful()) {
            Log.d(TAG, "SUCCESS - Code: " + response.code());
            onSuccess(response.body());  // body može biti null za 204
        } else {
            // Error handling
            String errorBody = "Unknown error";
            try {
                if (response.errorBody() != null) {
                    errorBody = response.errorBody().string();
                    Log.e(TAG, "Error Body: " + errorBody);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to parse error body", e);
            }

            String errorMessage = String.format(
                    "HTTP %d: %s\nDetails: %s",
                    response.code(),
                    response.message(),
                    errorBody
            );

            Log.e(TAG, "FULL ERROR: " + errorMessage);
            onError(errorMessage);
        }
        Log.d(TAG, "======================================================");
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, "==================== API FAILURE ====================");
        Log.e(TAG, "URL: " + call.request().url());
        Log.e(TAG, "Failure Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Message: " + t.getMessage(), t);
        Log.e(TAG, "=====================================================");

        String errorMessage = "Greška mreže: " + t.getMessage();
        onError(errorMessage);
    }

    public abstract void onSuccess(T response);
    public abstract void onError(String errorMessage);
}