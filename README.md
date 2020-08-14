# VkApi
[![CircleCI](https://circleci.com/gh/SantaSpeen/VkApi.Java.svg?style=svg)](https://circleci.com/gh/SantaSpeen/VkApi.Java)
![License](https://img.shields.io/github/license/SantaSpeen/VkApi.Java)


* Language: [RU] , [[EN]](https://github.com/SantaSpeen/VkApi.Java/README_en.md)

## Navigation:

1. [Features](https://github.com/SantaSpeen/VkApi.Java#features)
2. [Use](https://github.com/SantaSpeen/VkApi.Java#use)
    1. [Базовое представление библиотеки](https://github.com/SantaSpeen/VkApi.Java#база)
    2. [Работа с LongPoll API](https://github.com/SantaSpeen/VkApi.Java#работа-с-лонгпулом)
    3. [Парсинг LongPool и Сообщений](https://github.com/SantaSpeen/VkApi.Java#парсинг)
3. [Links](https://github.com/SantaSpeen/VkApi.Java#links)

## Features:

* Простое использование.
* Встроенный обработчик лонгпула.
* Встроенный парсинг лонгпула.
* Встроенный парсинг сообщений.


* Проект основан на [org.json](https://github.com/stleary/JSON-java).

### Use:

#### База

***Ниже будет пример. паст scr/tests/SimpleUseAPI.java***

1. Скачиваем [latest](https://github.com/SantaSpeen/VkApi.Java/releases) JAR (Скоро выложу в maven)
2. Инициализируем: `final vkApi api = new vkApi("TOKEN");`
3. Выбераем тип:
    1. Если токен группы, можно ничего не делать
    2. Если токен пользывателя: `api.setAccountType(vkApi.USER);`
4. Вызываем любой метод по шаблону: `String param = api.method("VK_API.METHOD", "PARAM1=some&PARAM2=some&PARAM3=some")`
    * Важно!:
        * `param` - это уже поле `response`
        * Всё обращение к апи приходит в формате `String`. Сделано для совместимости.
    
    * Фаст-методы (самые используемые методы):
        * `api.messagesSend(PEER_ID, MESSAGE)` - messages.send
        * `api.getGroupId()` - groups.getById, поле `id`. Работает только с групповым токеном.
        * `api.getLongPollServer()` - {messages}/{groups}.getLongPollServer. Имеется доп. поле - `URLFix`, его обрабатывать не следует.

5. Понять что вы самый крутой разраб, и скачать мою библиотеку ;)
```java
import santaspeen.vk.api.vkApi;
import santaspeen.vk.api.Exceptions.VkApiError;

import org.json.JSONObject;

public class SimpleUseAPI {
    private static final vkApi api = new vkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {

        api.setAccountType(vkApi.USER); // (!) Если токен юсера
        
        String unixTime = api.method("utils.getServerTime");

        System.out.println(unixTime.get("long"));  
    }
}
```
#### Работа с лонгпулом

***Ниже будет пример. паст scr/tests/LongPollAPI.java***

* Все ивенты приходят в классе `JSONObject` 

1. Следуем по схеме выше.
2. Получаем лонгпул:
    * Если нужно получить 1 ивент используйте: `JSONObject event = api.oneLongPollEvent(WAIT);`
    * Для получения ивентов по схеме:
        1. Получите настройки лонгпул сервер: `api.getLongPollServer();`, нужно для инициализации настроек, передавать параметр никуда не нужно.
        2. Создаём цикл:
            ```java
            while (true){
              JSONObject longPoll = api.listenLongPoll(25);
              System.out.println(longPoll);
            }
            ```
* Итого у нас получилось:
```java
import org.json.JSONObject;
import santaspeen.vk.api.Exceptions.VkApiError;
import santaspeen.vk.api.parseLongPoll;
import santaspeen.vk.api.vkApi;

public class LongPollAPI {
    private static final vkApi api = new vkApi("TOKEN");

    public static void main(String[] args) throws VkApiError {
        api.getLongPollServer();

        long lastTs = 0;
        while (true){
            JSONObject longPoll = api.listenLongPoll(25);
            System.out.println(longPoll);
        }
    }
}
```
#### Парсинг

***Ниже будет пример. паст scr/tests/LongPollAPIAndParse.java***

Прочитав и запустив предыдущую часть, вы, наверное, выключили, пожалев консоль. Я помогу её уберечь от таких рейдов.

1. Прочитать предыдущую часть.
2. Создать парсер: `parseLongPoll parse = api.parse( longPoll );`
    * Мы уже имеем поля: `failed`, `ts`, `type`, `updates`
    * Если токен групповой: `eventId`, `groupId`, `lastGroupUpdate`, `groupObject`, `groupMessage`. Не указанные поля будут всегда null.
    * Если токен страницы: `pts`, `lastUserUpdate`, `userObject`, `userMessage`. Не указанные поля будут всегда null.
3. Смотря в [документацию](https://vk.com/dev/using_longpoll?f=2.%20%D0%A4%D0%BE%D1%80%D0%BC%D0%B0%D1%82%20%D0%BE%D1%82%D0%B2%D0%B5%D1%82%D0%B0), можем сразу сделать обработку failed:
    ```java
    if (parse.failed > 0)
        api.getLongPollServer();
    ```
4. Теперь можно убрать бесконечный лонгпул из консоли, я это делаю с помощью ts:
    ```java
    if (lastTs != parse.ts) {
        lastTs = parse.ts;
        // Ваш код..
    }
    ```
5. Заворачиваем:
    ```java
    JSONObject longPoll = api.listenLongPoll(25);
    parseLongPoll parse = api.parse( longPoll );
    
    if (lastTs != parse.ts) {
        lastTs = parse.ts;
        if (parse.failed > 0)
            api.getLongPollServer();
            // Ваш код..
    }
    ```
6. На этом всё, вы наверное, уже, успели подумать, а нееет, ещё есть парсинг сообщений. (Group LongPoll version >= 5.107)
    * Парсинг сообщений очень прост, используя, уже созданный `parseLongPoll parse`, берём из него метод `message()`, т.е. `parse.message()`.
        * Поля метода: `date`, `important`, `fromId`, `groupAttachments`, `userAttachments`, `isHidden`, `fwdMessages`, `id`, `text`, `randomId`, `out`, `peerId`, `conversationMessageId`
    
    * Рекомендую `parse.message()` обернуть в `if (parse.isMessage())`.
7. Иии в итоге? У нас готовый чат-бот
```java
import org.json.JSONObject;
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
```
## Links

- [Link to this project](https://github.com/SantaSpeen/VkApi.Java)

- [Author](https://vk.com/id370926160)
