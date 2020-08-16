package santaspeen.vk.api.exceptions;


public class VkApiError extends Exception{
    public VkApiError(String error){super(error);}
    public VkApiError(Exception error){super(error);}
}
