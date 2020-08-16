package santaspeen.vk.api.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import santaspeen.vk.api.features.VkAPIAccountTypes;

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

    private long getLong(Object to){
        try {
            return Long.parseLong((String) to);
        } catch (Exception e){
            return 0;
        }

    }

    private boolean getBool(Object to){
        try {
            return Boolean.parseBoolean((String) to);
        } catch (Exception e){
            return false;
        }
    }


    /**
     * Parse base of LongPoll
     *
     * @since 0.6
     *
     * @param event > LongPoll event
     * @param accountType > Account type (Need for parseMessage)
     * @param userId > This is secret :) (Need for parseMessage)
     */
    public parseLongPoll(JSONObject event, VkAPIAccountTypes accountType, long userId){
        this.userId = userId;

        if (event.opt("failed") != null)
            failed = Integer.parseInt(event.get("failed").toString());

        updates = event.optJSONArray("updates");
        if (failed == 0)
            if (!updates.toString().equals("[]")){
                int i = updates.length() - 1;

                if (accountType.equals(VkAPIAccountTypes.GROUP)) {

                    ts = getLong(event.get("ts")); // Оно тут в стринге приходит, ОБОЖАЮ КОСТЫЛИ

                    lastGroupUpdate = (updates).getJSONObject(i);
                    eventId = lastGroupUpdate.getString("event_id");
                    groupId = lastGroupUpdate.getLong("group_id");
                    type = lastGroupUpdate.getString("type");
                    if (type.equals("message_new")){
                        groupObject = lastGroupUpdate.getJSONObject("object");
                        groupMessage = groupObject.getJSONObject("message");
                    }
                } else if (accountType.equals(VkAPIAccountTypes.USER)){
                    ts = event.getLong("ts");

                    pts = event.getLong("pts");

                    userObject = updates.getJSONArray(i);
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
