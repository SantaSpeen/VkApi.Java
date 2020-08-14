package santaspeen.vk.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class parseMessage{

    public long date = 0;
    public boolean important = false;
    public long fromId  = 0;
    public JSONArray groupAttachments = null;
    public JSONObject userAttachments = null;
    public boolean isHidden = false;
    public JSONArray fwdMessages = null;
    public long id = 0;
    public String text = null;
    public long randomId = 0;
    public long out = 0;
    public long peerId = 0;
    public long conversationMessageId = 0;

    // TODO: Сделать isHidden в userMessage
    /**
     * Init of message parser.
     * Tested on 5.120 LongPoll version.
     *
     * @since 0.7
     *
     * @param groupMessage > Obj of message from group LongPoll
     * @param userMessage > ... .. ........ .... user .........
     * @param userId > This is secret :)
     */
    public parseMessage(JSONObject groupMessage, JSONArray userMessage, long userId){
        if (groupMessage != null){
            date = (long) groupMessage.get("date");
            important = (boolean) groupMessage.get("important");
            fromId = (long) groupMessage.get("from_id");
            groupAttachments = (JSONArray) groupMessage.get("attachments");
            isHidden = (boolean) groupMessage.get("is_hidden");
            fwdMessages = (JSONArray) groupMessage.get("fwd_messages");
            id = (long) groupMessage.get("id");
            text = (String) groupMessage.get("text");
            randomId = (long) groupMessage.get("random_id");
            out = (long) groupMessage.get("out");
            peerId = (long) groupMessage.get("peer_id");
            conversationMessageId = (long) groupMessage.get("conversation_message_id");

        } else if (userMessage != null){

            date = (long) userMessage.get(4);

            if (((JSONObject) userMessage.get(6)).get("title") != null)
                fromId = userId;
            else
                fromId = Long.parseLong((String) ((JSONObject) userMessage.get(6)).get("from"));

            userAttachments = (JSONObject) userMessage.get(7);
            id = (long) userMessage.get(1);
            text = (String) userMessage.get(5);
            randomId = (long) userMessage.get(8);
            peerId = (long) userMessage.get(3);

        }
    }
}

