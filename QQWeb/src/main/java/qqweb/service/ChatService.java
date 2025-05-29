package qqweb.service;

import qqweb.entity.Friend;
import qqweb.entity.Message;
import qqweb.entity.User;

import java.util.List;

public interface ChatService {
    User login(String username, String password);
    void logout(Integer userId);
    void sendMessage(Message message);
    List<Message> getChatHistory(Integer userId, Integer friendId);
    List<Message> getUnreadMessages(Integer userId);
    List<Friend> getFriends(Integer userId);
    void addFriend(Integer userId, Integer friendUserId);
    void deleteFriend(Integer userId, Integer friendUserId);
    void markMessageAsRead(Long msgId);
}