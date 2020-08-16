import org.json.JSONObject;
import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.exceptions.VkApiError;
import santaspeen.vk.api.features.onVkMessage;
import santaspeen.vk.api.parsers.parseMessage;
import santaspeen.vk.api.VkApi;

public class LongPollAPIFromInterface {

    public static final VkApi api = new VkApi("1a8dfd16d88cca3661e1cb08a28ddf1c33636baa428eed4ef902fa6460f3a35b40c02fcd34bb3d09e2061");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(VkAPIAccountTypes.USER); // (!) Если токен группы, не обязательно.

        api.bindCommands(new LongPollAPIFromInterface()).start();

    }

    @onVkMessage(text = "hi", startsWith = true, returnLongPoll = true)
    public void helloWorld(parseMessage msg, VkApi api, JSONObject longPoll) throws VkApiError {

        String out = "Текст: "+msg.text+"\nОт: @id"+msg.fromId+"\nВ: "+msg.peerId;

        System.out.println(out);
        System.out.println(longPoll);
        api.messagesSend(msg.peerId, "Ку");
    }


}