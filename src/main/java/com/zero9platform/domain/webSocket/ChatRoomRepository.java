package com.zero9platform.domain.webSocket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUserId(Long userId);

    Page<ChatRoom> findAllByAdminIdOrderByLastMessageTimeDesc(Long adminId, Pageable pageable);
}
