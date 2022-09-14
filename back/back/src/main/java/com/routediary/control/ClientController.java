package com.routediary.control;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.routediary.dto.Client;
import com.routediary.dto.ResultBean;
import com.routediary.enums.ErrorCode;
import com.routediary.enums.SuccessCode;
import com.routediary.exception.AddException;
import com.routediary.exception.DuplicationException;
import com.routediary.exception.FindException;
import com.routediary.exception.LogoutFailureException;
import com.routediary.exception.MismatchException;
import com.routediary.exception.ModifyException;
import com.routediary.exception.NoPermissionException;
import com.routediary.exception.NotLoginedException;
import com.routediary.exception.RemoveException;
import com.routediary.exception.WithdrawnClientException;
import com.routediary.service.ClientService;

@RestController
@RequestMapping("client/*")
public class ClientController {

  @Autowired
  private ClientService clientService;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Client client) throws AddException {
    clientService.signup(client);
    ResultBean<?> resultBean = new ResultBean(SuccessCode.SIGNUP_SUCCESS);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @GetMapping("/login")
  public ResponseEntity<?> login(@RequestParam String clientId, @RequestParam String clientPwd,
      HttpSession session) throws FindException, MismatchException, WithdrawnClientException {
    boolean isLoginSucceeded = clientService.login(clientId, clientPwd);
    if (isLoginSucceeded) {
      session.setAttribute("loginInfo", clientId);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SIGNUP_SUCCESS);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    } else {
      throw new FindException();
    }
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout(HttpSession session) throws LogoutFailureException {
    session.removeAttribute("loginInfo");
    String clientId = (String) session.getAttribute("loginInfo");
    if (clientId == null) {
      ResultBean<?> resultBean = new ResultBean(SuccessCode.LOGOUT_SUCCESS);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    } else {
      throw new LogoutFailureException(ErrorCode.FAILED_TO_LOGOUT);
    }
  }

  @PutMapping("/modify")
  public ResponseEntity<?> modifyAccount(@RequestBody Client client, HttpSession session)
      throws ModifyException, NotLoginedException, NoPermissionException {
    String clientId = (String) session.getAttribute("loginInfo");
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(client.getClientId())) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else {
      clientService.modifyAccount(client);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_MODIFY);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @DeleteMapping("/remove")
  public ResponseEntity<?> removeAccount(@RequestBody Client client, HttpSession session)
      throws RemoveException, NotLoginedException, NoPermissionException {
    String clientId = (String) session.getAttribute("loginInfo");
    if (clientId == null) {
      throw new NotLoginedException(ErrorCode.NOT_LOGINED);
    } else if (!clientId.equals(client.getClientId())) {
      throw new NoPermissionException(ErrorCode.NO_PERMISSION);
    } else {
      clientService.removeAccount(client);
      ResultBean<?> resultBean = new ResultBean(SuccessCode.SUCCESS_TO_REMOVE);
      return new ResponseEntity<>(resultBean, HttpStatus.OK);
    }
  }

  @GetMapping("/idcheck")
  public ResponseEntity<?> idDuplicationCheck(@RequestParam String clientId)
      throws FindException, DuplicationException {
    clientService.idDuplicationCheck(clientId);
    ResultBean<?> resultBean = new ResultBean(SuccessCode.VAILD_ID);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }

  @GetMapping("/nicknamecheck")
  public ResponseEntity<?> NicknameDuplicationCheck(@RequestParam String clientNickname)
      throws FindException, DuplicationException {
    clientService.nicknameDuplicationCheck(clientNickname);
    ResultBean<?> resultBean = new ResultBean(SuccessCode.VAILD_NICKNAME);
    return new ResponseEntity<>(resultBean, HttpStatus.OK);
  }
}
