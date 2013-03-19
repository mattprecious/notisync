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

package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.LayoutFragment;

public class LayoutPage extends Page {
    protected int layoutId;

    protected LayoutPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return LayoutFragment.create(layoutId);
    }

    public LayoutPage setLayout(int resId) {
        layoutId = resId;
        return this;
    }

}
