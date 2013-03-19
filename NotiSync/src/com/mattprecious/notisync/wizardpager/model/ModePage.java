/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.wizardpager.model;

import android.content.Context;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.wizardpager.ui.ModeFragment;

import java.util.ArrayList;

/**
 * A page representing a branching point in the wizard. Depending on which
 * choice is selected, the next set of steps in the wizard may change.
 */
public class ModePage extends Page {
    private Context mContext;
    private Branch mPrimaryBranch;
    private Branch mSecondaryBranch;

    public ModePage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);

        mContext = context;
    }

    @Override
    public Page findByKey(String key) {
        if (getKey().equals(key)) {
            return this;
        }

        Page found;
        if (mPrimaryBranch != null) {
            found = mPrimaryBranch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        if (mSecondaryBranch != null) {
            found = mSecondaryBranch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> destination) {
        super.flattenCurrentPageSequence(destination);

        if (Preferences.isPrimary(mContext) && mPrimaryBranch != null) {
            mPrimaryBranch.childPageList.flattenCurrentPageSequence(destination);
        } else if (!Preferences.isPrimary(mContext) && mSecondaryBranch != null) {
            mSecondaryBranch.childPageList.flattenCurrentPageSequence(destination);
        }
    }

    public ModePage setPrimaryBranch(Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey("Primary");
        }
        mPrimaryBranch = new Branch(childPageList);
        return this;
    }

    public ModePage setSecondaryBranch(Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey("Secondary");
        }
        mSecondaryBranch = new Branch(childPageList);
        return this;
    }

    @Override
    public SherlockFragment createFragment() {
        return ModeFragment.create(getKey());
    }

    @Override
    public boolean isCompleted() {
        return Preferences.hasMode(mContext);
    }

    @Override
    public void notifyDataChanged() {
        mCallbacks.onPageTreeChanged();
        super.notifyDataChanged();
    }

    private static class Branch {
        public PageList childPageList;

        private Branch(PageList childPageList) {
            this.childPageList = childPageList;
        }
    }
}
