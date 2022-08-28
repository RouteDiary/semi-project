package com.routediary.respository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.routediary.dto.Notice;
import com.routediary.exception.DeleteException;
import com.routediary.exception.InsertException;
import com.routediary.exception.SelectException;
import com.routediary.exception.UpdateException;
import com.routediary.repository.NoticeRepository;

@SpringBootTest
class NoticeTest {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	NoticeRepository repository;

	@Test
	void InsertTest() throws InsertException {
		Notice notice = new Notice();
		notice.setAdminId("msk");
		notice.setNoticeTitle("강만두");
		notice.setNoticeContent("만두를좋아하는사람");
		repository.insert(notice);
	}

	@Test
	void UpdateTest() throws UpdateException, InsertException {

		Notice notice1 = new Notice();
		notice1.setAdminId("msk");
		notice1.setNoticeTitle("sorry");
		notice1.setNoticeContent("만두를좋아하는사람");
		repository.insert(notice1);

//		notice1.setNoticeTitle("강만두");
//		notice1.setNoticeNo(notice1.getNoticeNo());
		notice1.setNoticeContent("만두를안좋아함");
		repository.update(notice1);
		assertEquals(notice1.getNoticeContent(), "만두를안좋아함");
	}
	@Test
	void DeleteTest() throws DeleteException {

		int noticeNo = 64;
		repository.delete(noticeNo);

	}
	@Test
	void SelectOneTest() throws SelectException {
		
		int noticeNo = 65;
		
		String expectedAdminId = "9101";
	    String expectedTitle = "서포터즈 지원자 이벤트";
	    String expectedNoticeContent = "서포터즈 지원자 이벤트를 준비해보았습니다! 여행의 설레임을 마음껏 나누어주세요!";
	    Notice notice = repository.selectNotice(noticeNo);
	    assertEquals(expectedAdminId, notice.getAdminId());
	    assertEquals(expectedTitle, notice.getNoticeTitle());
	    assertEquals(expectedNoticeContent, notice.getNoticeContent());
		
	}
	@Test
	void SelectCountTest() throws SelectException{
		
		int expectedCnt = 13;
		int cnt = repository.selectCount();
		assertEquals(expectedCnt, cnt);
	}
	@Test
	void SelectNoticesTest() throws SelectException{
		int currentPage = 1;
		int cntPerPage = 10;
		int endRow = currentPage * cntPerPage; //10
		int startRow = endRow - cntPerPage + 1; // 1
		
		int expectedSize = 10;
		int []expectedNoticeNoArr = {12,11,10,9,8,7,6,5,4,3}; 
		List<Notice> list = repository.selectNotices(startRow, endRow);
		assertNotNull(list);
		assertEquals(expectedSize, list.size());
		for(int i = 0; i<list.size(); i++) {
			assertEquals(expectedNoticeNoArr[i], list.get(i).getNoticeNo());
		}
	}
	
}
