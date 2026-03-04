package com.z.module.ai.repository;

import com.z.module.ai.domain.chat.AiChatMessageDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * AI Chat Message Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessageDO, Long> {

    /**
     * Find by conversation id
     *
     * @param conversationId conversation id
     * @return list of AI chat messages
     */
    List<AiChatMessageDO> findByConversationId(Long conversationId);

    /**
     * Select count map by conversation id
     *
     * @param conversationIds collection of conversation ids
     * @return map of conversation id to message count
     */
    @Query("SELECT m.conversationId as conversationId, COUNT(m) as count " +
           "FROM AiChatMessageDO m " +
           "WHERE m.conversationId IN :conversationIds " +
           "GROUP BY m.conversationId")
    Map<Long, Integer> selectCountMapByConversationId(@Param("conversationIds") Collection<Long> conversationIds);
}
