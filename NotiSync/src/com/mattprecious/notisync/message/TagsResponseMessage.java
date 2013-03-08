
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
