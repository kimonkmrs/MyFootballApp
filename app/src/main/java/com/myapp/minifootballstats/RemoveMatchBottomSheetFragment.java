package com.myapp.minifootballstats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.minifootballstats.api.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoveMatchBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText editTextMatchID;
    private Button buttonDeleteMatch;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remove_match_bottom_sheet, container, false);

        // Initialize views
        editTextMatchID = view.findViewById(R.id.editTextMatchID);
        buttonDeleteMatch = view.findViewById(R.id.buttonDeleteMatch);

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Handle delete button click
        buttonDeleteMatch.setOnClickListener(v -> deleteMatch());

        return view;
    }

    private void deleteMatch() {
        try {
            int matchID = Integer.parseInt(editTextMatchID.getText().toString());

            Call<DeleteMatchResponse> call = apiService.deleteMatch(matchID);
            call.enqueue(new Callback<DeleteMatchResponse>() {
                @Override
                public void onResponse(Call<DeleteMatchResponse> call, Response<DeleteMatchResponse> response) {
                    if (response.isSuccessful()) {
                        DeleteMatchResponse deleteMatchResponse = response.body();
                        if (deleteMatchResponse != null) {
                            Toast.makeText(getContext(), deleteMatchResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Unknown error occurred", Toast.LENGTH_SHORT).show();
                        }

                        // Dismiss the dialog
                        dismiss();

                        // Notify MainActivity to remove the match from the list
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).removeMatchFromList(matchID);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to delete match. Status code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeleteMatchResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid match ID", Toast.LENGTH_SHORT).show();
        }
    }
}
