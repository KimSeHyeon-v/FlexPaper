package org.zerock.FlexPaper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.dto.BundleDTO;
import org.zerock.FlexPaper.dto.FPUserDTO;
import org.zerock.FlexPaper.service.BundleService;
import org.zerock.FlexPaper.service.MemberService;
import org.zerock.FlexPaper.domain.FPUser;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/bundle")
public class BundleController {
    private final MemberService memberService;

    private final BundleService bundleService;

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal FPUser user,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "") String keyword,
                       Model model) {
        if (user == null) {
            return "redirect:/member/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Bundle> bundlePage = bundleService.searchUserBundles(user.getUsername(), keyword, pageable);

        model.addAttribute("bundlePage", bundlePage);
        model.addAttribute("keyword", keyword);  // 검색어 유지
        return "bundle/list";
    }

    // Bundle 생성 폼을 위한 GET 요청
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("bundleDTO", new BundleDTO()); // 빈 DTO 객체 전달
        return "bundle/create"; // 해당 뷰에서 폼에 바인딩
    }

    // Bundle 생성 폼을 제출한 후 처리하는 POST 요청
    @PostMapping("/save")
    public String createBundle(@AuthenticationPrincipal FPUser user, BundleDTO bundleDTO) {
        if (user != null) {
            bundleService.createBundle(bundleDTO, user); // BundleDTO와 사용자를 통해 Bundle 생성
            log.info("Created new bundle: " + bundleDTO.getTitle());
        } else {
            log.info("User is not authenticated");
            return "redirect:/member/login";
        }
        return "redirect:/bundle/list";
    }

    // 특정 Bundle을 조회하고, 해당 Bundle에 포함된 Canvas 들도 가져옴
    @GetMapping("/detail/{bno}")
    public String getBundleDetails(@PathVariable("bno") Long bno, Model model) {
        // bno를 통해 Bundle과 해당 Bundle에 포함된 Canvas 목록을 가져옴
        BundleDTO bundleDTO = bundleService.getBundleWithCanvas(bno);

        // BundleDTO를 모델에 추가하여 view에 전달
        model.addAttribute("bundleDTO", bundleDTO);

        // 해당 정보를 담고 있는 view를 반환
        return "bundle/detail"; // Bundle의 상세 정보를 보여줄 페이지
    }

    @GetMapping("/modify/{bno}")
    public String modifyForm(@PathVariable("bno") Long bno, Model model) {
        BundleDTO bundleDTO = bundleService.getBundleWithCanvas(bno); // 이 메서드를 사용하지 않아도 되지만 확장성을 고려...
        model.addAttribute("bundleDTO", bundleDTO);
        return "bundle/modify";
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute BundleDTO bundleDTO) {
        bundleService.modifyBundle(bundleDTO);
        return "redirect:/bundle/list";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno) {
        bundleService.removeBundle(bno);
        return "redirect:/bundle/list";
    }

}
