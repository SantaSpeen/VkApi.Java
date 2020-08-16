package santaspeen.vk.api.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import santaspeen.vk.api.utils;

import java.util.Arrays;

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

    public int[] outboxMessage = {
            2, 3, 6, 7, 10, 11, 14, 15, 18, 19, 22, 26, 30, 31, 34, 35, 38, 42, 50, 51, 54, 58, 62, 63, 66, 67, 70, 74,
            82, 98, 99, 102, 106, 114, 115, 118, 122, 126, 127, 130, 131, 134, 138, 146, 162, 194, 195, 198, 202, 210,
            226, 227, 230, 234, 242, 243, 246, 250, 254, 255, 258, 259, 262, 266, 274, 290, 322, 386, 387, 390, 394,
            402, 418, 450, 451, 454, 458, 466, 482, 483, 486, 490, 498, 499, 502, 506, 510, 511, 514, 515, 518, 522,
            530, 546, 578, 642, 770, 771, 774, 778, 786, 802, 834, 898, 899, 902, 906, 914, 930, 962, 963, 966, 970,
            978, 994, 995, 998, 1002, 1010, 1011, 1014, 1018, 1022, 1023, 8227, 65538, 65539, 65542, 65546, 65554,
            65570, 65602, 65666, 65794, 66050, 66051, 66054, 66058, 66066, 66082, 66114, 66178, 66306, 66307, 66310,
            66314, 66322, 66338, 66370, 66434, 66435, 66438, 66442, 66450, 66466, 66498, 66499, 66502, 66506, 66514,
            66530, 66531, 66534, 66538, 66546, 66547, 66550, 66554, 66558, 66559, 131074, 131075, 131078, 131082,
            131090, 131106, 131138, 131202, 131330, 131586, 196610, 196611, 196614, 196618, 196626, 196642, 196674,
            196738, 196866, 197122, 197123, 197126, 197130, 197138, 197154, 197186, 197250, 197378, 197382, 197386,
            197394, 197410, 197442, 197506, 197507, 197510, 197514, 197522, 197538, 197570, 197574, 197578, 197586,
            197602, 197603, 197606, 197610, 197618, 197619, 197622, 197626, 197630, 197631, 262146, 262147, 262150,
            262154, 262162, 262178, 262210, 262274, 262402, 262658, 327682, 393218, 393219, 393222, 393226, 393234,
            393250, 393282, 393346, 393474, 393730, 458754, 458755, 458758, 458762, 458770, 458786, 458818, 458882,
            459010, 459266, 459267, 459270, 459274, 459282, 459298, 459330, 459394, 459522, 459523, 459526, 459530,
            459538, 459554, 459586, 459650, 459651, 459654, 459658, 459666, 459682, 459714, 459715, 459718, 459722,
            459730, 459746, 459747, 459750, 459754, 459762, 459763, 459766, 459770, 459774, 459775};


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


            System.out.println(Arrays.binarySearch(outboxMessage, userMessage.getInt(2)));

            date = utils.getLong(userMessage.get(4));

            userAttachments = userMessage.getJSONObject(7);
            id = userMessage.getLong(1);
            text = userMessage.getString(5);
            randomId = userMessage.getLong(8);
            peerId = userMessage.getLong(3);

            if (((JSONObject) userMessage.get(6)).opt("title") != null){
                if (Arrays.binarySearch(outboxMessage, userMessage.getInt(2)) > 0)
                    fromId = peerId;
                else
                    fromId = userId;
            } else
                fromId = Long.parseLong((userMessage.getJSONObject(6)).getString("from"));

        }
    }
}

