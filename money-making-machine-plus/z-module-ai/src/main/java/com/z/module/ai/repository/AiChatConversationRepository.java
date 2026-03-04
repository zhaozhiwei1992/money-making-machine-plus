package com.z.module.ai.repository;

import com.z.module.ai.domain.chat.AiChatConversationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Chat Conversation Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiChatConversationRepository extends JpaRepository<AiChatConversationDO, Long> {

    /**
     * Find by user id
     *
     * @param userId user id
     * @return list of AI chat conversations
     */
    List<AiChatConversationDO> findByUserId(Long userId);

    /**
     * Find by user id and pinned
     *
     * @param userId user id
     * @param pinned pinned status
     * @return list of AI chat conversations
     */
    List<AiChatConversationDO> findByUserIdAndPinned(Long userId, Boolean pinned);
}
