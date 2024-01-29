package com.social.demo.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "usersetting")
@Data
public class UserSetting {
    private String userId;

    private String username;

    private Integer disableTag;

    private Integer disableAddFriend;

    private Integer enNotification;

    private List<String> listBlockAll;

    private boolean isPubFullname;

    private boolean isPubEmail;

    private boolean isPubPhone;

    private boolean isPubWorkpalace;

    private boolean isPubDateOfBirth;

    private boolean isPubAddress;
}
