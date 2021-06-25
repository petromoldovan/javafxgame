package server.logic;

public enum Server {
    
    APP;

    private static final AppLogic APP_LOGIC = new AppLogic();

    public static AppLogic getLogic() {
        return APP_LOGIC;
    }
}
