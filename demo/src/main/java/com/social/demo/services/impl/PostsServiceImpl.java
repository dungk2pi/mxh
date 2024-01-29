package com.social.demo.services.impl;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Posts;
import com.social.demo.models.Users;
import com.social.demo.payload.request.HiddenRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.*;
import com.social.demo.services.PostsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostsServiceImpl implements PostsService {

    private final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MediaRepository mediaRepository;

    @Autowired
    TagRepository tagRepository;


    @Autowired
    ReactRepository reactRepository;

    @Autowired
    SavedPostRepository savedPostRepository;

    @Autowired
     HiddenPostsRepository hiddenPostRepository;


    @Override
    public CommonResponse<Object> deletePostById(String id, String username) {
        CommonResponse<Object> response = new CommonResponse<>();

        Optional<Posts> postsOptional = postsRepository.findById(id);
        if (postsOptional.isEmpty()) {
            log.error("Không tìm thấy postID = " + id);
            response.setMessage(EcodeConstant.NULL_MSG);
            response.setEcode(EcodeConstant.NULL);
        } else {
            Optional<Users> onlineUser = usersRepository.findByUsername(username);
            if (onlineUser.isPresent() && onlineUser.get().getId().equals(postsOptional.get().getUserId())) {
                postsRepository.deleteById(id);// xoá bài viết
                savedPostRepository.deleteByPostId(id);// xoá saved Post
                commentRepository.deleteByPostId(id);// xoá comment
                reactRepository.deleteByReferenceId(id);// xoá react
                response.setMessage(EcodeConstant.SUCCESS_MSG);
                response.setEcode(EcodeConstant.SUCCESS);
            } else {
                response.setMessage(EcodeConstant.NOT_ACCESS);
                response.setEcode(EcodeConstant.NOT_ACCESS_MSG);
            }
        }
        return response;
    }
}
