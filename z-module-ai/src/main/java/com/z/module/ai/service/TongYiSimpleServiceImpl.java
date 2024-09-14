package com.z.module.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class TongYiSimpleServiceImpl extends AbstractTongYiServiceImpl {
  /**
   * 自动注入ChatClient、StreamingChatClient，屏蔽模型调用细节
   */
  private final ChatClient chatClient;

  @Autowired
  public TongYiSimpleServiceImpl(ChatClient chatClient) {
    this.chatClient = chatClient;
  }
    /**
    * 具体实现：
    */
  @Override
  public String completion(String message) {
    Prompt prompt = new Prompt(new UserMessage(message));
    return chatClient.call(prompt).getResult().getOutput().getContent();
  }
}