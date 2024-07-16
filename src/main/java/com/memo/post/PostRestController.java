package com.memo.post;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.bo.PostBO;

@RequestMapping("/post")
@RestController
public class PostRestController {

	@Autowired
	private PostBO postBO;
	
//	@PostMapping("/create")
//	public Map<String, Object> create(
//			@RequestParam("userId") int userId,
//			@RequestParam("subject") String subject,
//			@RequestParam("content") String content, 
//			@RequestParam("file") MultipartFile file) {
//		
//		// db insert
//		post = postBO.addPostCreateByUserId(userId, subject, content, file);
//	}
}
