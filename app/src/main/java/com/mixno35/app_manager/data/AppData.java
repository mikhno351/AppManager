package com.mixno35.app_manager.data;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.mixno35.app_manager.BuildConfig;
import com.mixno35.app_manager.MainActivity;
import com.mixno35.app_manager.R;
import com.mixno35.app_manager.model.AppModel;
import com.mixno35.app_manager.utils.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData {

    public boolean isLaunchable(@NonNull PackageManager packageManager, @NonNull String packageName) {
        return packageManager.getLaunchIntentForPackage(packageName) != null;
    }

    public boolean isSystem(@NonNull PackageManager packageManager, @NonNull String packageName) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        } catch (Exception e) { return false; }
    }

    public boolean isSettings(@NonNull PackageManager packageManager, @NonNull String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + packageName));

        return intent.resolveActivity(packageManager) != null;
    }

    public static boolean isIntentAvailable(@NonNull Context context, @NonNull Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void installApk(Context context, File apkFile) {
        if(apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(context, apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isAppInstalledFromPlayStore(@NonNull Context context, @NonNull String packageName) {
        List<String> valid = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(packageName);
        return installer != null && valid.contains(installer);
    }

    public void openAppSettings(@NonNull Context context, @NonNull String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + packageName));

        if (isIntentAvailable(context, intent)) context.startActivity(intent);
    }

    public void uninstallApp(@NonNull String packageName) {
        try {
            Uri packageUri = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

            MainActivity.APP_PACKAGE_REMOVE = packageName;
            MainActivity.uninstallAppLauncher.launch(uninstallIntent);
        } catch (Exception ignored) {}
    }

    public void launchApp(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String packageName) {
        Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);

        if (launchIntent != null) {
            context.startActivity(launchIntent);
        }
    }

    public int removeAppByPackage(List<AppModel> appList, String packageToRemove) {
        int removedPosition = -1;
        for (int i = 0; i < appList.size(); i++) {
            AppModel appModel = appList.get(i);
            if (appModel.get_package().equals(packageToRemove)) {
                appList.remove(i);
                removedPosition = i;
                break;
            }
        }
        return removedPosition;
    }

    public String getProvenance(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull String packageName) {
        String provenance;

        if (isAppInstalledFromPlayStore(context, packageName)) provenance = context.getString(R.string.provenance_google_play);
        else if (isSystem(packageManager, packageName)) provenance = context.getString(R.string.provenance_system);
        else provenance = context.getString(R.string.provenance_user);

        return provenance;
    }

    public ArrayList<AppModel> get_arrayAppsUser(@NonNull PackageManager packageManager) {
        ArrayList<AppModel> list = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo info : packages) {
            if (!isSystem(packageManager, info.packageName)) list.add(new AppModel(
                    info.packageName,
                    info.applicationInfo.loadLabel(packageManager).toString(),
                    info.applicationInfo.loadIcon(packageManager)
            ));
        }

        return list;
    }

    public ArrayList<AppModel> get_arrayAppsSystem(@NonNull PackageManager packageManager) {
        ArrayList<AppModel> list = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo info : packages) {
            if (isSystem(packageManager, info.packageName)) list.add(new AppModel(
                    info.packageName,
                    info.applicationInfo.loadLabel(packageManager).toString(),
                    info.applicationInfo.loadIcon(packageManager)
            ));
        }

        return list;
    }

    public ArrayList<AppModel> get_arrayAppsGooglePlay(@NonNull Context context, @NonNull PackageManager packageManager) {
        ArrayList<AppModel> list = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo info : packages) {
            if (isAppInstalledFromPlayStore(context, info.packageName)) list.add(new AppModel(
                    info.packageName,
                    info.applicationInfo.loadLabel(packageManager).toString(),
                    info.applicationInfo.loadIcon(packageManager)
            ));
        }

        return list;
    }

    public ArrayList<Long> getAppSize(@NonNull Context context, @NonNull String packageName) {
        ArrayList<Long> arrayList = new ArrayList<>();

        final StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            StorageStats storageStats = storageStatsManager.queryStatsForUid(applicationInfo.storageUuid, applicationInfo.uid);
            arrayList.add(storageStats.getCacheBytes());
            arrayList.add(storageStats.getDataBytes());
            arrayList.add(storageStats.getAppBytes());
        } catch (Exception e) {
            arrayList.add(((long) 0));
            arrayList.add(((long) 0));
            arrayList.add(((long) 0));
        }

        return arrayList;
    }

    public ArrayList<AppModel> get_arrayAppsAll(@NonNull PackageManager packageManager) {
        ArrayList<AppModel> list = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo info : packages) {
            list.add(new AppModel(info.packageName, info.applicationInfo.loadLabel(packageManager).toString(), info.applicationInfo.loadIcon(packageManager)));
        }

        return list;
    }

    public ArrayList<AppModel> get_arrayAppsSearch(@NonNull PackageManager packageManager, @NonNull String text) {
        ArrayList<AppModel> list = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo info : packages) {
            String packageName = info.packageName.toLowerCase();
            String appName = info.applicationInfo.loadLabel(packageManager).toString().toLowerCase();
            String searchText = text.toLowerCase();

            if (packageName.contains(searchText) || appName.contains(searchText)) {
                list.add(new AppModel(info.packageName, info.applicationInfo.loadLabel(packageManager).toString(), info.applicationInfo.loadIcon(packageManager)));
            }
        }

        return list;
    }

    public String getAppDeveloper(@NotNull Context context, @NonNull String packageName) {
        String[] arr = packageName.split("\\.");
        String result = context.getString(R.string.text_unknown);
        if (arr.length >= 2) {
            result = TextUtils.capitalizeFirstLetter(arr[1]);
        }

        if (packageName.equalsIgnoreCase("com.mixno35.app_manager")) {
            result = context.getString(R.string.developer_alexander_mikhno);
        } if (packageName.equalsIgnoreCase("com.code_element.vipapp.mixno35")) {
            result = TextUtils.multipartDeveloper(context.getString(R.string.developer_vipapp), context.getString(R.string.developer_alexander_mikhno));
        } if (packageName.equalsIgnoreCase("com.code_element.vipapp")) {
            result = context.getString(R.string.developer_vipapp);
        } if (packageName.equalsIgnoreCase("com.instagram.android")) {
            result = context.getString(R.string.developer_meta);
        } if (packageName.startsWith("com.twitter.")) {
            result = context.getString(R.string.developer_x);
        } if (packageName.startsWith("com.google.")) {
            result = context.getString(R.string.developer_google);
        } if (packageName.startsWith("ru.vk.") || packageName.equalsIgnoreCase("com.vkontakte.android")) {
            result = context.getString(R.string.developer_vk);
        } if (packageName.startsWith("com.alseda.")) {
            result = context.getString(R.string.developer_1m_solutions);
        }

        return result;
    }

    Uri uriFromFile(Context context, File file) {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
    }
}
