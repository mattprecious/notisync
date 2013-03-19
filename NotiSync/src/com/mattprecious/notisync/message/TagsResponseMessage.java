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

package com.mattprecious.notisync.message;

import java.util.HashMap;

public class TagsResponseMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public final HashMap<String, String> tags;

    private TagsResponseMessage(Builder builder) {
        tags = builder.tags;
    }

    public static class Builder {
        private HashMap<String, String> tags;

        public Builder tags(HashMap<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public TagsResponseMessage build() {
            return new TagsResponseMessage(this);
        }
    }

}
