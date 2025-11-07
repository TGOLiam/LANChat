// Message class for message info
package core;
public class Message{
    String user;
    String message;
    String timestamp;
    Message(String timestamp, String user, String message){
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }
}