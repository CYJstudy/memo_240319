package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

@Service
public class PostBO {

	@Autowired
	private PostMapper postMapper;
	
	// input: 로그인 된 사람의 userId
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId); 
	}
	
	// input: 4개의 파라미터
	// output: List<Post>
	public List<Post> addPostCreateByUserId(int userId, String subject, String content, MultipartFile file) {
		return postMapper.insertPostCreateByUserId(userId, subject, content, file);
	}
}
