package com.social.demo.services.impl;

import java.util.Optional;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.controllers.ResetPasswordController;
import com.social.demo.models.Users;
import com.social.demo.payload.request.ResetPasswordRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.repository.UsersRepository;
import com.social.demo.services.ResetPasswordService;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {
  private static final Logger log = LogManager.getLogger(ResetPasswordController.class);


  private static final long EXPIRE_TOKEN_AFTER_MINUTES = 10;

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
	PasswordEncoder encoder;

  @Override
  public CommonResponse<Object> updateResetPasswordToken(ResetPasswordRequest request, int token) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    Optional<Users> user = usersRepository.findByEmail(request.getEmail());
    if(!user.isPresent()){
      response.setEcode(EcodeConstant.NULL);
      response.setMessage(EcodeConstant.NULL_MSG);
			return response;
    }else{
      try {
        user.get().setResetPwToken(token);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, (int) EXPIRE_TOKEN_AFTER_MINUTES);
				c.add(Calendar.HOUR, 7);
				Date now = c.getTime();

        user.get().setExpiryResetPwTokenDate(now);

        usersRepository.save(user.get());

        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
        return response;
      } catch (Exception e) {
        log.error("Exception during create resetpassword {} |", e);
        response.setEcode(EcodeConstant.EXCEPTION);
        response.setMessage(EcodeConstant.EXCEPTION_MSG);
        return response;
      }
    }
  }

  @Override
  public CommonResponse<Object> checkMatchToken(ResetPasswordRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    Optional<Users> user = usersRepository.findByResetPwToken(request.getToken());
    if(!user.isPresent()){
      response.setEcode(EcodeConstant.NULL);
      response.setMessage(EcodeConstant.NULL_MSG);
			return response;
    }else{
      try {
        Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR, 7);
				Date now = c.getTime();
        boolean isTimeOut = now.after(user.get().getExpiryResetPwTokenDate());
        if(isTimeOut){
          user.get().setExpiryResetPwTokenDate(null);
          user.get().setResetPwToken(-1);

          response.setEcode(EcodeConstant.TIMEOUT);
          response.setMessage(EcodeConstant.TIMEOUT_MSG);
          usersRepository.save(user.get());

          return response;
        }else{
          user.get().setExpiryResetPwTokenDate(null);
          user.get().setResetPwToken(null);
          usersRepository.save(user.get());
          response.setEcode(EcodeConstant.SUCCESS);
          response.setMessage(EcodeConstant.SUCCESS_MSG);
          response.setData(user.get().getEmail());
          return response;
        }
      } catch (Exception e) {
          response.setEcode(EcodeConstant.EXCEPTION);
          response.setMessage(EcodeConstant.EXCEPTION_MSG);
          return response;
      }
    }
  }

  @Override
  public CommonResponse<Object> setNewPassword(ResetPasswordRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    Optional<Users> user = usersRepository.findByEmail(request.getEmail());
    try {
        Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR, 7);
				Date now = c.getTime();

        user.get().setPassword(encoder.encode(request.getNewPassword()));
        user.get().setUpdateTime(now);

        usersRepository.save(user.get());

        response.setEcode(EcodeConstant.SUCCESS);
        response.setMessage(EcodeConstant.SUCCESS_MSG);
      return response;
    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }

  }

}
