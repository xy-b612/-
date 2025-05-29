package qqweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import qqweb.dao.FriendDao;
import qqweb.dao.MessageDao;
import qqweb.dao.UserDao;
import qqweb.entity.Friend;
import qqweb.entity.Message;
import qqweb.entity.User;
import qqweb.service.ChatService;

import java.util.Date;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private FriendDao friendDao;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            userDao.updateOnlineStatus(user.getUserId(), 1);
            return user;
        }
        return null;
    }

    @Override
    public void logout(Integer userId) {
        userDao.updateOnlineStatus(userId, 0);
    }

    @Override
    public void sendMessage(Message message) {
        message.setSendTime(new Date());
        messageDao.save(message);
    }

    @Override
    public List<Message> getChatHistory(Integer userId, Integer friendId) {
        return messageDao.findByUsers(userId, friendId);
    }

    @Override
    public List<Message> getUnreadMessages(Integer userId) {
        return messageDao.findUnreadMessages(userId);
    }

    @Override
    public List<Friend> getFriends(Integer userId) {
        return friendDao.findFriendsByUserId(userId);
    }

    @Override
    public void addFriend(Integer userId, Integer friendUserId) {
        friendDao.addFriend(userId, friendUserId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendUserId) {
        friendDao.deleteFriend(userId, friendUserId);
    }

    @Override
    public void markMessageAsRead(Long msgId) {
        messageDao.markMessageAsRead(msgId);
    }
}