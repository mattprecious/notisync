
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + VERSION_CODE;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        GtalkMessage other = (GtalkMessage) obj;
        if (VERSION_CODE != other.VERSION_CODE)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (sender == null) {
            if (other.sender != null)
                return false;
        } else if (!sender.equals(other.sender))
            return false;
        return true;
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
