package com.purwandi.app.gateway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.purwandi.app.gateway.model.UserEntity;

@RestController
@RequestMapping("/v2/user")
public class UserHandler {

    @GetMapping("/")
    public ResponseEntity<UserEntity> getUser() {
        UserEntity user = new UserEntity();
        user.setId(10);
        user.setEmail("foo@bar.com");
        user.setUsername("foobar");

        return ResponseEntity.ok().body(user);
    }

}
