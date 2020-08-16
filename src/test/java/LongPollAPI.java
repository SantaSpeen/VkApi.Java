import org.json.JSONObject;
import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.exceptions.VkApiError;
import santaspeen.vk.api.VkApi;

public class LongPollAPI {
    private static final VkApi api = new VkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(VkAPIAccountTypes.GROUP); // (!) Если токен группы, не обязательно.

        api.getLongPollServer();

        while (true){

            JSONObject longPoll = api.listenLongPoll(25);
            System.out.println(longPoll);

        }
    }
}
