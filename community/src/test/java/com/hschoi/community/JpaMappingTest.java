package com.hschoi.community;

import com.hschoi.community.domain.Board;
import com.hschoi.community.domain.User;
import com.hschoi.community.domain.enums.BoardType;
import com.hschoi.community.repository.BoardRepository;
import com.hschoi.community.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// JUnit 내장 러너 대신 정의된 클래스 호출
// JPA 테스트 전용 어노테이션 엔티티간 관계설정 및 기능 테스트를 간으하게 도움
@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaMappingTest {
    private final String boardTestTitle = "테스트";
    private final String email = "test@gmail.com";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    // 각 테스트가 실행되기 전에 실행될 메소드
    @Before
    public void init() {
        User user = userRepository.save(
                User.builder()
                .name("hschoi")
                .password("aaaa")
                .email(email)
                .createdDate(LocalDateTime.now())
                .build()
        );

        boardRepository.save(
                Board.builder()
                .title(boardTestTitle)
                .subTitle("sub title")
                .content("content")
                .boardType(BoardType.free)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .user(user).build()
        );
    }

    @Test
    public void testCreate() {
        // init()에서 저장된 user를 email로 조회
        User user = userRepository.findByEmail(email);
        assertThat(user.getName(), is("hschoi"));
        assertThat(user.getPassword(), is("aaaa"));
        assertThat(user.getEmail(), is(email));

        Board board = boardRepository.findByUser(user);
        assertThat(board.getTitle(), is(boardTestTitle));
        assertThat(board.getSubTitle(), is("sub title"));
        assertThat(board.getContent(), is("content"));
        assertThat(board.getBoardType(), is(BoardType.free));
    }
}
