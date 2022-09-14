package com.routediary.control;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.routediary.dto.Notice;
import com.routediary.dto.PageBean;
import com.routediary.dto.ResultBean;
import com.routediary.enums.SuccessCode;
import com.routediary.exception.FindException;
import com.routediary.exception.NumberNotFoundException;
import com.routediary.service.NoticeService;

@CrossOrigin(origins = "*")
@RestControllerAdvice
@RequestMapping("notice/*")
public class NoticeController {
  @Autowired
  NoticeService noticeService;

  @GetMapping(value = {"list", "list/{pageNo}"})
  public ResponseEntity<?> showNoticeBoard(@PathVariable Optional<Integer> pageNo)
      throws FindException {
    int currentPage;
    if (pageNo.isPresent()) {
      currentPage = pageNo.get();
    } else {
      currentPage = 1;
    }
    PageBean<Notice> pageBean = noticeService.showNoticeBoard(currentPage);
    ResultBean<PageBean<Notice>> resultBean =
        new ResultBean<PageBean<Notice>>(SuccessCode.PAGE_LOAD_SUCCESS);
    resultBean.setT(pageBean);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @GetMapping(value = "list/{keyword}/{pageNo}")
  public ResponseEntity<?> showNoticeBoardByKeyword(@PathVariable Optional<Integer> pageNo,
      @PathVariable String keyword) throws FindException {
    int currentPage;
    if (pageNo.isPresent()) {
      currentPage = pageNo.get();
    } else {
      currentPage = 1;
    }
    PageBean<Notice> pageBean = noticeService.showNoticeBoardByKeyword(currentPage, keyword);
    ResultBean<PageBean<Notice>> resultBean =
        new ResultBean<PageBean<Notice>>(SuccessCode.PAGE_LOAD_SUCCESS);
    resultBean.setT(pageBean);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @GetMapping(value = {"/{noticeNo}"})
  public ResponseEntity<?> showNotice(@PathVariable int noticeNo)
      throws FindException, NumberNotFoundException {
    Notice notice = noticeService.showNotice(noticeNo);
    ResultBean<Notice> resultBean = new ResultBean<Notice>(SuccessCode.NOTICE_LOAD_SUCCESS);
    resultBean.setT(notice);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }
}
