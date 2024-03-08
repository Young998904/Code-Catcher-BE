package com.lion.codecatcherbe.domain.user;

import com.lion.codecatcherbe.domain.user.dto.NicNameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<NicNameDto> UpdateUserNicName(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody NicNameDto nicNameDto) {
        return userService.updateUserNickname(token, nicNameDto);
    }
}
