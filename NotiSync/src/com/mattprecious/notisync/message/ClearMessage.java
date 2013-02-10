
package com.mattprecious.notisync.message;

public class ClearMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public final BaseMessage message;

    public ClearMessage(BaseMessage message) {
        this.message = message;
    }

}
