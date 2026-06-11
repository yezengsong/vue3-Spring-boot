package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Chapter;
import com.novel.entity.Comment;
import com.novel.entity.User;
import com.novel.mapper.CommentMapper;
import com.novel.service.ChapterService;
import com.novel.service.CommentService;
import com.novel.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    private final UserService userService;
    private final ChapterService chapterService;
    
    public CommentServiceImpl(UserService userService, ChapterService chapterService) {
        this.userService = userService;
        this.chapterService = chapterService;
    }
    
    @Override
    public List<CommentDTO> getCommentsByChapterId(Long chapterId) {
        // 查询一级评论（已审核通过的）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getChapterId, chapterId)
               .isNull(Comment::getParentId)  // 一级评论
               .eq(Comment::getStatus, 1)      // 已审核通过
               .orderByDesc(Comment::getCreateTime);
        
        List<Comment> comments = list(wrapper);
        
        return comments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(Long chapterId, Long userId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setChapterId(chapterId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setLikeCount(0);
        comment.setStatus(0);  // 待审核
        
        save(comment);
        return comment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment replyComment(Long chapterId, Long userId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setChapterId(chapterId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setLikeCount(0);
        comment.setStatus(0);  // 待审核
        
        save(comment);
        return comment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId) {
        baseMapper.update(null,
            new LambdaUpdateWrapper<Comment>()
                .setSql("like_count = like_count + 1")
                .eq(Comment::getId, commentId));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComment(Long commentId, Integer status) {
        Comment comment = getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setStatus(status);
        updateById(comment);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        // 删除子评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, commentId);
        remove(wrapper);
        
        // 删除评论
        removeById(commentId);
    }
    
    @Override
    public List<CommentDTO> getCommentsByNovelId(Long novelId) {
        // 获取该小说的所有章节 ID
        List<Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
        if (chapters == null || chapters.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        List<Long> chapterIds = chapters.stream()
            .map(Chapter::getId)
            .collect(Collectors.toList());
        
        // 查询这些章节的评论（待审核和已审核的）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Comment::getChapterId, chapterIds)
               .isNull(Comment::getParentId)  // 一级评论
               .in(Comment::getStatus, 0, 1)  // 待审核或已通过
               .orderByDesc(Comment::getCreateTime);
        
        List<Comment> comments = list(wrapper);
        
        return comments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setChapterId(comment.getChapterId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setLikeCount(comment.getLikeCount());
        dto.setStatus(comment.getStatus());
        dto.setCreateTime(comment.getCreateTime());
        
        // 获取用户信息
        User user = userService.getById(comment.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());
        }
        
        return dto;
    }
}
