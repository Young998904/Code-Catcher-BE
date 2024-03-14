package com.lion.codecatcherbe.domain.user;

import com.lion.codecatcherbe.domain.user.dto.NicNameDto;
import com.lion.codecatcherbe.domain.user.dto.response.UserInfoRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<NicNameDto> UpdateUserNicName(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody NicNameDto nicNameDto) {
        return userService.updateUserNickname(token, nicNameDto);
    }

    @DeleteMapping("/withdraw")
    public HttpStatus DeleteUser(@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.deleteUser(token);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoRes> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        return userService.getUserInfo(token);
    }
}
