package com.social.demo.models;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Document(collection = "users")
@Data
public class Users {
  @Id
  private String id;

  @NotBlank
  @Size(max = 20)
  private String username;

  private String nickname;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  private String firstName;

  private String lastName;

  private Set<Role> roles = new HashSet<>();

  private String phone;

  private Date dateOfBirth;

  private String roleId;

  private String gender;

  private Boolean isOnline;

  private String address;

  private String workpalace;

  private String avatar;

  private String background;

  private List<String> listFriend;

  private String recipientToken;

  private Date createTime;

  private Date updateTime;

  private Integer resetPwToken;

  private Date expiryResetPwTokenDate;
}
