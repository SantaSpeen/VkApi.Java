package santaspeen.vk.api;

public class utils {

    public static long getLong(Object to){
        try {
            return Long.parseLong(String.valueOf(to));
        } catch (Exception e){
            return 0;
        }

    }

    public static boolean getBool(Object to){
        try {
            return Boolean.parseBoolean(String.valueOf(to));
        } catch (Exception e){
            return false;
        }
    }
}
