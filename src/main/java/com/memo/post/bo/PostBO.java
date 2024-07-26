package com.memo.post.bo;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostBO {
	// 로깅을 거는 방법들
	//private Logger log = LoggerFactory.getLogger(PostBO.class);
	//private Logger log = LoggerFactory.getLogger(this.getClass()); // this로 하면, 이 문구를 복사해서 사용하고싶은곳에서 사용가능

	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	// 페이징 정보 필드(limit) -- 지금은 bo에 있지만, 정석대로라면 페이지에 대한 클래스를 따로 빼는게 좋다
	private static final int POST_MAX_SIZE = 3;
	
	// input: 로그인 된 사람의 userId
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		// 게시글 번호(게시글이 10개있을때 기준) : 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 4 3 2 페이지에 있을 때
		// 1. 다음 : 2보다 작은 3개 DESC
		// 2. 이전 : 4보다 큰 3개 ASC => 5 6 7 => BO에서 reverse 7 6 5
		// 3. 그냥 들어왔을때(페이징 X) : 최신순 3개 DESC
		
		/* 1) 현재 7 6 5 페이지에 있다고 가정했을 때, 다음(next)를 눌렀을때 쿼리문
		select * from `post`
		where `id` < 5
		order by `id` desc
		limit 3;
		 2) 현재 4 3 2 페이지에 있다고 가정했을 때, 이전(prev)를 눌렀을때 쿼리문을 아래와 같이 짠다면, 
		    desc가 있으므로 제일 마지막 3개인 10 9 8을 가져오게 됨
		    따라서, desc가 아닌 asc로 가져오고, BO에서 reverse하도록 짜야함
		select * from `post`
		where `id` > 4
		order by `id` desc
		limit 3;
		*/
		
		Integer standardId = null; // 기준 postId
		String direction = null; // 방향
		if (prevId != null) { // 2. 이전
			standardId = prevId;
			direction = "prev";
			
			List<Post> postList = postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE);
			// [5, 6, 7] 로 들어온 리스트가 있을때 => [7, 6, 5]로 reverse
			Collections.reverse(postList);  // 뒤집고 저장까지 해줌
			
			return postList;
			
		} else if (nextId != null) { // 1. 다음
			standardId = nextId;
			direction = "next";
		}
		
		// 3. 페이징 X or 1.다음을 눌렀을때
		return postMapper.selectPostListByUserId(userId, standardId, direction, POST_MAX_SIZE); 
	}
	
	// '이전'을 눌러도 이동할 페이지가 없는가?
	public boolean isPrevLastPageByUserId(int userId, int prevId) {
		int maxPostId = postMapper.selectPostIdByUserIdAsSort(userId, "DESC");
		return maxPostId == prevId; // 같으면 마지막이므로 이동할 페이지 없음
	}
	
	// '다음'을 눌러도 이동할 페이지가 없는가?
	public boolean isNextLastPageByUserId(int userId, int nextId) {
		int minPostId = postMapper.selectPostIdByUserIdAsSort(userId, "ASC");
		return minPostId == nextId;
	}
	
	
	// input: userId, postId
	// output: Post or null
	public Post getPostByPostIdUserId(int userId, int postId) {
		return postMapper.selectPostByPostIdUserId(userId, postId);
	}
	
	// input: 파라미터들
	// output: X
	public void addPost(int userId, String userLoginId, 
			String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		
		if (file != null) {
			// 업로드 할 이미지가 있을때에만 업로드
			imagePath = fileManagerService.uploadFile(file, userLoginId);
		}
		
		postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	// input: 파라미터들
	// output: X
	public void updatePostByPostId(
			int userId, String loginId, 
			int postId, String subject, String content,
			MultipartFile file) {
		
		// 기존글을 가져온다.(1. 이미지 교체시 삭제하기 위해 2. 업데이트 대상이 있는지 확인하기 위해)
		Post post = postMapper.selectPostByPostIdUserId(userId, postId);
		if (post == null) {
			log.warn("[글 수정] post is null. userId:{}, postId:{}", userId, postId);
			return;
		}
		
		// 업로드할 파일이 있으면
		// 1) 새 이미지를 업로드
		// 2) 1번 단계가 성공하면 기존 이미지가 있을 때 삭제
		String imagePath = null;
		
		if (file != null) {
			// 새 이미지 업로드
			imagePath = fileManagerService.uploadFile(file, loginId);
			
			// 업로드 성공 시(imagePath가 null이 아님) 기존 이미지가 있으면 제거
			if (imagePath != null && post.getImagePath() != null) {
				// 폴더와 이미지 제거(서버에서)
				fileManagerService.deleteFile(post.getImagePath());
			}
		}
		
		// db update
		postMapper.updatePostByPostId(postId, subject, content, imagePath);
	}
	
	// input: postId, userId
	// output: X
	public void deletePostByPostIdUserId(int postId, int userId) {
		// 기존글 가져오기 (왜냐하면, 이미지가 존재할 경우 삭제를 위해서)
		Post post = postMapper.selectPostByPostIdUserId(userId, postId);
		if (post == null) {
			log.info("[글 삭제] post is null. postId:{}, userId:{}", postId, userId);
			return;
		}
		
		// post db delete
		int rowCount = postMapper.deletePostByPostId(postId);
		
		// TODO 이미지가 존재하면 삭제, 삭제된 행도 1일때
		if (rowCount > 0 && post.getImagePath() != null) {
			fileManagerService.deleteFile(post.getImagePath());
		}
	}
}





