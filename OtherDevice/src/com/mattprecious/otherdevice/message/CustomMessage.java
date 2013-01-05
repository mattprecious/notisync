
package com.mattprecious.otherdevice.message;

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
