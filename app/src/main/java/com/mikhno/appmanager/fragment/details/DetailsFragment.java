package com.mikhno.appmanager.fragment.details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhno.appmanager.R;
import com.mikhno.appmanager.adapter.DetailInfoAdapter;
import com.mikhno.appmanager.data.Data;
import com.mikhno.appmanager.model.DetailInfoModel;

import java.util.ArrayList;
import java.util.Objects;

public class DetailsFragment extends Fragment {

    String packageName = "";

    RecyclerView recyclerView;

    SharedPreferences prefs;
    PackageManager packageManager;

    DetailInfoAdapter adapter;

    ArrayList<DetailInfoModel> list = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        packageManager = requireActivity().getPackageManager();
        prefs = requireActivity().getSharedPreferences(Data.PREFS_NAME(requireActivity()), Context.MODE_PRIVATE);

        try {
            packageName = requireActivity().getIntent().getStringExtra("packageName");
        } catch (Exception e) {
            e.printStackTrace();
            packageName = requireActivity().getPackageName();
        }

        recyclerView = view.findViewById(R.id.recyclerView);

        if (recyclerView != null) recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new DetailInfoAdapter(list, requireActivity());

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(Objects.requireNonNull(packageName), 0);

            list.clear();

            String app_sourceDir = Objects.requireNonNull(packageInfo.applicationInfo.sourceDir);
            String app_publicSourceDir = Objects.requireNonNull(packageInfo.applicationInfo.publicSourceDir);

            try {
                list.add(new DetailInfoModel(getString(R.string.text_backup_agent), Objects.requireNonNull(packageInfo.applicationInfo.backupAgentName), 0, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            } try {
                list.add(new DetailInfoModel(getString(R.string.text_source_dir), app_sourceDir, 0, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            } try {
                if (!app_sourceDir.equalsIgnoreCase(app_publicSourceDir)) list.add(new DetailInfoModel(getString(R.string.text_pub_source_dir), app_publicSourceDir, 0, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            } try {
                list.add(new DetailInfoModel(getString(R.string.text_native_library_dir), Objects.requireNonNull(packageInfo.applicationInfo.nativeLibraryDir), 0, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            } try {
                list.add(new DetailInfoModel("UID", Objects.requireNonNull(String.valueOf(packageInfo.applicationInfo.uid)), 0, null, null));
            } catch (Exception e) {
                e.printStackTrace();
            }

            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        return view;
    }
}
