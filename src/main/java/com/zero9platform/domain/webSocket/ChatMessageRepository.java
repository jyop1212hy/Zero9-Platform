package com.zero9platform.domain.webSocket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    long countByRoomIdAndReceiverIdAndIsReadFalse(Long roomId, Long receiverId);

    List<ChatMessage> findByRoomIdAndReceiverIdAndIsReadFalse(Long roomId, Long receiverId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}
