package com.routediary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
  SIGNUP_SUCCESS(200, "회원가입 성공"), VAILD_ID(200, "사용가능한 ID입니다."), VAILD_NICKNAME(200,
      "사용가능한 닉네임입니다."), LOGIN_SUCCESS(200, "로그인 성공"), LOGOUT_SUCCESS(200,
          "로그아웃 성공"), PAGE_LOAD_SUCCESS(200, "페이지 로드 성공"), DIARY_LOAD_SUCCESS(200,
              "다이어리 로드 성공"), NOTICE_LOAD_SUCCESS(200, "공지사항 로드 성공"), SUCCESS_TO_WRITE(200,
                  "글/댓글 작성 성공"), SUCCESS_TO_MODIFY(200, "글/댓글 삭제 성공"), SUCCESS_TO_REMOVE(200,
                      "글/댓글 삭제 성공"), LIKE_HANDLING_SUCCESS(200, "좋아요/좋아요취소 성공");

  private int status;
  private String message;
}
