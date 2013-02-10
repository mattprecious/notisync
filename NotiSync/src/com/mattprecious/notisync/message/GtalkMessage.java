
package com.mattprecious.notisync.message;

public class GtalkMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public String sender;
    public String message;

    private GtalkMessage(Builder builder) {
        super();

        sender = builder.sender;
        message = builder.message;
    }

    public static class Builder {
        private String sender;
        private String message;

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public GtalkMessage build() {
            return new GtalkMessage(this);
        }
    }
}
