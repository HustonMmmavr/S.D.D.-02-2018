package com.colorit.backend.controllers;

import com.colorit.backend.common.UserResponseMaker;
import com.colorit.backend.services.IUserService;
import com.colorit.backend.services.responses.UserServiceResponse;
import com.colorit.backend.views.*;
import com.colorit.backend.views.input.UpdateEmailView;
import com.colorit.backend.views.input.UpdatePasswordView;
import com.colorit.backend.views.output.ResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Locale;


@RestController
@RequestMapping(path = "/api/user/")
public class UserController extends AbstractController {

    @Autowired
    public UserController(@NotNull IUserService userService,
                          @NotNull UserResponseMaker userResponseMaker) {
        super(userService, userResponseMaker);
    }

    @GetMapping(path = "/info/{nickname}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseView> getUserInfo(@PathVariable(name = "nickname") String nickname,
                                                    HttpSession httpSession, Locale locale) {

        final String sessionNickname = (String) httpSession.getAttribute(getSessionKey());
        if (sessionNickname == null) {
            return getResponseMaker().makeUnauthorizedResponse(locale);
        }

        // todo check userexist, then get data;
        final UserServiceResponse userServiceResponse = getUserService().getUser(
                nickname != null ? nickname : sessionNickname);
        return getResponseMaker().makeResponse(userServiceResponse, locale);
    }

    @PostMapping(path = "/upadate_avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseView> updateAvatar(@RequestParam(name = "file") MultipartFile avatar,
                                                     HttpSession httpSession) {
        return null;
    }

    @GetMapping(path = "/avatar/{avatarPath}")
    public ResponseEntity<ResponseView> getAvatar(@PathVariable(name = "avatarPath") String avatarPath,
                                                  HttpSession httpSession) {
        return null;
    }

    @PostMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseView> update(//UpdateView updateView
                                               HttpSession httpSession) {
        return null;
    }

    @PostMapping(path = "/update_email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseView> updateEmail(@RequestBody UpdateEmailView updateEmailView, HttpSession httpSession, Locale locale) {
        final String nickname = (String) httpSession.getAttribute(getSessionKey());
        if (nickname == null) {
            return getResponseMaker().makeUnauthorizedResponse(locale);
        }

        final ViewStatus viewStatus = updateEmailView.checkValid();
        if (viewStatus.isNotValid()) {
            return getResponseMaker().makeResponse(viewStatus, locale);
        }

        final UserServiceResponse userServiceResponse = getUserService().updateEmail(nickname, updateEmailView.getNewEmail());
        return getResponseMaker().makeResponse(userServiceResponse, locale);
    }

    @PostMapping(path = "/update_password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseView> updatePassword(@RequestBody UpdatePasswordView updatePasswordView,
                                                       HttpSession httpSession, Locale locale) {
        final String nickname = (String) httpSession.getAttribute(getSessionKey());
        if (nickname == null) {
            return getResponseMaker().makeUnauthorizedResponse(locale);
        }

        final ViewStatus viewStatus = updatePasswordView.checkValid();
        if (viewStatus.isNotValid()) {
            return getResponseMaker().makeResponse(viewStatus, locale);
        }

        final UserServiceResponse userServiceResponse = getUserService().updatePassword(nickname,
                updatePasswordView.getOldPassword(), updatePasswordView.getNewPassword());
        return getResponseMaker().makeResponse(userServiceResponse, locale);
    }
}