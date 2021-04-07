package com.hschoi.community;

import com.hschoi.community.domain.Board;
import com.hschoi.community.domain.User;
import com.hschoi.community.domain.enums.BoardType;
import com.hschoi.community.repository.BoardRepository;
import com.hschoi.community.repository.UserRepository;
import com.hschoi.community.resolver.UserArgumentResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

// CommandLineRunner 인터페이스
// UserArgumentResolver를 적용하려면 WebMvcConfigurerAdapter를 상속받아야 함
@SpringBootApplication
public class BootWebApplication extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(BootWebApplication.class, args);
    }
    
    @Autowired
    private UserArgumentResolver userArgumentResolver;
    
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver>
    		argumentResolvers) {
    	argumentResolvers.add(userArgumentResolver);
    }
    

    @Bean
    public CommandLineRunner runner(UserRepository userRepository,
                                    BoardRepository boardRepository) throws Exception {
        // 스프링은 빈으로 생성된 메소드에 파라미터로 DI(Dependency Injection) 시키는 메커니즘
        // 생성자를 통해 의존성을 주입
        // CommandLineRunner을 빈으로 등록 후 UserRepository, BoardRepository를 주입

        // User 객체를 빌더 패턴을 사용하여 생성한 후 주입받은 UserRepository를 사용하여 User 객체를 저장
        return (args) -> {
            User user = userRepository.save(User.builder()
                    .name("hschoi")
                    .password("1111")
                    .email("hschoi@aaa.com")
                    .createdDate(LocalDateTime.now())
                    .build()
            );

            // 페이징 처리 테스트를 위해 위와 동일하개 빌터 패턴 사용
            // IntStream의 rangeClosed를 사용하여 index 순서대로 Board 생성
            IntStream.rangeClosed(1, 200).forEach(index ->
                    boardRepository.save(Board.builder()
                            .title("게시글" + index)
                            .subTitle("순서" + index)
                            .content("게시글 내용")
                            .boardType(BoardType.free)
                            .createdDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .user(user).build())
            );
        };
    }
}
