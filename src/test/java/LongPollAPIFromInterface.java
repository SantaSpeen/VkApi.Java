import com.sun.org.apache.regexp.internal.RE;
import org.json.JSONObject;
import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.exceptions.VkApiError;
import santaspeen.vk.api.features.onNewVkEvent;
import santaspeen.vk.api.features.onVkMessage;
import santaspeen.vk.api.parsers.parseMessage;
import santaspeen.vk.api.VkApi;

import java.util.Arrays;

public class LongPollAPIFromInterface {

    public static final VkApi api = new VkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(VkAPIAccountTypes.GROUP); // (!) Если токен группы, не обязательно.

        api.bindCommands(new LongPollAPIFromInterface());

        Thread thread = api.startWithThread();
        System.out.println(thread.getName());

    }

    @onNewVkEvent() // 0.9.2 - Not work yet
    public void newEvent(JSONObject event){
        System.out.println(event);
    }

    @onVkMessage(text = "hi", startsWith = true, returnLongPoll = true)
    public void newMessage(parseMessage msg, VkApi api, JSONObject longPoll) throws VkApiError {

        System.out.println("Текст: "+msg.text+"\nОт: @id"+msg.fromId+"\nВ: "+msg.peerId);
        System.out.println(longPoll);

        if (api.userId != msg.fromId)
            api.messagesSend(msg.peerId, "hi");
    }
}

