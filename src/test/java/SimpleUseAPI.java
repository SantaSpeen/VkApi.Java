import santaspeen.vk.api.vkApi;
import santaspeen.vk.api.Exceptions.VkApiError;

import org.json.JSONObject;

public class SimpleUseAPI {
    private static final vkApi api = new vkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(vkApi.USER); // (!) Если токен юсера

        String unixTime = api.method("utils.getServerTime");

        System.out.println(unixTime);
    }
}