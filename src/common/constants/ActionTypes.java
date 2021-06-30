package common.constants;

public class ActionTypes {
    public enum Code {
        SUCCESS,
        ERROR,
    }

    public enum ActionType {
        INVALID,
        LOGIN_USER,
        REGISTER_USER,
        FIND_MATCH,
        JOIN_ROOM,
        CURRENT_GAME_DATA_RESPONSE,
        UPDATE_GAME_POSITION_REQUEST,
        START_SINGLE_MATCH_REQUEST,
        RESET_GAME_POSITION_REQUEST,
        GAME_EVENT_TIMEOUT,
        GAME_EVENT_WIN,
        GAME_EVENT_LOSE,
        SCORES
    }

    public static ActionType getActionTypeFromMessage(String message) {
        final String[] s = message.split(";");
        if (s.length == 0) {
            System.err.printf("Error reading action type fro, [%s] \n", message);
            return ActionType.INVALID;
        }
        final String name = s[0];
        for (ActionType type : ActionType.values()) {
            if (type.name().equals(name)) return type;
        }
        return ActionType.INVALID;
    }
}
