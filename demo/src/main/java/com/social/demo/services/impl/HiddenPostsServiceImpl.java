package com.social.demo.services.impl;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.HiddenPost;
import com.social.demo.models.Posts;
import com.social.demo.models.Users;
import com.social.demo.payload.request.HiddenRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.HiddenPostsRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.HiddenPostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class HiddenPostsServiceImpl implements HiddenPostsService {
    @Autowired
    PostsRepository postsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    HiddenPostsRepository hiddenPostsRepository;

    @Override
    public CommonResponse<Object> hiddenPost(HiddenRequest dto, String username) {
        CommonResponse<Object> response = new CommonResponse<>();
        Optional<Posts> postsOptional = postsRepository.findById(dto.getId());
        if (postsOptional.isEmpty()) {
            response.setMessage(EcodeConstant.NULL_MSG);
            response.setEcode(EcodeConstant.NULL);
            return response;
        }
        Optional<Users> onlineUser = usersRepository.findByUsername(username);
        if (!onlineUser.isPresent()) {
            response.setMessage(EcodeConstant.USER_NOT_FOUND);
            response.setEcode(EcodeConstant.USER_NOT_FOUND_MSG);
            return response;
        }

        Optional<HiddenPost> hiddenPost =
                hiddenPostsRepository.findByPostIdAndUserId(dto.getId(), onlineUser.get().getId());
        if (hiddenPost.isPresent()) {
            response.setMessage(EcodeConstant.POST_STT_ALREADY_HIDDEN_MSG);
            response.setEcode(EcodeConstant.POST_STT_ALREADY_HIDDEN);
            return response;
        }
        HiddenPost newHiddenPost = new HiddenPost();
        final String hiddenPostId = "hiddenPost_" + UUID.randomUUID().toString().replace("-", "");
        newHiddenPost.setId(hiddenPostId);
        newHiddenPost.setPostId(dto.getId());
        newHiddenPost.setUserId(onlineUser.get().getId());
        newHiddenPost.setStatus("1");
        Calendar c = Calendar.getInstance();c.add(Calendar.HOUR, 7);Date now = c.getTime();
        newHiddenPost.setHiddenDate(now);
        hiddenPostsRepository.save(newHiddenPost);
        response.setEcode(EcodeConstant.SUCCESS);
        response.setEcode(EcodeConstant.SUCCESS_MSG);
        return response;
    }
}
