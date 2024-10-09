package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.User;
import org.kreyzon.stripe.model.JWTRequest;
import org.kreyzon.stripe.model.JWTResponse;
import org.kreyzon.stripe.repository.UserRepository;
import org.kreyzon.stripe.security.JwtHelper;
import org.kreyzon.stripe.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")

@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;


    @Autowired
    private JwtHelper helper;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);



    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {


        // this will get email and password from header.
        this.doAuthenticate(request.getEmail(), request.getPassword());


        // If all ok and validated by manager class then we get user here
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generating tocken
        String token = this.helper.generateToken(userDetails);

        // Retrieve userId from the user entity
        String userId = ""; // Add logic to retrieve userId from the User entity

        if (userDetails instanceof User) {
            userId = ((User) userDetails).getUserId();
        }

        // Retrieve user's name from the userDetails object
        String name = "";
        if (userDetails instanceof User) {
            name = ((User) userDetails).getName();
        }

        JWTResponse response = JWTResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .name(name)
                .userId(userId)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

  /*  @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
    */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>("Credentials Invalid !!", HttpStatus.UNAUTHORIZED);
    }


    // mapping for creating user
    @PostMapping("/create-user")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @Autowired
    private UserRepository userRepository;
}
