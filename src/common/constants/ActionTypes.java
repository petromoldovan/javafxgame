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
        GET_MULTIPLAYER_MATCH_INFO,
        JOIN_ROOM
    }

    public static ActionType getActionTypeFromMessage(String message) {
        ActionType type = ActionType.INVALID;

        try {
            type = Enum.valueOf(ActionTypes.ActionType.class, message.split(";")[0]);
        } catch (Exception e) {
            System.out.println("ERROR: getActionTypeFromMessage#" + e.getMessage());
        }

        return type;
    }
}
