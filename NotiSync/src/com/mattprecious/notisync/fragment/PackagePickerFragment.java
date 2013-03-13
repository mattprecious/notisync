
package com.mattprecious.notisync.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.common.collect.Lists;
import com.mattprecious.notisync.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackagePickerFragment extends DialogFragment {
    private PackageListAdapter listAdapter;
    private OnPackageSelectedListener packageListener;

    private RelativeLayout viewHolder;
    private ListView listView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            packageListener = (OnPackageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPackageSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        viewHolder = (RelativeLayout) inflater.inflate(R.layout.package_picker);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.package_picker_title);
        builder.setView(viewHolder);
        builder.setNegativeButton(R.string.package_picker_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dismiss();
            }

        });

        new Thread(new Runnable() {

            @Override
            public void run() {
                listAdapter = new PackageListAdapter(getActivity());

                listView = new ListView(getActivity());
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActivityInfo info = (ActivityInfo) view.getTag();
                        packageListener.onPackageSelected(info.packageName);
                        dismiss();
                    }
                });

                viewHolder.post(new Runnable() {

                    @Override
                    public void run() {
                        viewHolder.removeAllViews();
                        viewHolder.addView(listView);
                    }
                });
            }

        }).start();

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    public interface OnPackageSelectedListener {
        public void onPackageSelected(CharSequence packageName);
    }

    private class PackageListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private PackageManager packageManager;
        private List<ActivityInfo> data;

        public PackageListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            packageManager = context.getPackageManager();

            Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> launcherApps = packageManager.queryIntentActivities(launcherIntent,
                    PackageManager.PERMISSION_GRANTED);

            data = Lists.newArrayList();
            for (ResolveInfo info : launcherApps) {
                data.add(info.activityInfo);
            }

            Collections.sort(data, new Comparator<ActivityInfo>() {

                @Override
                public int compare(ActivityInfo a, ActivityInfo b) {
                    String aName = (String) a.loadLabel(packageManager);
                    String bName = (String) b.loadLabel(packageManager);
                    return aName.compareToIgnoreCase(bName);
                }
            });
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ActivityInfo getItem(int position) throws IndexOutOfBoundsException {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) throws IndexOutOfBoundsException {
            if (position < getCount() && position >= 0) {
                return position;
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActivityInfo info = getItem(position);

            if (convertView == null) { // If the View is not cached
                // Inflates the Common View from XML file
                convertView = this.inflater.inflate(R.layout.package_list_row, null);
            }

            convertView.setTag(info);

            TextView packageName = (TextView) convertView.findViewById(R.id.packageName);
            packageName.setText(info.loadLabel(packageManager));

            ImageView packageIcon = (ImageView) convertView.findViewById(R.id.packageIcon);
            packageIcon.setImageDrawable(info.loadIcon(packageManager));

            return convertView;
        }
    }

}
