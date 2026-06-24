package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.response.ReadHistoryDTO;
import com.novel.security.JwtUserDetails;
import com.novel.service.ReadHistoryService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReadHistoryController {
    
    private final ReadHistoryService readHistoryService;
    
    public ReadHistoryController(ReadHistoryService readHistoryService) {
        this.readHistoryService = readHistoryService;
    }
    
    /**
     * 记录阅读历史
     */
    @PostMapping("/novel/{novelId}/chapter/{chapterId}/history")
    public Result<Void> recordHistory(@PathVariable Long novelId,
                                       @PathVariable Long chapterId,
                                       Principal principal) {
        Long userId = getCurrentUserId(principal);
        readHistoryService.recordReadHistory(userId, novelId, chapterId);
        return Result.success();
    }
    
    /**
     * 获取用户的阅读历史列表
     */
    @GetMapping("/user/history")
    public Result<List<ReadHistoryDTO>> getUserReadHistory(Principal principal) {
        Long userId = getCurrentUserId(principal);
        List<ReadHistoryDTO> histories = readHistoryService.getUserReadHistory(userId);
        return Result.success(histories);
    }
    
    private Long getCurrentUserId(Principal principal) {
        if (principal instanceof org.springframework.security.core.Authentication) {
            org.springframework.security.core.Authentication auth = 
                (org.springframework.security.core.Authentication) principal;
            Object principalObj = auth.getPrincipal();
            if (principalObj instanceof JwtUserDetails) {
                return ((JwtUserDetails) principalObj).getId();
            }
        }
        return null;
    }
}
