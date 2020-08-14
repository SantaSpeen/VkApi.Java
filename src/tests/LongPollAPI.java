package tests;

import org.json.simple.JSONObject;
import santaspeen.vk.api.Exceptions.VkApiError;
import santaspeen.vk.api.parseLongPoll;
import santaspeen.vk.api.vkApi;

public class LongPollAPI {
    private static final vkApi api = new vkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(vkApi.GROUP); // (!) Если токен группы, не обязательно.

        api.getLongPollServer();

        while (true){

            JSONObject longPoll = api.listenLongPoll(25);
            System.out.println(longPoll);

        }
    }
}
