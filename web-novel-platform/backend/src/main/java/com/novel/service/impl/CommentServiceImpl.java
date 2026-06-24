package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Chapter;
import com.novel.entity.Comment;
import com.novel.entity.Novel;
import com.novel.entity.User;
import com.novel.mapper.CommentMapper;
import com.novel.service.ChapterService;
import com.novel.service.CommentService;
import com.novel.service.NovelService;
import com.novel.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    private final UserService userService;
    private final ChapterService chapterService;
    private final NovelService novelService;
    
    public CommentServiceImpl(UserService userService, ChapterService chapterService, NovelService novelService) {
        this.userService = userService;
        this.chapterService = chapterService;
        this.novelService = novelService;
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
        User user = userService.getById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new RuntimeException("您的账号已被封禁，无法发表评论");
        }
        
        Comment comment = new Comment();
        comment.setChapterId(chapterId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setLikeCount(0);
        comment.setStatus(1);  // 自动审核通过
        
        save(comment);
        return comment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment replyComment(Long chapterId, Long userId, CommentRequest request) {
        User user = userService.getById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new RuntimeException("您的账号已被封禁，无法发表评论");
        }
        
        Comment comment = new Comment();
        comment.setChapterId(chapterId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setLikeCount(0);
        comment.setStatus(1);  // 自动审核通过
        
        save(comment);
        return comment;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId) {
        baseMapper.update(null,
            new LambdaUpdateWrapper<Comment>()
                .setSql("like_count = like_count + 1")
                .eq(Comment::getCommentId, commentId));
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
            .map(Chapter::getChapterId)
            .collect(Collectors.toList());
        
        // 查询这些章节的一级评论（不包括二级评论）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Comment::getChapterId, chapterIds)
               .isNull(Comment::getParentId)  // 只查询一级评论
               .orderByAsc(Comment::getCommentId);
        
        List<Comment> comments = list(wrapper);
        
        return comments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        // 查询该用户的所有评论（包括一级和二级评论）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId)
               .orderByDesc(Comment::getCreateTime);
        
        List<Comment> comments = list(wrapper);
        
        return comments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
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
        
        // 获取章节和小说信息
        Chapter chapter = chapterService.getById(comment.getChapterId());
        if (chapter != null) {
            dto.setChapterTitle(chapter.getTitle());
            dto.setNovelId(chapter.getNovelId());
            
            Novel novel = novelService.getById(chapter.getNovelId());
            if (novel != null) {
                dto.setNovelTitle(novel.getTitle());
            }
        }
        
        // 获取回复信息（如果是回复评论）
        if (comment.getParentId() != null) {
            Comment parentComment = getById(comment.getParentId());
            if (parentComment != null) {
                User parentUser = userService.getById(parentComment.getUserId());
                if (parentUser != null) {
                    dto.setReplyToUsername(parentUser.getUsername());
                }
            }
        }
        
        // 获取该评论的回复列表（一级评论才需要）
        if (comment.getParentId() == null) {
            dto.setReplies(getRepliesByParentId(comment.getCommentId()));
        }
        
        return dto;
    }
    
    private List<CommentDTO> getRepliesByParentId(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .eq(Comment::getStatus, 1)  // 已审核通过
               .orderByAsc(Comment::getCreateTime);
        
        List<Comment> replies = list(wrapper);
        return replies.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
