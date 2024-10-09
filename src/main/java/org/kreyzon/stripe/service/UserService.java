package org.kreyzon.stripe.service;

import org.kreyzon.stripe.dto.UserDTO;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.entity.ScheduleDate;
import org.kreyzon.stripe.entity.User;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    //  private List<User> store=new ArrayList<>();

/*   For in memory data
    public UserService() {

        store.add(new User(UUID.randomUUID().toString(), "Swapnil Take", "swapniltake1@outlook.com"));
    store.add(new User(UUID.randomUUID().toString(), "Kiran Take", "ktake1@outlook.com"));
    store.add(new User(UUID.randomUUID().toString(), "Mahesh Take", "mtake1@outlook.com"));

    }
*/

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;
    public UserService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // Fetch user by email
    public List<User> getUsers(){
        // return this.store;      // this is for in memory data
        return this.userRepository.findAll();
    }

    // Create User
    public User createUser(User user){
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("new");

        return this.userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    // Update user by userId
    public User updateUser(String userId, User user) {
        User existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAbout(user.getAbout());
        existingUser.setStatus(user.getStatus());
        existingUser.setProfile_Url(user.getProfile_Url());

        // Check if the new password is provided and update it if so
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }
    private static final int OTP_LENGTH = 6; // Length of OTP
    String otp = generateOtp();
    //    public boolean sendOtp(String email) {
//        String otp = generateOtp();
//        String subject = "Password Reset OTP";
//        String body = "Your OTP for password reset is: " + otp;
//        boolean emailSent = EmailService.sendEmail(email, subject, body);
//
//        return emailSent;
//    }
//
//
//    private String generateOtp() {
//        // Generate a random OTP
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//
//        for (int i = 0; i < OTP_LENGTH; i++) {
//            otp.append(random.nextInt(10)); // Append a random digit (0-9)
//        }
//
//        return otp.toString();
//    }
    private final JavaMailSender javaMailSender;

    //    public boolean sendOtp(String email) {
//        String otp = generateOtp();
//        String subject = "Password Reset OTP";
//        String body = "Your OTP for password reset is: " + otp;
//        return sendEmail(email, subject, body);
//    }
//
//    private boolean sendEmail(String to, String subject, String body) {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true);
//            javaMailSender.send(message);
//            return true; // Email sent successfully
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            return false; // Failed to send email
//        }
//    }
//
//    private String generateOtp() {
//        int OTP_LENGTH = 6; // Length of OTP
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < OTP_LENGTH; i++) {
//            otp.append(random.nextInt(10)); // Append a random digit (0-9)
//        }
//        return otp.toString();
//    }
//
//
//    public boolean verifyOtp(String email, String enteredOtp) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        // Check if the Optional contains a User object
//        if (userOptional.isPresent()) {
//            User user = userOptional.get(); // Retrieve the User object from Optional
//            return enteredOtp.equals(user.getOtp());
//        }
//
//        return false; // If the Optional is empty (no User found with the email)
//    }
    private Map<String, String> otpStorage = new HashMap<>();
    private static final long OTP_EXPIRATION_TIME = 5* 60 * 1000;// 30 seconds in milliseconds
    private static final Timer timer = new Timer();

    // Generate OTP and send it to the user's email
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    ;

    public boolean sendOtp(String email) {
        String otp = generateOtp();
        otpStorage.put(email, otp);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                otpStorage.remove(email);
            }
        }, OTP_EXPIRATION_TIME);

        // Send OTP via email
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(" OTP verification for Impulz");

            // HTML content with image
            String htmlContent = "<div style='background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>" +
                    "<h2 style='color: #333;'>Forgot Your Password?</h2>" +
                    "<p style='color: #666;'>No worries! We're here to help. Please use the OTP below to reset your password:</p>" +
                    "<h3 style='color: #333;'>Your OTP:</h3>" +
                    "<p style='font-size: 24px; color: #0088cc;'><strong>" + otp + "</strong></p>" +
                    "<p style='color: #666;'>If you didn't request a password reset, please ignore this email. Your account is safe and secure.</p>" +
                    "<p style='color: #666;'>Best regards,<br/>Lider Technology Solutions Pte Lt</p>" +
                    "</div>" +
                    "</div>" +
                    "<br/>" +
                    "<img src='https://newtonimpulzstorage.s3.ap-south-1.amazonaws.com/WebStorage/newtonlogo1.png' alt='Logo' style='display: block; margin: 0 auto;' height='80px'>";

            // Set HTML content
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            // Handle exception (e.g., email sending failure)
            e.printStackTrace();
            return false;
        }
    }

    // Verify OTP provided by the user
    public boolean verifyOtp(String email, String enteredOtp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(enteredOtp);
    }

    // Generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        return String.valueOf(otpValue);
    }

    public void resetPassword(String email, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Encode the new password before saving it
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
        } else {
            // Handle the case where no user is found with the provided email
            throw new RuntimeException("User with email " + email + " not found");
        }
    }

    public User changeStatusToActiveBySfid(long sfid, long registrationAdvanceId) {
        User user = userRepository.findBySfidAndUsertype(sfid, 1).orElse(null);
        if (user != null) {
            user.setStatus("active");

            // Fetch ScheduleDate values based on registrationAdvanceId
            RegistrationAdvance registrationAdvance = registrationAdvanceRepository.findById(registrationAdvanceId).orElse(null);
            if (registrationAdvance != null) {
                List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
                if (!scheduleDates.isEmpty()) {
                    user.setStartDate(scheduleDates.get(0).getStartDate());
                    user.setEndDate(scheduleDates.get(scheduleDates.size() - 1).getEndDate());
                }
            }

            return userRepository.save(user);
        }
        return null; // User with provided sfid not found or userType is not 1
    }

    public User updateStatusBySfid(long sfid, String status) {
        User user = userRepository.findBySfidAndUsertype(sfid, 1)
                .orElseThrow(() -> new RuntimeException("User with sfid " + sfid + " and usertype 1 not found"));
        user.setStatus(status);
        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    public List<UserDTO> getUsersByUserType(int usertype) {
        List<User> users = userRepository.findByUsertype(usertype);
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getProfile_Url()))
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

}
