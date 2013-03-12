
package com.mattprecious.notisync.message;

public class TagsRequestMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    private TagsRequestMessage() {
    }

    public static class Builder {
        public TagsRequestMessage build() {
            return new TagsRequestMessage();
        }
    }
}
