package com.group7.onlinelibrary.controller;

import com.group7.onlinelibrary.entity.User;
import com.group7.onlinelibrary.repository.UserRepository;
import com.group7.onlinelibrary.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final UserRepository userRepository;

    @PostMapping("/borrow/{bookId}")
    @PreAuthorize("hasAnyAuthority('ROLE_READER', 'ROLE_LIBRARIAN')")
    public String processBorrow(
            @PathVariable("bookId") Integer bookId,
            @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam("expectedReturnDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate expectedReturnDate,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để mượn sách!");
            return "redirect:/books";
        }

        try {
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản."));

            borrowService.createBorrowRequest(user.getId(), Collections.singletonList(bookId), startDate, expectedReturnDate);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký mượn sách thành công! Vui lòng chờ thủ thư phê duyệt.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mượn sách thất bại: " + e.getMessage());
        }

        return "redirect:/books";
    }
}
