package com.group7.onlinelibrary.controller;

import com.group7.onlinelibrary.entity.BorrowRequest;
import com.group7.onlinelibrary.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/librarian")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_LIBRARIAN', 'ROLE_ADMIN')")
public class LibrarianController {

    private final BorrowService borrowService;

    @GetMapping("/requests")
    public String getPendingRequests(Model model) {
        List<BorrowRequest> requests = borrowService.getPendingRequests();
        List<BorrowRequest> activeBorrowings = borrowService.getActiveRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("activeBorrowings", activeBorrowings);
        return "librarian/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            borrowService.approveRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã phê duyệt yêu cầu mượn sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phê duyệt thất bại: " + e.getMessage());
        }
        return "redirect:/librarian/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            borrowService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối yêu cầu mượn sách!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Từ chối thất bại: " + e.getMessage());
        }
        return "redirect:/librarian/requests";
    }

    @PostMapping("/requests/{id}/return")
    public String returnRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            BorrowRequest req = borrowService.recordReturn(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ghi nhận trả sách thành công! Tổng phí thuê là " + String.format("%,.0f", req.getTotalFee()) + " VND.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ghi nhận trả sách thất bại: " + e.getMessage());
        }
        return "redirect:/librarian/requests";
    }

    @GetMapping("/customer-history")
    public String getCustomerHistory(@RequestParam(value = "search", required = false) String search, Model model) {
        List<BorrowRequest> history;
        if (search != null && !search.trim().isEmpty()) {
            history = borrowService.searchRequestsByReader(search);
        } else {
            history = borrowService.getAllRequests();
        }
        model.addAttribute("history", history);
        model.addAttribute("search", search);
        return "librarian/customer-history";
    }
}
