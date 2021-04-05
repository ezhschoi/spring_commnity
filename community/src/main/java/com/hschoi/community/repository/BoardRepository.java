package com.hschoi.community.repository;

import com.hschoi.community.domain.Board;
import com.hschoi.community.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByUser(User user);
}

