package qqweb.dao;

import org.apache.ibatis.annotations.Param;
import qqweb.entity.Message;

import java.util.List;

public interface MessageDao {
    void save(Message message);
    List<Message> findByUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
    List<Message> findUnreadMessages(@Param("receiverId") Integer receiverId);
    void markMessageAsRead(@Param("msgId") Long msgId);
}