import com.sun.istack.internal.NotNull;
import org.json.JSONObject;
import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.exceptions.VkApiError;
import santaspeen.vk.api.parsers.parseLongPoll;
import santaspeen.vk.api.parsers.parseMessage;
import santaspeen.vk.api.VkApi;

public class LongPollAPIAndParse {

    public static final VkApi api = new VkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(VkAPIAccountTypes.USER); // (!) Если токен группы, не обязательно.

        api.getLongPollServer();

        long lastTs = 0;
        while (true){

            JSONObject longPoll = api.listenLongPoll(25);
            parseLongPoll parse = api.parse(longPoll);

            if (lastTs != parse.ts) {
                long start = System.currentTimeMillis();

                lastTs = parse.ts;

                if (parse.failed > 0)
                    api.getLongPollServer();

                if (parse.isMessage()) {

                    parseMessage message = parse.message();
                    String text = message.text;
                    long peerId = message.peerId;
                    long fromId = message.fromId;

                    String out = "Текст: "+text+"\nОт: @id"+fromId+"\nВ: "+peerId;

                    System.out.println(out);
                    if (api.userId != fromId)
                        api.messagesSend(peerId, out);
                }

                System.out.println(longPoll);
                System.out.println("Event live time: " + (System.currentTimeMillis() - start)+ " ms\n");
            }
        }
    }
}