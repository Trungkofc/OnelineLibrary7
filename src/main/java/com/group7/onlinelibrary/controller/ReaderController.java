package com.group7.onlinelibrary.controller;

import com.group7.onlinelibrary.entity.BorrowRequest;
import com.group7.onlinelibrary.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reader")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_READER')")
public class ReaderController {

    private final BorrowService borrowService;

    @GetMapping("/history")
    public String getBorrowHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        List<BorrowRequest> history = borrowService.getHistoryByUsername(userDetails.getUsername());
        model.addAttribute("history", history);
        return "reader/history";
    }
}
