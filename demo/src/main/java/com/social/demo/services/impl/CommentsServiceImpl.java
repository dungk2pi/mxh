package com.social.demo.services.impl;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.*;
import com.social.demo.payload.request.CreateCommentRequest;
import com.social.demo.payload.request.GetAllCommentRequest;
import com.social.demo.payload.request.GetCommentByReferenceIdRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.*;
import com.social.demo.services.CommentsService;
import com.social.demo.services.FirebaseMessageService;
import com.social.demo.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
@Transactional
public class CommentsServiceImpl implements CommentsService {
    private final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private FirebaseMessageService firebaseMessageService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    ReactRepository reactRepository;
    @Override
    public CommonResponse<Object> createComment(CreateCommentRequest dto, String userName)  {
        CommonResponse<Object> response = new CommonResponse<>();
        Map<String, String> data = new HashMap<>();
        try {
            Optional<Users> onlineUser = usersRepository.findByUsername(userName);
            final String cmtId = "cmt_" + UUID.randomUUID().toString().replace("-", "");
            // save ảnh trong cmt
            if(dto.getImages() != null && !dto.getImages().isEmpty()){
                log.info("{} | Save list of images for this new cmt", cmtId);
                ArrayList<Media> listImages = new ArrayList<Media>();
                for(HashMap<String, String> image : dto.getImages()){
                    Media media = new Media();
                    String mediaId = "media_" + UUID.randomUUID().toString().replace("-", "");
                    media.setId(mediaId);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.HOUR, 7);
                    Date now = c.getTime();
                    media.setCreateDate(now);
                    media.setDescription("");
                    media.setMediaUrl(image.get("url"));
                    media.setReferenceId(cmtId);
                    media.setSeq(Integer.parseInt(image.get("seq")));
                    media.setStatus(EcodeConstant.MEDIA_STT_ACTIVE);
                    media.setType(image.get("type"));
                    listImages.add(media);
                }
                mediaRepository.saveAll(listImages);
                log.info("CommentId={} |Save list of images success. List size = {}", cmtId, listImages.size());
            }

            if(dto.getContent() != null && !dto.getContent().isEmpty()){
                ArrayList<HashMap<String, String>> listUserTagRequest = Utils.getUsertag_frContent(dto.getContent());
                log.info("tag list {}", listUserTagRequest.size());
                if (listUserTagRequest != null && !listUserTagRequest.isEmpty()) {
                    log.info("{} | Save list of tag for this new comment", cmtId);
                    ArrayList<Tag> lstUserTags = new ArrayList<Tag>();
                    log.info(listUserTagRequest.toString());
                    for (HashMap<String, String> userTag : listUserTagRequest) {
                        Tag tag = new Tag();
                        String tagId = "tag_" + UUID.randomUUID().toString().replace("-", "");
                        tag.setId(tagId);
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.HOUR, 7);
                        Date now = c.getTime();
                        tag.setCreateDate(now);
                        tag.setReferenceId(cmtId);
                        tag.setStatus("1");
                        tag.setUserReq(onlineUser.get().getId());
                        tag.setSeq(Integer.parseInt(userTag.get("seq")));
                        tag.setUserRes(userTag.get("userId"));
                        lstUserTags.add(tag);
                    }
                    tagRepository.saveAll(lstUserTags);
                    log.info("CommentId={} | Save list of hashtag success", cmtId);
                }
            }
            // cập nhật lại lastReactDate
            Posts posts = postsRepository.findPostsById(dto.getPostId());
            if (posts == null ) {
                response.setEcode(EcodeConstant.EXCEPTION);
                response.setData(null);
                response.setMessage(EcodeConstant.NULL_MSG);
                return response;
            }
            Comments cmt = new Comments();
            cmt.setId(cmtId);
            cmt.setContent(dto.getContent() == null ? "" : dto.getContent());
            cmt.setUserId(String.valueOf(onlineUser.get().getId()));
            cmt.setStatus("active");
            if (dto.getLevel() == 1 && dto.getReferenceId() == null) {
                dto.setReferenceId(dto.getPostId());
            }
            cmt.setLevel(dto.getLevel());
            cmt.setPostId(dto.getPostId());
            cmt.setReferenceId(dto.getReferenceId());
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 7);
            Date now = c.getTime();
            cmt.setCreateDate(now);
            cmt.setEditDate(now);
            commentRepository.save(cmt);
            log.info("Save new cmt to DB success | cmtId = {}", cmtId);
            data.put("cmtId", cmtId);
            response.setData(data);
            response.setEcode(EcodeConstant.SUCCESS);
            response.setMessage(EcodeConstant.SUCCESS_MSG);
            posts.setLastReactDate(now);
            postsRepository.save(posts);

            // thêm thông báo cho thằng được reply
            Notifications _notifications;
            final String notificationId = "notification_" + UUID.randomUUID().toString().replace("-", "");
            _notifications = new Notifications();
            _notifications.setId(notificationId);
            _notifications.setUserReqId(onlineUser.get().getId());// thằng gửi
            String idNotifier;
            if (dto.getReferenceId().equals(dto.getPostId())) { // đây laf comment chính, trực tiếp dưới bài viết
                // thằng nhận sẽ là thằng chủ bài viết
                _notifications.setUserRevId(posts.getUserId());
            } else { // đây là comment không phải cmt chính, thằng nhận sẽ là thằng đc reply
                Optional<Comments> parentCmt = commentRepository.findById(dto.getReferenceId());
                if (parentCmt.isEmpty()) {
                    log.error("cmt reply không tồn tại");
                    response.setEcode(EcodeConstant.NULL);
                    response.setMessage(EcodeConstant.NULL_MSG);
                    return response;
                } else  _notifications.setUserRevId(parentCmt.get().getUserId());
            }
            _notifications.setReferenceId(posts.getId());


            if(dto.getReferenceId().equals(dto.getPostId())){
                Optional<Users> postUser = usersRepository.findById(posts.getId());
                firebaseMessageService.sendNotificationByToken(postUser.get().getRecipientToken(),
                        onlineUser.get().getUsername() + " đã bình luận bài viết của bạn", "", onlineUser.get().getAvatar());

                _notifications.setContent("đã bình luận bài viết của bạn");

            } else {
                Comments comments = commentRepository.findById(cmt.getReferenceId()).get();
                Optional<Users> cmtReplyUser = usersRepository.findById(comments.getReferenceId());
                firebaseMessageService.sendNotificationByToken(cmtReplyUser.get().getRecipientToken(),
                        onlineUser.get().getUsername() + " đã trả lời bình luận của bạn", "", onlineUser.get().getAvatar());
                _notifications.setContent("đã trả lời bình luận của bạn");
            }
            _notifications.setRead(false);
            _notifications.setStatus(1);

            Calendar cNoti = Calendar.getInstance();
            cNoti.add(Calendar.HOUR, 7);
            Date nowNoti = cNoti.getTime();
            _notifications.setCreateDate(nowNoti);
            _notifications.setEditDate(nowNoti);
            notificationRepository.save(_notifications);
            return response;
        } catch (Exception e) {
            log.error("Exception during create new comment {} |", e);
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setData(null);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

    @Override
    public CommonResponse<Object> getCmtByReferenceId(GetCommentByReferenceIdRequest dto, String userName) {
        CommonResponse<Object> response = new CommonResponse<>();
        try {
            Users onlineUser = usersRepository.findByUsername(userName).get();
            Page<Comments> pageComments;
            Pageable paging = PageRequest.of(dto.getPage(), 10);
            pageComments =  commentRepository.getByReferenceIdAndStatusOrderByCreateDate(dto.getReferenceId(),"active", paging);
            ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();
            List<Comments> _comment = pageComments.getContent();
            insertMediaAndAuthor(_comment, newArr);
            response.setData(newArr);
            response.setEcode(EcodeConstant.SUCCESS);
            response.setMessage(EcodeConstant.SUCCESS_MSG);
            return response;
        } catch (Exception e) {
            log.error("Exception during get comment {} |", e);
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

    @Override
    public CommonResponse<Object> deleteCommentById(String id, String username) {
        CommonResponse<Object> response = new CommonResponse<>();
        List<Comments> comments = commentRepository.findAllById(Collections.singleton(id));
        if (comments.isEmpty()) {
            log.error("Không tìm thấy commentId = " + id);
            response.setMessage(EcodeConstant.NULL_MSG);
            response.setEcode(EcodeConstant.NULL);
        } else {
            Optional<Users> onlineUser = usersRepository.findByUsername(username);
            if (onlineUser.isPresent() && onlineUser.get().getId().equals(comments.get(0).getUserId()))
            {
                commentRepository.deleteById(id); // xoá comment
                reactRepository.deleteByReferenceId(id);// xoá react
                response.setMessage(EcodeConstant.SUCCESS_MSG);
                response.setEcode(EcodeConstant.SUCCESS);
            } else {
                response.setMessage(EcodeConstant.NOT_ACCESS_MSG);
                response.setEcode(EcodeConstant.NOT_ACCESS);
            }
        }
        return response;
    }

    @Override 
    public CommonResponse<Object> getAllComment(GetAllCommentRequest dto, String username) {
        CommonResponse<Object> response = new CommonResponse<>();
        try {
            Users onlineUser = usersRepository.findByUsername(username).get();
            Page<Comments> pageComments;
            Pageable paging = PageRequest.of(dto.getPage(), 10);
            if (dto.getLevel() != null)
                pageComments = commentRepository.getByPostIdAndLevelAndStatusOrderByCreateDate(dto.getPostId(),dto.getLevel(), "active", paging);
            else  pageComments =  commentRepository.getByPostIdOrderByCreateDate(dto.getPostId(), paging);
            ArrayList<LinkedHashMap<String, Object>> newArr = new ArrayList<>();
            List<Comments> _comment = pageComments.getContent();
            insertMediaAndAuthor(_comment, newArr);
            response.setData(newArr);
            response.setEcode(EcodeConstant.SUCCESS);
            response.setMessage(EcodeConstant.SUCCESS_MSG);
            return response;
        } catch (Exception e) {
            log.error("Exception during get comment {} |", e);
            response.setEcode(EcodeConstant.EXCEPTION);
            response.setMessage(EcodeConstant.EXCEPTION_MSG);
            return response;
        }
    }

    private void insertMediaAndAuthor(List<Comments> _comment, ArrayList<LinkedHashMap<String, Object>> newArr) {
        for(Comments item : _comment){
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            List<Media> _medias = mediaRepository.findByReferenceIdOrderBySeqAsc(item.getId());
            Optional<Users> authorCmt = usersRepository.findById(item.getUserId());
            data.put("postId", item.getPostId());
            data.put("referenceId", item.getReferenceId());
            data.put("commentId", item.getId());
            data.put("content", item.getContent());
            data.put("level", item.getLevel());
            data.put("number", commentRepository.countByReferenceIdAndStatus(item.getId(), "active"));
            if(!_medias.isEmpty()){
                ArrayList newArrMedia = new ArrayList<>();
                for(Media mediaItem : _medias){
                    LinkedHashMap<String, String> mediaData = new LinkedHashMap<>();
                    mediaData.put("url", mediaItem.getMediaUrl());
                    mediaData.put("status", mediaItem.getStatus());
                    mediaData.put("type", mediaItem.getType());
                    newArrMedia.add(mediaData);
                }
                data.put("images", newArrMedia);
            }

            data.put("createDate", item.getCreateDate());

            LinkedHashMap<String, String> author = new LinkedHashMap<>();
            author.put("authorId", authorCmt.get().getId());
            author.put("username", authorCmt.get().getUsername());
            author.put("authorAvatar", authorCmt.get().getAvatar());
            data.put("poster",author);

            data.put("status", item.getStatus());
            newArr.add(data);
        }
    }
}
