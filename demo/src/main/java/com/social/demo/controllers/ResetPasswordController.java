package com.social.demo.controllers;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.demo.constant.EcodeConstant;
import com.social.demo.payload.request.ResetPasswordRequest;
import com.social.demo.payload.response.CommonResponse;
import com.social.demo.services.ResetPasswordService;
import com.social.demo.utils.Utils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class ResetPasswordController {
  @Autowired
  private ResetPasswordService resetPasswordService;

  @PostMapping("/resetPw")
	public CommonResponse<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();
    Random random = new Random();
    int token = random.nextInt(90000000) + 10000000;
    try {
      response = resetPasswordService.updateResetPasswordToken(request, token);
      Utils.sendEmail(request.getEmail(), token);

      return response;

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @GetMapping("/resetPw")
	public CommonResponse<Object> checkMatchToken(@RequestBody ResetPasswordRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      response = resetPasswordService.checkMatchToken(request);

      return response;

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }

  @PostMapping("/setNewPw")
	public CommonResponse<Object> setNewPassword(@RequestBody ResetPasswordRequest request) {
    CommonResponse<Object> response = new CommonResponse<Object>();

    try {
      response = resetPasswordService.setNewPassword(request);

      return response;

    } catch (Exception e) {
      response.setEcode(EcodeConstant.EXCEPTION);
      response.setMessage(EcodeConstant.EXCEPTION_MSG);
      return response;
    }
  }
}
