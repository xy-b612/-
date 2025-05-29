package qqweb.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import qqweb.entity.Message;
import qqweb.service.ChatService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储在线用户的会话: key=userId, value=WebSocketSession
    private static final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    @Autowired
    private ChatService chatService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session属性中获取用户ID（需要在握手拦截器中设置）
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId != null) {
            onlineUsers.put(userId, session);
            // 发送未读消息
            sendUnreadMessages(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析消息
        Message msg = objectMapper.readValue(message.getPayload(), Message.class);
        msg.setSenderId((Integer) session.getAttributes().get("userId"));

        // 保存消息
        chatService.sendMessage(msg);

        // 实时推送
        WebSocketSession receiverSession = onlineUsers.get(msg.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(message);
            // 标记消息为已读
            chatService.markMessageAsRead(msg.getMsgId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId != null) {
            onlineUsers.remove(userId);
            chatService.logout(userId);
        }
    }

    private void sendUnreadMessages(Integer userId, WebSocketSession session) throws IOException {
        for (Message msg : chatService.getUnreadMessages(userId)) {
            String jsonMsg = objectMapper.writeValueAsString(msg);
            session.sendMessage(new TextMessage(jsonMsg));
            // 标记消息为已读
            chatService.markMessageAsRead(msg.getMsgId());
        }
    }
}