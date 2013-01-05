
package com.mattprecious.otherdevice.message;

public class PhoneCallMessage extends BaseMessage {
    public enum Type {
        INCOMING,
        MISSED
    }

    public final int VERSION_CODE = 1;

    public String name;
    public String number;
    public Type type;

    private PhoneCallMessage(Builder builder) {
        super();

        name = builder.name;
        number = builder.number;
        type = builder.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + VERSION_CODE;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PhoneCallMessage other = (PhoneCallMessage) obj;
        if (VERSION_CODE != other.VERSION_CODE)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public static class Builder {
        private String name;
        private String number;
        private Type type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public PhoneCallMessage build() {
            return new PhoneCallMessage(this);
        }
    }

}
