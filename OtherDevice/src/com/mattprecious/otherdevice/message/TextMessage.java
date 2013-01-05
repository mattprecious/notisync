
package com.mattprecious.otherdevice.message;

public class TextMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public String name;
    public String number;
    public String message;

    private TextMessage(Builder builder) {
        super();

        name = builder.name;
        number = builder.number;
        message = builder.message;
    }

    public static class Builder {
        private String name;
        private String number;
        private String message;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public TextMessage build() {
            return new TextMessage(this);
        }
    }

}
