package com.mikhno.appmanager.fragment;

import static android.app.Activity.RESULT_OK;

import static com.mikhno.appmanager.MainActivity.APP_PACKAGE_REMOVE;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.mikhno.appmanager.MainActivity;
import com.mikhno.appmanager.R;
import com.mikhno.appmanager.adapter.AppAdapter;
import com.mikhno.appmanager.data.AppData;
import com.mikhno.appmanager.decoration.ListExpressiveDecorate;
import com.mikhno.appmanager.model.AppModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsFragment extends Fragment {

    ArrayList<AppModel> list = new ArrayList<>();

    CircularProgressIndicator progressBar;
    RecyclerView recyclerView;

    AppAdapter adapter;

    ExecutorService executorSingle;

    public AppsFragment newInstance(String tabName) {
        Bundle args = new Bundle();
        args.putString("TAB_NAME", tabName);

        setArguments(args);

        return this;
    }

    @SuppressLint({"MissingInflatedId", "UseRequireInsteadOfGet"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        try {
            progressBar = view.findViewById(R.id.progressBar);
            recyclerView = view.findViewById(R.id.recyclerView);

            adapter = new AppAdapter(list, requireContext());

            if (recyclerView != null) {
                recyclerView.addItemDecoration(new ListExpressiveDecorate(requireContext(), 2, 12, 12));
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(adapter);

                ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(insets.left, 0, insets.right, insets.bottom);

                    return WindowInsetsCompat.CONSUMED;
                });
            }

            MainActivity.uninstallAppLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (!APP_PACKAGE_REMOVE.trim().isEmpty()) {
                        Snackbar.make(recyclerView, String.format(getString(R.string.toast_app_deleted), APP_PACKAGE_REMOVE), Snackbar.LENGTH_LONG).show();
                        requireActivity().runOnUiThread(() -> adapter.notifyItemRemoved(new AppData().removeAppByPackage(list, APP_PACKAGE_REMOVE)));
                    }
                } else Snackbar.make(recyclerView, getString(R.string.toast_app_not_deleted), Snackbar.LENGTH_LONG).show();

                APP_PACKAGE_REMOVE = "";
            });

            executorSingle = Executors.newSingleThreadExecutor();
            executorSingle.submit(() -> {
                requireActivity().runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.post(() -> progressBar.animate().alpha(1f).setDuration(200).start());
                    } if (recyclerView != null) {
                        recyclerView.post(() -> {
                            recyclerView.animate().alpha(0f).setDuration(0).start();
                            recyclerView.scrollToPosition(0);
                        });
                    }
                });

                list.clear();

                PackageManager packageManager = requireActivity().getPackageManager();

                try {
                    assert getArguments() != null;
                    if (Objects.requireNonNull(getArguments().getString("TAB_NAME")).equalsIgnoreCase("default")) {
                        list.addAll(new AppData().get_arrayAppsUser(packageManager));
                    } else if (Objects.requireNonNull(getArguments().getString("TAB_NAME")).equalsIgnoreCase("system")) {
                        list.addAll(new AppData().get_arrayAppsSystem(packageManager));
                    } else if (Objects.requireNonNull(getArguments().getString("TAB_NAME")).equalsIgnoreCase("google_play")) {
                        list.addAll(new AppData().get_arrayAppsGooglePlay(requireContext(), packageManager));
                    } else if (Objects.requireNonNull(getArguments().getString("TAB_NAME")).equalsIgnoreCase("all")) {
                        list.addAll(new AppData().get_arrayAppsAll(packageManager));
                    }
                } catch (Exception ignored) {}

                requireActivity().runOnUiThread(() -> {
                    try {
                        adapter.setList(list);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (progressBar != null) {
                        progressBar.post(() -> progressBar.animate().alpha(0f).setDuration(200).start());
                    } if (recyclerView != null) {
                        recyclerView.post(() -> recyclerView.animate().alpha(1f).setDuration(200).start());
                    }
                });
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error apps fragment: "  + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (executorSingle != null && !executorSingle.isShutdown()) {
            executorSingle.shutdownNow();
        }
    }
}
