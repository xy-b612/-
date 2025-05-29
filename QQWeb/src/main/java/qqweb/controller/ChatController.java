package qqweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import qqweb.entity.Friend;
import qqweb.entity.Message;
import qqweb.entity.User;
import qqweb.service.ChatService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/login")
    public User login(@RequestParam String username,
                      @RequestParam String password,
                      HttpSession session) {
        User user = chatService.login(username, password);
        if (user != null) {
            session.setAttribute("userId", user.getUserId());
            return user;
        }
        return null;
    }

    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam Integer friendId,
                                    HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return chatService.getChatHistory(userId, friendId);
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        // 图片上传逻辑（需要实现）
        return "image_url.jpg";
    }

    @GetMapping("/friends")
    public List<Friend> getFriends(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return chatService.getFriends(userId);
    }

    @PostMapping("/addFriend")
    public void addFriend(@RequestParam Integer friendUserId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        chatService.addFriend(userId, friendUserId);
    }

    @PostMapping("/deleteFriend")
    public void deleteFriend(@RequestParam Integer friendUserId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        chatService.deleteFriend(userId, friendUserId);
    }
}