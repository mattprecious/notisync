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

public class CustomMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public String tag;
    public String title;
    public String body;

    private CustomMessage(Builder builder) {
        super();

        tag = builder.tag;
        title = builder.title;
        body = builder.body;
    }

    public static class Builder {
        private String tag;
        private String title;
        private String body;

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String body) {
            this.body = body;
            return this;
        }

        public CustomMessage build() {
            return new CustomMessage(this);
        }
    }
}
