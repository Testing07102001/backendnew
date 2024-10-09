package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.UserDTO;
import org.kreyzon.stripe.entity.User;
import org.kreyzon.stripe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class Home {
    @Autowired
    private UserService userService;


    // to get users details
    @GetMapping("/user")
    public List<User> getUser(){

        System.out.println("getting current users");

        return this.userService.getUsers();
    }

    // To get current user info.

    @GetMapping("/current-user")
    public String getLoginUser(Principal principal){
        System.out.println(principal.getName());
        return "current User";
    }
    @GetMapping("/user/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/user/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @PutMapping("/{sfid}/activate")
    public User activateUserBySfid(@PathVariable long sfid, @RequestParam long registrationAdvanceId) {
        return userService.changeStatusToActiveBySfid(sfid, registrationAdvanceId);
    }



    @PutMapping("/user/status")
    public User updateUserStatus(@RequestBody Map<String, Object> requestBody) {
        long sfid = Long.parseLong(requestBody.get("sfid").toString());
        String status = requestBody.get("status").toString();
        return userService.updateStatusBySfid(sfid, status);
    }
    @GetMapping("/check-email")
    public boolean checkEmailAvailability(@RequestParam String email) {
        return userService.isEmailAvailable(email);
    }

    @GetMapping("/usertype/{usertype}")
    public ResponseEntity<List<UserDTO>> getUsersByUserType(@PathVariable int usertype) {
        List<UserDTO> users = userService.getUsersByUserType(usertype);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
