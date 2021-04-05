package com.hschoi.community.controller;

import com.hschoi.community.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// API URI 경로
@Controller
@RequestMapping("/board")
public class BoardController {
    // boardService 의존성
    @Autowired
    BoardService boardService;

    //매핑경로를 중괄호를 사용하여 여러개 받기
    @GetMapping({"", "/"})
    public String board(@RequestParam(value = "idx", defaultValue = "0")
                                Long idx, Model model) {
        // @RequestParam를 사용하여 idx 파라미터를 필수로 받음
        // 디폴트값은 0
        // idx값을 0으로 조회하면 board값은 null로 반환
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "/board/form";
    }

    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, Model model)
    {
        // @PageableDefault는 파라미터인 size, sord, direction 등을 사용하여 페이징 처리에 대한 규약 정의
        model.addAttribute("boardList", boardService.findBoardList(pageable));
        // src/resource/templates를 기준으로 데이터를 바인딩할 타깃의 뷰 경로 지정
        return "/board/list";
    }
}
