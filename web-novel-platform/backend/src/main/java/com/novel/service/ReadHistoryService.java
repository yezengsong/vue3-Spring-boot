package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.response.ReadHistoryDTO;
import com.novel.entity.ReadHistory;

import java.util.List;

public interface ReadHistoryService extends IService<ReadHistory> {
    
    /**
     * 记录阅读历史
     */
    void recordReadHistory(Long userId, Long novelId, Long chapterId);
    
    /**
     * 获取用户的阅读历史列表（每本小说最新一条）
     */
    List<ReadHistoryDTO> getUserReadHistory(Long userId);
}
