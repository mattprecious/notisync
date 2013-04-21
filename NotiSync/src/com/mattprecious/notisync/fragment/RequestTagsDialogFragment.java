/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.common.collect.Lists;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.message.BaseMessage;
import com.mattprecious.notisync.message.TagsRequestMessage;
import com.mattprecious.notisync.service.SecondaryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class RequestTagsDialogFragment extends SherlockDialogFragment {
    private final int REQUEST_TIMEOUT = 5000;

    private OnTagSelectedListener tagListener;

    private LocalBroadcastManager broadcastManager;

    private ProgressBar progressBar;
    private TextView errorText;
    private View emptyView;
    private ListView listView;
    private TagListAdapter listAdapter;

    private Timer requestTimer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            tagListener = (OnTagSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTagSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(
                R.layout.request_tags, null);

        // for some reason when you replace the view on a legacy dialog it wipes
        // the background colour...
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            rootView.setBackgroundColor(getActivity().getResources().getColor(
                    android.R.color.background_light));
        }

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.request_tags_title);
        builder.setView(rootView);
        builder.setNegativeButton(R.string.request_tags_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dismiss();
            }

        });

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        errorText = (TextView) rootView.findViewById(R.id.error);
        emptyView = rootView.findViewById(R.id.empty_view);

        listAdapter = new TagListAdapter(getActivity());

        listView = (ListView) rootView.findViewById(R.id.tagsList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TagInfo info = (TagInfo) view.getTag();
                tagListener.onTagSelected(info.profileName, info.tagName);
                dismiss();
            }
        });

        broadcastManager.registerReceiver(tagsReceiver, new IntentFilter(
                SecondaryService.ACTION_TAGS_RECEIVED));

        startTimer();

        Intent tagRequestIntent = new Intent(SecondaryService.ACTION_SEND_MESSAGE);
        tagRequestIntent.putExtra(SecondaryService.EXTRA_MESSAGE,
                BaseMessage.toJsonString(new TagsRequestMessage.Builder().build()));
        broadcastManager.sendBroadcast(tagRequestIntent);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().setContext(getActivity());
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTimer();

        unregisterReceiver();
    }

    public interface OnTagSelectedListener {
        public void onTagSelected(String profileName, String tag);
    }

    private void startTimer() {
        if (requestTimer != null) {
            requestTimer.cancel();
        }

        requestTimer = new Timer();
        requestTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                unregisterReceiver();
                progressBar.post(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        errorText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, REQUEST_TIMEOUT);
    }

    private void cancelTimer() {
        if (requestTimer != null) {
            requestTimer.cancel();
        }
    }

    private void unregisterReceiver() {
        try {
            broadcastManager.unregisterReceiver(tagsReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    private BroadcastReceiver tagsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            cancelTimer();

            @SuppressWarnings("unchecked")
            HashMap<String, String> tags = (HashMap<String, String>) intent
                    .getSerializableExtra(SecondaryService.EXTRA_TAGS);

            List<TagInfo> tagList = Lists.newArrayList();
            for (Entry<String, String> entry : tags.entrySet()) {
                tagList.add(new TagInfo(entry.getValue(), entry.getKey()));
            }

            listAdapter.setData(tagList);
            listAdapter.notifyDataSetChanged();

            progressBar.setVisibility(View.GONE);

            if (tagList.isEmpty()) {
                listView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

        }
    };

    private class TagInfo {
        public String profileName;
        public String tagName;

        public TagInfo(String profileName, String tagName) {
            this.profileName = profileName;
            this.tagName = tagName;
        }
    }

    private class TagListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<TagInfo> data;

        public TagListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            data = Lists.newArrayList();
        }

        public void setData(List<TagInfo> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public TagInfo getItem(int position) throws IndexOutOfBoundsException {
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
            TagInfo info = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.request_tags_list_row, null);
            }

            convertView.setTag(info);

            TextView profileName = (TextView) convertView.findViewById(R.id.profileName);
            profileName.setText(info.profileName);

            TextView tagName = (TextView) convertView.findViewById(R.id.tagName);
            tagName.setText(info.tagName);

            return convertView;
        }
    }

}
