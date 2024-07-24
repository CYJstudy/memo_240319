package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileManagerService {

	// 실제 업로드가 된 이미지가 저장될 서버의 경로
	public static final String FILE_UPLOAD_PATH = "D:\\최윤정\\6_spring_project\\memo\\memo_workspace\\images/";
	
	// input: MultipartFile, userLoginId
	// output: String(이미지 경로)
	public String uploadFile(MultipartFile file, String loginId) {
		// 폴더(디렉토리) 생성
		// 예:aaaa_17384789312/sun.png
		String directoryName = loginId + "_" + System.currentTimeMillis();
		// D:\\최윤정\\6_spring_project\\memo\\memo_workspace\\images/aaaa_17384789312/
		String filePath = FILE_UPLOAD_PATH + directoryName + "/";
		
		// 폴더 생성
		File directory = new File(filePath);
		if (directory.mkdir() == false) {
			// 폴더 생성 시 실패하면 경로를 null로 리턴
			return null;
		}
		
		// 파일 업로드
		try {
			byte[] bytes = file.getBytes();
			// ★★★★★★★ 한글명으로 된 이미지는 업로드 불가하므로 나중에 영문자로 바꾸기
			Path path = Paths.get(filePath + file.getOriginalFilename());
			Files.write(path, bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return null; // 이미지 업로드 실패시 경로 null
		}
		
		// 파일업로드가 성공하면 이미지 url path를 리턴
		// 주소는 이렇게 될 것이다. (지금은 예언, 아직 이미지와 서버사이에 연결되기전이므로)
		//  /images/aaaa_17384789312/sun.png
		return "/images/" + directoryName + "/" + file.getOriginalFilename();
	}
	
	// 파일삭제
	// input: 이미지 경로(path)   output: X
	public void deleteFile(String imagePath) { // 인풋값: /images/aaaa_1721209530231/beach-418742_1280.jpg
		// D:\최윤정\6_spring_project\memo\memo_workspace\images\aaaa_1721209530231/beach-418742_1280.jpg
		
		// FILE_UPLOAD_PATH + imagePath = "D:\\최윤정\\6_spring_project\\memo\\memo_workspace\\images//images/aaaa_1721209530231/beach-418742_1280.jpg"
		// 상기 주소에 겹치는 /images/ 를 지운다.
		Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images/", ""));
		
		// 삭제할 이미지가 존재하는지 확인
		if (Files.exists(path)) {
			// 이미지 삭제
			try {
				Files.delete(path);
			} catch (IOException e) {
				log.info("[FileManagerService 파일삭제] 삭제 실패. path:{}", path.toString());
				return;
			}
			
			// 폴더(디렉토리) 삭제
			path = path.getParent();
			if (Files.exists(path)) {
				try {
					Files.delete(path);
				} catch (IOException e) {
					log.info("[FileManagerService 파일삭제] 디렉토리 삭제 실패. path:{}", path.toString());
				}
			}
		}
	}
}
