package tests;

import org.json.simple.JSONObject;
import santaspeen.vk.api.Exceptions.VkApiError;
import santaspeen.vk.api.parseLongPoll;
import santaspeen.vk.api.vkApi;

public class LongPollAPIAndParse {
    private static final vkApi api = new vkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(vkApi.GROUP); // (!) Если токен группы, не обязательно.

        api.getLongPollServer();

        long lastTs = 0;
        while (true){

            JSONObject longPoll = api.listenLongPoll(25);
            parseLongPoll parse = api.parse(longPoll);

            if (lastTs != parse.ts) {
                lastTs = parse.ts;

                if (parse.failed > 0)
                    api.getLongPollServer();

                if (parse.isMessage()) {
                    String text = parse.message().text;
                    long peerId = parse.message().peerId;
                    long fromId = parse.message().fromId;

                    String message = "Текст: "+text+"\nОт: @id"+fromId;

                    System.out.println(message);
                    api.messagesSend(peerId, message);

                }
            }
        }
    }
}
