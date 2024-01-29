package com.social.demo.controllers;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.payload.request.LoginRequest;
import com.social.demo.payload.request.RegisterRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.RoleRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.AuthService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger log = LogManager.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AuthService authService;

	@Autowired
	UsersRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public CommonResponse<Object> loginUser(@Valid @RequestBody LoginRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();
    try {
			response = authService.login(request);
			return response;
    } catch (Exception e) {
			log.error(e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
			return response;
    }
	}

	@PostMapping("/signup")
	public CommonResponse<Object> registerUser(@Valid @RequestBody RegisterRequest request) {
		CommonResponse<Object> response = new CommonResponse<Object>();
		try {
			response = authService.register(request);
			return response;
		} catch (Exception e) {
			log.error(e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
			return response;
		}
	}
}
