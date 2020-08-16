import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.VkApi;
import santaspeen.vk.api.exceptions.VkApiError;

public class SimpleUseAPI {
    private static final VkApi api = new VkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(VkAPIAccountTypes.USER); // (!) Если токен юсера

        String unixTime = api.method("utils.getServerTime");

        System.out.println(unixTime);
    }
}