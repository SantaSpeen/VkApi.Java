package santaspeen.vk.api.features;

public enum VkAPIAccountTypes {
    USER("user"),
    GROUP("group");

    private final String accountType;

    VkAPIAccountTypes(String type) {
        this.accountType = type;
    }

    public String getAccountType() {
        return accountType;
    }
}
