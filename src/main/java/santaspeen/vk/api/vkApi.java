package santaspeen.vk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import santaspeen.vk.api.exceptions.VkApiError;
import santaspeen.vk.api.features.VkAPIAccountTypes;
import santaspeen.vk.api.features.onVkMessage;
import santaspeen.vk.api.parsers.parseLongPoll;
import santaspeen.vk.api.parsers.parseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static java.net.URLEncoder.encode;

/**
 * @author SantaSpeen (GitHub)
 * @author id370926160 (VK)
 */

public class VkApi {

    private final String API_URL = "https://api.vk.com/method/";  // Base API URL

    public String v = "5.120";  // Tested on 5.120

    public VkAPIAccountTypes accountType = VkAPIAccountTypes.GROUP;  // Default account type is group :)
    private final String token;  // For access_token

    public long userId = 0;  // If account type is USER, here been your id. Needs for parse..

    private JSONObject longPollServer = null;  // Obj of LongPoll

    private final List<String> commandsText = new ArrayList<>();
    private final List<int[]> commandsOnlyBy = new ArrayList<>();

    private final List<Boolean> commandsStartsWith = new ArrayList<>();
    private final List<Boolean> commandsReturnLongPoll = new ArrayList<>();

    private final List<Method> commandsMethod = new ArrayList<>();
    private final List<Object> commandsObj = new ArrayList<>();

    /**
     * Init of class.
     * Use:
     *      public static final vkApi api = new vkApi("token");
     *
     * @since 0.1
     *
     * @param token > Token from vk.
     */
    public VkApi(String token){this.token = token;}

    private void notToBind(Class<?>[] params, Method method, Object obj, String expected){
        StringBuilder sb = new StringBuilder();
        for (Class<?> c : params) {
            sb.append(c.getSimpleName());
            sb.append(", ");
        }

        String paramString = sb.toString();
        if (paramString.length() > 2) {
            paramString = paramString.substring(0, paramString.length() - 2);
        }

        System.err.println("Skipped method with invalid parameter types found in method " +
                method.getName() + "(" + paramString + ") in class" + obj.getClass().getName() +
                ". Expected " + expected+".");
    }

    public Thread bindCommands(Object... objects) throws VkApiError {

        if (objects.length == 0)
            throw new VkApiError("No classes into commands();");

        for (Object obj: objects){

            if (obj == null)
                continue;

            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method: methods){

                // Skip if annotation is not present
                if (!method.isAnnotationPresent(onVkMessage.class)) {
                    continue;
                }

                // Make private method accessible
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                // Validate parameter types
                Class<?>[] params = method.getParameterTypes();

                if (params.length < 2){
                    notToBind(params, method, obj, "parseMessage and VkApi. (JSONObject - optional)");
                    continue;
                }

                if (params[0] != parseMessage.class || params[1] != VkApi.class){

                    if (params.length == 2) {
                        notToBind(params, method, obj, "parseMessage and VkApi");
                        continue;

                    } else if (params.length > 3 || params[2] != JSONObject.class){
                        notToBind(params, method, obj, "parseMessage, VkApi and JSONObject");
                        continue;
                    }
                }

                onVkMessage[] annotations =  method.getAnnotationsByType(onVkMessage.class);
                for (onVkMessage messageSettings : annotations) {

                    String text = messageSettings.text();
                    int[] onlyBy = messageSettings.onlyBy();
                    Boolean startsWith = messageSettings.startsWith();
                    Boolean returnLongPoll = messageSettings.returnLongPoll();

                    commandsText.add(text);
                    commandsOnlyBy.add(onlyBy);
                    commandsStartsWith.add(startsWith);
                    commandsReturnLongPoll.add(returnLongPoll);

                }

                commandsMethod.add(method);
                commandsObj.add(obj);
            }
        }

        System.out.println(commandsText);
        System.out.println(commandsOnlyBy);
        System.out.println(commandsStartsWith);
        System.out.println(commandsReturnLongPoll);
        
        System.out.println(commandsMethod);
        System.out.println(commandsObj);

        Thread thread = new Thread(() -> {
            try {
                getLongPollServer();

                long lastTs = 0;
                while (true){

                    JSONObject longPoll = listenLongPoll(25);
                    parseLongPoll parse = parse(longPoll);

                    if (lastTs != parse.ts) {
                        long start = System.currentTimeMillis();

                        lastTs = parse.ts;

                        if (parse.failed > 0)
                            getLongPollServer();

                        if (parse.isMessage()) {

                            parseMessage message = parse.message();

                            String text = message.text.toLowerCase();
                            long fromId = message.fromId;

                            int i = 0;
                            for (String comm: commandsText) {
                                comm = comm.toLowerCase();
                                JSONObject longPollToInvoke = null;

                                if (commandsReturnLongPoll.get(i))
                                    longPollToInvoke = longPoll;

                                if (commandsStartsWith.get(i)){
                                    if (text.startsWith(comm)){
                                        if (longPollToInvoke != null)
                                            commandsMethod.get(i).invoke(commandsObj.get(i), message, this, longPollToInvoke);
                                        else
                                            commandsMethod.get(i).invoke(commandsObj.get(i), message, this);
                                        continue;
                                    }
                                }
                                if (comm.equals(text)){
                                    if (longPollToInvoke != null)
                                        commandsMethod.get(i).invoke(commandsObj.get(i), message, this, longPollToInvoke);
                                    else
                                        commandsMethod.get(i).invoke(commandsObj.get(i), message, this);
                                    continue;

                                } i++;
                            }
                        }
                    }
                }
            } catch (VkApiError | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        thread.setName("Bot thread");
        return thread;
    }

    /**
     * Set account type.
     * Default:
     *      VkAPIAccountTypes.GROUP.
     *
     * Use:
     *      api.setAccountType(vkApi.USER);
     *
     * @throws VkApiError Api Error
     * @since v0.5
     * @param type > Account type
     */
    public void setAccountType(VkAPIAccountTypes type) throws VkApiError {
        if (type.equals(VkAPIAccountTypes.USER))
            userId = Long.parseLong(String.valueOf(parseJson(method("account.getProfileInfo")).get("id")));
        accountType = type;
    }

    /**
     * Parse JSON
     *
     * @since v0.8
     *
     * @param in > Json to parse
     *
     * @return JSONObject
     */
    public JSONObject parseJson(String in)  {
        try {return new JSONObject(in);}
        catch (JSONException e){return new JSONObject();}
    }

    /**
     * Parse JSON
     *
     * @since v0.8
     *
     * @param in > Json to parse
     *
     * @return JSONArray
     */
    public JSONArray parseJsonArray(String in)  {
        try {return  new JSONArray(in);}
        catch (JSONException e){return new JSONArray();}
    }


    private String errorOrResponse(String data, String method) throws VkApiError {
        JSONObject rqAns = parseJson(data);

        if (rqAns == null){
            rqAns = new JSONObject();
            JSONObject emulateJsonError = new JSONObject();
            emulateJsonError.put("error_code", 0);
            emulateJsonError.put("error_msg", "JSONException");
            rqAns.put("error", emulateJsonError);
        }

        if (rqAns.optJSONObject("error") != null){
            JSONObject errorObj = rqAns.getJSONObject("error");
            throw new VkApiError("Method: "+method+". Error: №: " + errorObj.get("error_code") + ", " + errorObj.get("error_msg"));
        }
        return rqAns.get("response").toString();
    }


    /**
     * Main API wrapper.
     * Use:
     *      //             Method          Params
     *      api.method("messages.send", peer_id=370926160&random_id=0&message=Hi");
     *
     * @throws VkApiError API error
     * @since v0.1
     *
     * @param method > Api method
     * @param params > Api params. Not required.
     *
     * @return JSON (String) answer from vk API
     */
    public String method(String method, String... params) throws VkApiError {
        String rq_get;
        if (params.length == 0)
            rq_get = rq.get(API_URL + method + "?" + "&access_token="+token+"&v="+v);
        else
            rq_get = rq.get(API_URL + method + "?" + params[0] + "&access_token="+token+"&v="+v);
        return errorOrResponse(rq_get, method);
    }

    /**
     * Fast method - messages.send.
     * Features:
     *      1. Message encode to URL
     *
     * Use:
     *      api.messagesSend(STRING_PEER_ID, STRING_TEXT);
     *
     * @throws VkApiError API error
     * @since v0.2
     * @update v0.8.2
     *
     * @param peerId > String peer_id
     * @param message > Text message
     *
     * @return Is send message
     */
    public boolean messagesSend(String peerId, String message) throws VkApiError {
        JSONObject send = parseJson(method("messages.send", "peer_id="+peerId+"&random_id=0&message="+encode(message)));
        return send != null;
    }

    /**
     * Fast method - messages.send.
     * Features:
     *      1. Message encode to URL
     *
     * Use:
     *      api.messagesSend(INT_PEER_ID, STRING_TEXT);
     *
     * @throws VkApiError API error
     * @since v0.3
     *
     * @param peerId > int peer_id
     * @param message > Text message
     *
     * @return Is send message
     */
    public boolean messagesSend(int peerId, String message) throws VkApiError {
        JSONObject send = parseJson(method("messages.send", "peer_id="+peerId+"&random_id=0&message="+ encode(message)));
        return send != null;
    }

    /**
     * Fast method - messages.send.
     * Features:
     *      1. Message encode to URL
     *
     * Use:
     *      api.messagesSend(LONG_PEER_ID, STRING_TEXT);
     *
     * @throws VkApiError API error
     * @since v0.7
     *
     * @param peerId > long peer_id
     * @param message > Text message
     *
     * @return Is send message
     */
    public boolean messagesSend(long peerId, String message) throws VkApiError {
        String send = method("messages.send", "peer_id="+peerId+"&random_id=0&message="+ encode(message));
        return send != null;
    }


    /**
     * Used by getLongPollServer() if accountType is GROUP.
     *
     * @throws VkApiError API error
     * @since v0.7
     *
     * @return self group_id parametr
     */
    public long getGroupId() throws VkApiError {
        return utils.getLong(parseJson(parseJsonArray(method("groups.getById")).get(0).toString()).get("id"));
    }

    /**
     * Fast method - {messages}/{groups}.getLongPollServer.
     * Use:
     *      api.getLongPollServer();
     *
     * @since v0.5
     * @throws VkApiError API error
     *
     * @return Api method - groups.getLongPollServer
     */
    public JSONObject getLongPollServer() throws VkApiError {
        String URLFix = "";
        if (accountType.equals(VkAPIAccountTypes.GROUP)) {
            long gi = getGroupId();
            longPollServer = parseJson(method("groups.getLongPollServer", "group_id=" + gi));
        } else {
            URLFix = "https://";
            longPollServer = parseJson(method("messages.getLongPollServer"));
        }

        longPollServer.put("URLFix", URLFix);

        return longPollServer;
    }

    /**
     * Listen long poll.
     * Use:
     *      JSONObject longPoll = api.listenLongPoll(INT_WAIT);
     *
     * @throws VkApiError API error
     * @since v0.4
     *
     * @param wait > LongPoll timeout.
     *
     * @return JSON LongPool answer from vk
     */
    public JSONObject listenLongPoll(int wait) throws VkApiError {
        if (longPollServer == null){
            throw new VkApiError("Init LongPoll Server with getLongPollServer()");
        }
        String key = longPollServer.get("key").toString();
        String ts = longPollServer.get("ts").toString();
        String server = longPollServer.get("server").toString();
        String URLFix = longPollServer.get("URLFix").toString();

        String rqOfLongPoll = rq.get(URLFix+server+"?act=a_check&key="+key+"&ts="+ts+"&wait="+wait+"&version=3&mode=234");
        return parseJson(rqOfLongPoll);
    }

    /**
     * Get only one LongPoll event.
     *
     * @throws VkApiError API Error
     * @since v0.2
     *
     * @return JSON LongPool answer from vk
     */
    public JSONObject oneLongPollEvent(int wait) throws VkApiError {
        getLongPollServer();
        return listenLongPoll(wait);
    }

    /**
     * Link to parse useful LongPoll
     * Use:
     *      api.parseLongPoll(api.listenLongPoll);
     *
     * @since v0.4
     *
     * @param event > LongPoll JSON
     *
     * @return class - parseLongPoll
     */
    public parseLongPoll parse(JSONObject event){
        return new parseLongPoll(event, accountType, userId);
    }
}

class rq {

    /**
     * GET request
     *
     * @throws VkApiError API error
     * @since v0.1
     *
     * @param urlToRead > URL to request
     *
     * @return Site answer
     */
    public static String get(String urlToRead) throws VkApiError {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        }
        catch (SocketTimeoutException e){throw new VkApiError("Connection failed..");}
        catch (UnknownHostException e){throw new VkApiError("Connection failed..");}
        catch (IOException e){throw new VkApiError(String.valueOf(e));}

    }
}
