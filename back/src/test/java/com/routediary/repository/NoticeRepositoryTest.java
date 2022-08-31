package com.routediary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

@SpringBootTest
class NoticeRepositoryTest {
  private Logger logger = Logger.getLogger(getClass());

  @Autowired
  NoticeRepository repository;

  @Test
  void InsertTest() throws InsertException, SelectException { // 공지사항 작성 테스트
    Notice notice = new Notice();
    notice.setAdminId("1234");
    notice.setNoticeTitle("강만두");
    notice.setNoticeContent("만두를좋아하는사람");
    repository.insert(notice);

    Notice noticeInDb = repository.selectNotice(11);
    assertNotNull(noticeInDb);
    assertEquals("1234", noticeInDb.getAdminId());
  }

  @Test
  void updateTest() throws UpdateException, SelectException { // 내용을 수정하기 위해 update()를 사용하는 경우
    String expectedAdminId = "1234";
    String noticeTitle = "modified notice";
    String noticeContent = "modified notice";
    Notice notice = new Notice();
    notice.setNoticeNo(10);
    notice.setNoticeTitle(noticeTitle);
    notice.setNoticeContent(noticeContent);
    repository.update(notice);

    Notice noticeInDb = repository.selectNotice(11);
    assertEquals(noticeTitle, noticeInDb.getNoticeTitle());
    assertEquals(expectedAdminId, noticeInDb.getAdminId());
    assertEquals(new Integer(0), noticeInDb.getNoticeViewCnt());
  }

  @Test
  void updateViewCntTest() throws UpdateException, SelectException { // 조회수를 증가시키기 위해
                                                                     // update()를 사용하는 경우
    Notice noticeForViewCnt = new Notice();
    noticeForViewCnt.setNoticeNo(11);
    noticeForViewCnt.setNoticeViewCnt(-1);
    repository.update(noticeForViewCnt);

    Notice noticeInDb = repository.selectNotice(11);
    assertEquals(new Integer(1), noticeInDb.getNoticeViewCnt());
  }

  @Test
  void DeleteTest() throws DeleteException, SelectException { // 공지사항 삭제 테스트

    int noticeNo = 11;
    repository.delete(noticeNo);
    Notice noticeInDb = repository.selectNotice(11);
    assertNull(noticeInDb);
  }

  @Test
  void SelectOneTest() throws SelectException { // 공지사항 클릭했을때 한개 보여주기 테스트

    int noticeNo = 1;

    String expectedAdminId = "9101";
    String expectedTitle = "서포터즈 지원자 이벤트";
    String expectedNoticeContent = "서포터즈 지원자 이벤트를 준비해보았습니다! 여행의 설레임을 마음껏 나누어주세요!";
    Notice notice = repository.selectNotice(noticeNo);
    assertEquals(expectedAdminId, notice.getAdminId());
    assertEquals(expectedTitle, notice.getNoticeTitle());
    assertEquals(expectedNoticeContent, notice.getNoticeContent());

  }

  @Test
  void SelectCountTest() throws SelectException { // 공지사항 총개수 테스트

    int expectedCnt = 10;
    int cnt = repository.selectCount();
    assertEquals(expectedCnt, cnt);
  }

  @Test
  void SelectNoticesTest() throws SelectException { // 공지사항 목록 테스트
    int currentPage = 1;
    int cntPerPage = 10;
    int endRow = currentPage * cntPerPage; // 10
    int startRow = endRow - cntPerPage + 1; // 1

    int expectedSize = 10;
    // int []expectedNoticeNoArr = {12,11,10,9,8,7,6,5,4,3};
    List<Notice> list = repository.selectNotices(startRow, endRow, null);
    assertNotNull(list);
    assertEquals(expectedSize, list.size());
    // for(int i = 0; i<list.size(); i++) {
    // assertEquals(expectedNoticeNoArr[i], list.get(i).getNoticeNo());
    // }
  }

  @Test
  void SelectNoticesWordTest() throws SelectException { // 공지사항 검색어를통한 목록 테스
    int currentPage = 1;
    int cntPerPage = 10;
    int endRow = currentPage * cntPerPage; // 10
    int startRow = endRow - cntPerPage + 1; // 1

    int expectedSize = 3;
    String keyword = "개";
    List<Notice> list = repository.selectNotices(startRow, endRow, keyword);
    assertNotNull(list);
    assertEquals(expectedSize, list.size());
  }
}
