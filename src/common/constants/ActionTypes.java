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
        GET_DATA_FOR_ROOM_REQUEST,
        GET_DATA_FOR_ROOM_RESPONSE,
        CURRENT_GAME_DATA_RESPONSE,
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
