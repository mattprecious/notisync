
package com.mattprecious.notisync.message;

public class TagPushMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public final String name;
    public final String tag;

    private TagPushMessage(Builder builder) {
        name = builder.name;
        tag = builder.tag;
    }

    public static class Builder {
        private String name;
        private String tag;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public TagPushMessage build() {
            return new TagPushMessage(this);
        }
    }
}
