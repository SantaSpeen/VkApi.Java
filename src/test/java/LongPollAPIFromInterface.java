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
        api.messagesSend(msg.peerId, "!");
    }

    @onVkMessage(text = "ks", startsWith = true)
    public void helloWorld2(parseMessage msg, VkApi api) throws VkApiError {

        String out = "Текст: "+msg.text+"\nОт: @id"+msg.fromId+"\nВ: "+msg.peerId;

        System.out.println(out);
        api.messagesSend(msg.peerId, "!!");
    }

//          In chat                                                                         In private
//             msgid flags peer_id     timestamp  text  ХЗ, честно     вложения  random_id     msgid flags peer_id     timestamp  text  ХЗ, честно     вложения  random_id
// Non-self [4,63214,532497,2000000035,1597576801,"hi",{"from":"370926160"},{},0]           [4,63219,49,   370926160,  1597577183,"hi",{"title":" ... "},{},0]
// Self     [4,63215,8227,  2000000035,1597576801,"hi",{"from":"583018016"},{},0]           [4,63220,35,   370926160,  1597577184,"hi",{"title":" ... "},{},0]

}