
package com.mattprecious.notisync.message;

import java.util.Map;

public class TagsResponseMessage extends BaseMessage {
    public final int VERSION_CODE = 1;
    
    public final Map<String, String> tags;
    
    private TagsResponseMessage(Builder builder) {
        tags = builder.tags;
    }
    
    public static class Builder {
        private Map<String, String> tags;

        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public TagsResponseMessage build() {
            return new TagsResponseMessage(this);
        }
    }
    
}
