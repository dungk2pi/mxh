package com.social.demo.services.impl;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.models.ERole;
import com.social.demo.models.Role;
import com.social.demo.models.UserSetting;
import com.social.demo.models.Users;
import com.social.demo.payload.request.LoginRequest;
import com.social.demo.payload.request.RegisterRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.payload.response.JwtResponse;
import com.social.demo.repository.RoleRepository;
import com.social.demo.repository.UserSettingRepository;
import com.social.demo.repository.UsersRepository;
import com.social.demo.security.jwt.JwtUtils;
import com.social.demo.services.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class AuthServiceImpl implements AuthService {


  @Autowired
	AuthenticationManager authenticationManager;

  @Autowired
	UsersRepository usersRepository;

  @Autowired
  UserSettingRepository userSettingRepository;

	@Autowired
	RoleRepository roleRepository;

  @Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

  private static final Logger log = LogManager.getLogger(AuthServiceImpl.class);

  @Override
  public CommonResponse<Object> login(LoginRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);

      UsersDetailsImpl userDetails = (UsersDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      JwtResponse jwtResponse = new JwtResponse();

      jwtResponse.setToken(jwt);
      jwtResponse.setId(userDetails.getId());
      jwtResponse.setUsername(userDetails.getUsername());
      jwtResponse.setEmail(userDetails.getEmail());
      jwtResponse.setRoles(roles);


      response.setData(jwtResponse);
      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);

      return response;
    } catch (Exception e) {
      log.error("Exception during login {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
			return response;
    }
  }

  @Override
  public CommonResponse<Object> register(RegisterRequest request) {
   CommonResponse<Object> response = new CommonResponse<Object>();

		if (usersRepository.existsByUsername(request.getUsername())) {
			response.setEcode(EcodeConstant.EXIST);
      response.setMessage(EcodeConstant.EXIST_USERNAME_MSG);
			return response;
		}

		if (usersRepository.existsByEmail(request.getEmail())) {
			response.setEcode(EcodeConstant.EXIST);
      response.setMessage(EcodeConstant.EXIST_USERNAME_MSG);
			return response;
		}

    Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 7);
    Date now = c.getTime();

		Users user = new Users();
    String userId = "user_" + UUID.randomUUID().toString().replace("-", "");

    user.setId(userId);
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setListFriend(new ArrayList<>());
    user.setLastName(request.getLastName());
    user.setPhone(request.getPhoneNumber());
    user.setPassword(encoder.encode(request.getPassword()));
    user.setCreateTime(now);

		Set<String> strRoles = request.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;

				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);

    UserSetting _userSetting = new UserSetting();

    _userSetting.setUserId(userId);
    _userSetting.setDisableTag(0);
    _userSetting.setDisableAddFriend(0);
    _userSetting.setListBlockAll(new ArrayList<>());
    _userSetting.setEnNotification(1);
    _userSetting.setPubFullname(true);
    _userSetting.setPubEmail(true);
    _userSetting.setPubPhone(true);
    _userSetting.setPubWorkpalace(true);
    _userSetting.setPubDateOfBirth(true);
    _userSetting.setPubAddress(true);

    try {
      usersRepository.save(user);
      userSettingRepository.save(_userSetting);
      log.info("Save response {}", user.getId());
      log.info("Save response usersetting {}", user.getId());

      response.setEcode(EcodeConstant.SUCCESS);
      response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      log.error("Exception during register {} |", e);
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
			return response;
    }
  }

}
