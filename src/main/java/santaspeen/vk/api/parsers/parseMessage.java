package santaspeen.vk.api.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import santaspeen.vk.api.utils;

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
            date = utils.getLong(groupMessage.get("date"));
            important = groupMessage.getBoolean("important");
            fromId = groupMessage.getLong("from_id");
            groupAttachments = groupMessage.getJSONArray("attachments");
            isHidden = groupMessage.getBoolean("is_hidden");
            fwdMessages = groupMessage.getJSONArray("fwd_messages");
            id = groupMessage.getLong("id");
            text = (String) groupMessage.get("text");
            randomId = groupMessage.getLong("random_id");
            out = utils.getLong(groupMessage.get("out"));
            peerId = utils.getLong(groupMessage.get("peer_id"));
            conversationMessageId = utils.getLong(groupMessage.get("conversation_message_id"));

        } else if (userMessage != null){

            date = utils.getLong(userMessage.get(4));

            if (((JSONObject) userMessage.get(6)).opt("title") != null)
                fromId = userId;
            else
                fromId = Long.parseLong((userMessage.getJSONObject(6)).getString("from"));

            userAttachments = userMessage.getJSONObject(7);
            id = userMessage.getLong(1);
            text = userMessage.getString(5);
            randomId = userMessage.getLong(8);
            peerId = userMessage.getLong(3);

        }
    }
}

