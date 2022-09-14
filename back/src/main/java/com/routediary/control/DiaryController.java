package com.routediary.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routediary.annotation.LogExecutionTime;
import com.routediary.dto.Client;
import com.routediary.dto.Comment;
import com.routediary.dto.Diary;
import com.routediary.dto.Like;
import com.routediary.dto.PageBean;
import com.routediary.dto.ResultBean;
import com.routediary.enums.ErrorCode;
import com.routediary.enums.SuccessCode;
import com.routediary.exception.AddException;
import com.routediary.exception.EmptyContentException;
import com.routediary.exception.FindException;
import com.routediary.exception.InvalidActionException;
import com.routediary.exception.ModifyException;
import com.routediary.exception.NoPermissionException;
import com.routediary.exception.NotLoginedException;
import com.routediary.exception.NumberNotFoundException;
import com.routediary.exception.RemoveException;
import com.routediary.service.DiaryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class DiaryController {
  @Autowired
  private DiaryService diaryService;

  @PostMapping(value = "diary/write")
  public ResponseEntity<?> writeDiary(
      @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles, String diary,
      HttpSession session) throws AddException, NotLoginedException, EmptyContentException,
      JsonMappingException, JsonProcessingException {
    String clientId = (String) session.getAttribute("loginInfo");
    clientId = "japanwoman@gmail.com";
    ObjectMapper mapper = new ObjectMapper();
    Diary d = null;
    d = mapper.readValue(diary, Diary.class);
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (d.getDiaryTitle() == null || d.getDiaryTitle().equals("")) {
      throw new EmptyContentException(ErrorCode.EMPTY_TITLE);
    } else if (d.getDiaryStartDate() == null || d.getDiaryEndDate() == null) {
      throw new EmptyContentException(ErrorCode.EMPTY_DATE);
    } else {
      Client client = new Client();
      client.setClientId(clientId);
      d.setClient(client);
      diaryService.writeDiary(d, imageFiles);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_WRITE);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @PutMapping(value = "diary/{diaryNo}")
  public ResponseEntity<?> modifyDiary(@PathVariable int diaryNo,
      @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles, String diary,
      HttpSession session) throws ModifyException, NotLoginedException, EmptyContentException,
      NoPermissionException, JsonMappingException, JsonProcessingException {
    String clientId = (String) session.getAttribute("loginInfo");
    clientId = "japanwoman@gmail.com";
    ObjectMapper mapper = new ObjectMapper();
    Diary d = null;
    d = mapper.readValue(diary, Diary.class);
    String writerId = d.getClient().getClientId();
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(writerId)) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else if (d.getDiaryTitle() == null || d.getDiaryTitle().equals("")) {
      throw new EmptyContentException(ErrorCode.EMPTY_TITLE);
    } else if (d.getDiaryStartDate() == null || d.getDiaryEndDate() == null) {
      throw new EmptyContentException(ErrorCode.EMPTY_DATE);
    } else {
      diaryService.modifyDiary(d, imageFiles);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_MODIFY);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @DeleteMapping(value = "diary/{diaryNo}")
  public ResponseEntity<?> removeDiary(@PathVariable int diaryNo, @RequestParam String writerId,
      HttpSession session) throws RemoveException, NotLoginedException, NoPermissionException {
    String clientId = (String) session.getAttribute("loginInfo");
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(writerId)) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else {
      diaryService.removeDiary(diaryNo);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_REMOVE);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @GetMapping(value = {"diary/list/{order}", "diary/list/{order}/{pageNo}"})
  @LogExecutionTime
  public ResponseEntity<?> showDiaryBoard(@PathVariable Optional<Integer> pageNo,
      @PathVariable int order,
      @RequestParam(value = "hashtags[]", required = false) List<String> hashtags)
      throws FindException, NumberNotFoundException {
    log.error("hashtags : " + hashtags);
    int currentPageNo;
    if (pageNo.isPresent()) {
      currentPageNo = pageNo.get();
    } else {
      currentPageNo = 1;
    }
    PageBean<Diary> pageBean = diaryService.showDiaryBoard(order, currentPageNo, hashtags);
    ResultBean<PageBean<Diary>> resultBean =
        new ResultBean<PageBean<Diary>>(SuccessCode.PAGE_LOAD_SUCCESS);
    resultBean.setT(pageBean);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @GetMapping(value = {"diary/mylist", "diary/mylist/{pageNo}"})
  public ResponseEntity<?> showMyDiaryBoard(@PathVariable Optional<Integer> pageNo,
      HttpSession session) throws FindException, NotLoginedException {
    String clientId = (String) session.getAttribute("loginInfo");
    int currentPageNo;
    if (pageNo.isPresent()) {
      currentPageNo = pageNo.get();
    } else {
      currentPageNo = 1;
    }
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else {
      PageBean<Diary> pageBean = diaryService.showMyDiaryBoard(currentPageNo, clientId);
      ResultBean<PageBean<Diary>> resultBean =
          new ResultBean<PageBean<Diary>>(SuccessCode.PAGE_LOAD_SUCCESS);
      resultBean.setT(pageBean);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @GetMapping(value = "diary/{diaryNo}")
  public ResponseEntity<?> showDiary(@PathVariable int diaryNo, HttpSession session)
      throws FindException, NumberNotFoundException {
    String clientId = (String) session.getAttribute("loginInfo");
    Diary diary = diaryService.showDiary(diaryNo);
    ResultBean<Map<String, Object>> resultBean =
        new ResultBean<Map<String, Object>>(SuccessCode.DIARY_LOAD_SUCCESS);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("diary", diary);
    map.put("loginedId", clientId);
    resultBean.setT(map);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @PostMapping(value = "like/{isLike}")
  public ResponseEntity<?> clikeLikeToggle(@PathVariable boolean isLike, @RequestBody Like like,
      HttpSession session)
      throws AddException, RemoveException, InvalidActionException, NotLoginedException {
    String clientId = (String) session.getAttribute("loginInfo");
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else {
      diaryService.clickLikeToggle(isLike, like);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.LIKE_HANDLING_SUCCESS);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @GetMapping(value = "/")
  public ResponseEntity<?> showIndexPage() throws FindException {
    Map<String, List<Diary>> diaries = diaryService.showIndexPage();
    ResultBean<Map<String, List<Diary>>> resultBean =
        new ResultBean<Map<String, List<Diary>>>(SuccessCode.PAGE_LOAD_SUCCESS);
    resultBean.setT(diaries);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @PostMapping(value = "diary/{diaryNo}/comment")
  public ResponseEntity<?> writeComment(@PathVariable int diaryNo, @RequestBody Comment comment,
      HttpSession session) throws AddException, NotLoginedException, EmptyContentException {
    log.error("diaryo=" + diaryNo);
    // String clientId = (String) session.getAttribute("loginInfo");
    String clientId = "koreawoman@gmail.com";
    if (clientId == null) {
      log.error("clientId=" + clientId);
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (comment == null || comment.getCommentContent() == null
        || comment.getCommentContent().equals("")) {
      log.error("commentContent is null or empty");
      throw new EmptyContentException(ErrorCode.EMPTY_CONTENT);
    } else {
      log.error("ok" + clientId + ", commentContent=" + comment.getCommentContent());
      Client client = new Client();
      client.setClientId(clientId);
      comment.setClient(client);
      // comment.setDiaryNo(diaryNo);
      diaryService.writeComment(comment);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_WRITE);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @PutMapping(value = "diary/{diaryNo}/comment")
  public ResponseEntity<Object> modifyComment(@PathVariable int diaryNo,
      @RequestBody Comment comment, HttpSession session)
      throws ModifyException, NotLoginedException, EmptyContentException, NoPermissionException {
    String clientId = (String) session.getAttribute("loginInfo");
    String writerId = comment.getClient().getClientId();
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(writerId)) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else if (comment.getCommentContent() == null || comment.getCommentContent().equals("")) {
      throw new EmptyContentException(ErrorCode.EMPTY_CONTENT);
    } else {
      diaryService.modifyComment(comment);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_MODIFY);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @DeleteMapping(value = "diary/{diaryNo}/comment")
  public ResponseEntity<?> removeComment(@PathVariable int diaryNo, @RequestBody Comment comment,
      HttpSession session) throws RemoveException, NotLoginedException, NoPermissionException {
    String clientId = (String) session.getAttribute("loginInfo");
    String writerId = comment.getClient().getClientId();
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(writerId)) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else {
      diaryService.removeComment(diaryNo, comment.getCommentNo());
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_REMOVE);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }
}
