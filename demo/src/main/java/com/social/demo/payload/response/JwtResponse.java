package com.social.demo.payload.response;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
  private String token;
	private String type = "Bearer";
	private String id;
	private String username;
	private String email;
	private List<String> roles;


}
