package com.wcms.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AIReq {
    // 顶层字段：模型名称
    private String model;
    // 顶层字段：消息列表（元素为内部类 Message）
    private List<Message> messages;

    // ==================== 静态内部类：对应 messages 数组元素 ====================
    @Getter
    @Setter
    public static class Message {
        private String role;
        // 消息内容列表（元素为内部类 ContentItem）
        private List<ContentItem> content;
    }

    // ==================== 静态内部类：对应 content 数组元素 ====================
    @Getter
    @Setter
    public static class ContentItem {
        private String type;
        private String text;
        // 图片URL信息（对应嵌套对象，类型为内部类 ImageUrl）
        private ImageUrl image_url;
    }

    // ==================== 静态内部类：对应 image_url 嵌套对象 ====================
    @Getter
    @Setter
    public static class ImageUrl {
        private String url;
    }

    public static AIReq build(String model, String content, String url) {
        AIReq aiReq = new AIReq();
        aiReq.setModel(model);
        AIReq.Message message = new AIReq.Message();
        message.setRole("user");
        AIReq.ContentItem textItem = new AIReq.ContentItem();
        textItem.setType("text");
        textItem.setText(content);
        AIReq.ContentItem imageItem = new AIReq.ContentItem();
        imageItem.setType("image_url");
        AIReq.ImageUrl imageUrl = new AIReq.ImageUrl();
        imageUrl.setUrl(url);
        imageItem.setImage_url(imageUrl);
        List<ContentItem> contentList = new ArrayList<>();
        contentList.add(imageItem);
        contentList.add(textItem);
        message.setContent(contentList);
        aiReq.setMessages(Collections.singletonList(message));
        return aiReq;
    }

}
