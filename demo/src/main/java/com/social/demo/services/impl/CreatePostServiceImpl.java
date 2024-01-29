package com.social.demo.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.Media;
import com.social.demo.models.Posts;
import com.social.demo.models.Tag;
import com.social.demo.models.Users;
import com.social.demo.payload.request.CreatePostRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.MediaRepository;
import com.social.demo.repository.PostsRepository;
import com.social.demo.repository.TagRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.CreatePostService;
import com.social.demo.utils.Utils;

@Service
public class CreatePostServiceImpl implements CreatePostService {
  private static final Logger log = LogManager.getLogger(CreatePostServiceImpl.class);

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private PostsRepository postsRepository;

  @Override
  public CommonResponse<Object> createNewPost(CreatePostRequest request, String username) {
    CommonResponse<Object> response = new CommonResponse<>();
    Map<String, String> data = new HashMap<>();
    try {
      Optional<Users> onlineUser = usersRepository.findByUsername(username);
      final String postId = "post_" + UUID.randomUUID().toString().replace("-", "");


      if(request.getImages() != null && !request.getImages().isEmpty()){
        log.info("{} | Save list of images for this new post", postId);
        ArrayList<Media> listImages = new ArrayList<Media>();
        for(HashMap<String, String> image : request.getImages()){
          Media media = new Media();

          String mediaId = "media_" + UUID.randomUUID().toString().replace("-", "");
          media.setId(mediaId);

          Calendar c = Calendar.getInstance();
					c.add(Calendar.HOUR, 7);
					Date now = c.getTime();

          media.setCreateDate(now);
          media.setDescription("");
          media.setMediaUrl(image.get("url"));
          media.setReferenceId(postId);
		  media.setSeq(Integer.parseInt(image.get("seq")));
          media.setStatus(EcodeConstant.MEDIA_STT_ACTIVE);
					media.setType(image.get("type"));
					listImages.add(media);
        }
        mediaRepository.saveAll(listImages);
        log.info("PostId={} |Save list of images success. List size = {}", postId, listImages.size());
      }


      if(request.getContent() != null && !request.getContent().isEmpty()){
        ArrayList<HashMap<String, String>> listUserTagRequest = Utils.getUsertag_frContent(request.getContent());
        log.info("tag list {}", listUserTagRequest.size());
        if (listUserTagRequest != null && !listUserTagRequest.isEmpty()) {
					log.info("{} | Save list of tag for this new post", postId);
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
						tag.setReferenceId(postId);
						tag.setStatus("1");
						tag.setUserReq(onlineUser.get().getId());
						tag.setSeq(Integer.parseInt(userTag.get("seq")));
            tag.setUserRes(userTag.get("userId"));

						lstUserTags.add(tag);
					}
					tagRepository.saveAll(lstUserTags);
					log.info("PostId={} | Save list of hashtag success", postId);
        }
      }
      Posts post = new Posts();
			post.setId(postId);
			post.setContent(request.getContent() == null ? "" : request.getContent());
			post.setUserId(String.valueOf(onlineUser.get().getId()));
			post.setReportStatus(EcodeConstant.POST_STT_ACTIVE);
      post.setStatus(request.getStatus());

      Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, 7);
			Date now = c.getTime();
			post.setCreateDate(now);
			post.setEditDate(now);
      post.setLastReactDate(now);
      postsRepository.save(post);
      log.info("Save new post to DB success | PostId = {}", post.getId());
      data.put("postId", postId);
			response.setData(data);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);

      return response;
    } catch (Exception e) {
      log.error("Exception during create new post {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }
}
