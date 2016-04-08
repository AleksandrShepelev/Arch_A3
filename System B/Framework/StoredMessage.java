package Framework;

import MessagePackage.Message;

public class StoredMessage {
    public Message Message;
    public int Age;

    public StoredMessage(Message message) {
        Message = message;
        Age = 0;
    }

    public void incAge(){
        Age++;
    };
}
