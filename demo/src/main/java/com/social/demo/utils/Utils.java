package com.social.demo.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;


public class Utils {
    private static final Logger log = LogManager.getLogger(Utils.class);

    private static final char[] SOURCE_CHARACTERS = { 'a', 'à', 'ả', 'ã', 'á', 'ạ', 'ă', 'ằ', 'ẳ', 'ẵ', 'ắ', 'ặ', 'â',
			'ầ', 'ẩ', 'ẫ', 'ấ', 'ậ', 'e', 'è', 'ẻ', 'ẽ', 'é', 'ẹ', 'ê', 'ề', 'ể', 'ễ', 'ế', 'ệ', 'i', 'ì', 'ỉ', 'ĩ',
			'í', 'ị', 'o', 'ò', 'ỏ', 'õ', 'ó', 'ọ', 'ô', 'ồ', 'ổ', 'ỗ', 'ố', 'ộ', 'ơ', 'ờ', 'ở', 'ỡ', 'ớ', 'ợ', 'u',
			'ù', 'ủ', 'ũ', 'ú', 'ụ', 'ư', 'ừ', 'ử', 'ữ', 'ứ', 'ự', 'y', 'ỳ', 'ỷ', 'ỹ', 'ý', 'ỵ', 'A', 'À', 'Ả', 'Ã',
			'Á', 'Ạ', 'Ă', 'Ằ', 'Ẳ', 'Ẵ', 'Ắ', 'Ặ', 'Â', 'Ầ', 'Ẩ', 'Ẫ', 'Ấ', 'Ậ', 'E', 'È', 'Ẻ', 'Ẽ', 'É', 'Ẹ', 'Ê',
			'Ề', 'Ể', 'Ễ', 'Ế', 'Ệ', 'I', 'Ì', 'Ỉ', 'Ĩ', 'Í', 'Ị', 'O', 'Ò', 'Ỏ', 'Õ', 'Ó', 'Ọ', 'Ô', 'Ồ', 'Ổ', 'Ỗ',
			'Ố', 'Ộ', 'Ơ', 'Ờ', 'Ở', 'Ỡ', 'Ớ', 'Ợ', 'U', 'Ù', 'Ủ', 'Ũ', 'Ú', 'Ụ', 'Ư', 'Ừ', 'Ử', 'Ữ', 'Ứ', 'Ự', 'Y',
			'Ỳ', 'Ỷ', 'Ỹ', 'Ý', 'Ỵ', 'd', 'đ', 'D', 'Đ' };

	private static final char[] DESTINATION_CHARACTERS = { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
			'a', 'a', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'i', 'i', 'i',
			'i', 'i', 'i', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
			'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'y', 'y', 'y', 'y', 'y', 'y', 'A', 'A', 'A',
			'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'E', 'E', 'E', 'E', 'E', 'E',
			'E', 'E', 'E', 'E', 'E', 'E', 'I', 'I', 'I', 'I', 'I', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O',
			'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
			'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'd', 'd', 'D', 'D' };

	public static char removeAccent(char ch) {
		int index = -1;
		for (int i = 0; i < SOURCE_CHARACTERS.length; i++) {
			if (SOURCE_CHARACTERS[i] == (ch)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			ch = DESTINATION_CHARACTERS[index];
		}
		return ch;
	}

	public static String removeAccent(String str) {
		StringBuilder sb = new StringBuilder(str);
		for (int i = 0; i < sb.length(); i++) {
			sb.setCharAt(i, removeAccent(sb.charAt(i)));
		}
		return sb.toString();
	}

    public static ArrayList<HashMap<String, String>> getUsertag_frContent(String content) {
    ArrayList<HashMap<String, String>> lst_Usertag = new ArrayList<HashMap<String,String>>();

    try {
        if (content != null) {
            Pattern MY_PATTERN = Pattern.compile("#?([a-fA-F0-9]{24})#?");
            Matcher mat = MY_PATTERN.matcher(content);
            int i = 1;
            while (mat.find()) {
                String a_usertag = mat.group(1);
                log.info("Found user tag: {}", a_usertag);
                HashMap<String, String> usertagObj = new HashMap<String, String>();
                usertagObj.put("userId", a_usertag);
                usertagObj.put("seq", i + "");
                lst_Usertag.add(usertagObj);

                i++;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        log.error("getHashtag_frContent_New!", e);
    }
    return lst_Usertag;
    }

    public static String getConversation(String ownerId, String userId){
        return ownerId.hashCode() <= userId.hashCode()
        ? ownerId + "_" + userId
        : userId + "_" + ownerId;
    }

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public static void sendEmail(String recipientEmail, int token) throws MessagingException, UnsupportedEncodingException{
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("luffy3042001@gmail.com");
        mailSender.setPassword("xisxmipeknfoqgzn");

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol=smtp", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom("luffy3042001@gmail.com", "Support");
    helper.setTo(recipientEmail);

    String subject = "Here's the link to reset your password";

    String content = "<p>Hello,</p>"
            + "<p>You have requested to reset your password.</p>"
            + "<p>Here is code to change your password:</p>"
            + "<p>" + token + "</p>"
            + "<br>"
            + "<p>Ignore this email if you do remember your password, "
            + "or you have not made the request.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);
            try {
                mailSender.send(message);
            } catch (Exception e) {
                log.error("Authentication failed: " + e.getMessage(), e);
            }
        }
}
