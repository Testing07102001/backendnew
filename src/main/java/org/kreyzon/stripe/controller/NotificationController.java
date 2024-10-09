package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Notification;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository){
        this.notificationRepository= notificationRepository;
    }

    @PostMapping({"/notification"})
    public Notification newNotification (@RequestBody Notification newNotification){
        return (Notification)this.notificationRepository.save(newNotification);
    }

    @GetMapping({"/notifications"})
    public List<Notification> getAllNotification(){
        return this.notificationRepository.findAll();
    }

    @GetMapping({"/notification/{id}"})
    public Notification getNotification(@PathVariable Long id){
        return (Notification)this.notificationRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException((id));
        });
    }

    @DeleteMapping("/notification/{id}")
    public String deleteNotification(@PathVariable Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            notificationRepository.deleteById(id);
            return "Notification with id " + id + " has been deleted successfully.";
        }
    }
}
