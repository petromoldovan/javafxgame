package client.logic;

public enum Client {
    APP;
    
    public static final AppLogic APP_LOGIC = new AppLogic();

    public static AppLogic getAppLogic() {
        return APP_LOGIC;
    }
}
