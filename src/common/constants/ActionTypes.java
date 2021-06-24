package common.constants;

public class ActionTypes {
    public enum Code {
        SUCCESS,
        ERROR,
    }

    public enum ActionType {
        INVALID,
        LOGIN_USER,
        FIND_MATCH,
        JOIN_ROOM,
        CURRENT_GAME_DATA_RESPONSE,
        UPDATE_GAME_POSITION_REQUEST,
        START_SINGLE_MATCH_REQUEST,
        RESET_GAME_POSITION_REQUEST,
        GAME_EVENT_TIMEOUT,
        GAME_EVENT_WIN,
        SCORES
    }

    public static ActionType getActionTypeFromMessage(String message) {
        ActionType type = ActionType.INVALID;

        try {
            type = Enum.valueOf(ActionTypes.ActionType.class, message.split(";")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }
}
