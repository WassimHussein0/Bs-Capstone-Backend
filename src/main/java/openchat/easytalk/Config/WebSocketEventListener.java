package openchat.easytalk.Config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import openchat.easytalk.User.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Getter
@Slf4j
public class WebSocketEventListener {

    public static final Map<String, String> connectedUsers = new ConcurrentHashMap<>();
    private final SimpMessageSendingOperations messageSendingOperations;
    private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userId = accessor.getFirstNativeHeader("userId");
        connectedUsers.put(sessionId, userId);

        messageSendingOperations
                .convertAndSend("/topic/connections", getConnectionEventNotifier("connect", userId));

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = connectedUsers.remove(headerAccessor.getSessionId());

        messageSendingOperations
                .convertAndSend("/topic/connections", getConnectionEventNotifier("disconnect", userId));

    }

    private Map<String, Object> getConnectionEventNotifier(String eventType, String userId) {
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("eventType", eventType);
        toReturn.put("userId", userId);

        Long currentTimestamp = System.currentTimeMillis();
        toReturn.put("eventTimestamp", currentTimestamp);

        userService.setLastSeen(userId, currentTimestamp);

        return toReturn;
    }


}
