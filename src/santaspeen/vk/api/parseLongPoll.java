package santaspeen.vk.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class parseLongPoll{

    public int failed = 0;

    public long ts;
    public String type = null;

    public JSONArray updates = null;

    public JSONObject lastGroupUpdate = null;
    public String eventId = null;
    public long groupId = 0;
    public JSONObject groupObject = null;
    public JSONObject groupMessage = null;

    public long pts = 0;

    public JSONArray lastUserUpdate = null;
    public JSONArray userObject = null;
    public JSONArray userMessage = null;

    private final long userId;


    /**
     * Parse base of LongPoll
     *
     * @since 0.6
     *
     * @param event > LongPoll event
     * @param accountType > Account type (Need for parseMessage)
     * @param userId > This is secret :) (Need for parseMessage)
     */
    public parseLongPoll(JSONObject event, String accountType, long userId){
        this.userId = userId;

        if (event.get("failed") != null)
            failed = Integer.parseInt(event.get("failed").toString());

        updates = (JSONArray) event.get("updates");
        if (failed == 0)
            if (!updates.toString().equals("[]")){
                int i = updates.toArray().length - 1;
                if (accountType.equals(vkApi.GROUP)) {
                    ts = Long.parseLong((String) event.get("ts"));
                    lastGroupUpdate = (JSONObject) (updates).get(i);
                    eventId = lastGroupUpdate.get("event_id").toString();
                    groupId = (long) lastGroupUpdate.get("group_id");
                    type = (String) lastGroupUpdate.get("type");
                    if (type.equals("message_new")){
                        groupObject = (JSONObject) lastGroupUpdate.get("object");
                        groupMessage = (JSONObject) groupObject.get("message");
                    }
                } else if (accountType.equals(vkApi.USER)){
                    ts = (long) event.get("ts");

                    pts = (long) event.get("pts");

                    userObject = (JSONArray) updates.get(i);
                    lastUserUpdate = userObject;

                    type = userObject.get(0).toString();
                    if (type.equals("4")){
                        userMessage = userObject;
                    }
                }
            }
    }

    /**
     * Is last event been message?
     *
     * @since 0.6
     *
     * @return If last event been message return true
     */
    public boolean isMessage(){
        return groupMessage!=null || userMessage!=null;
    }

    /**
     * Link to class - parseMessage.
     *
     * @since 0.7
     *
     * @return class - parseMessage
     */
    public parseMessage message(){
        return new parseMessage(groupMessage, userMessage, userId);
    }

}
