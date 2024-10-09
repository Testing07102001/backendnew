package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Blog;
import org.kreyzon.stripe.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class BlogController {
    @Autowired
    private BlogRepository blogRepository;


    @PostMapping("/blog")
    public ResponseEntity<Blog> createBlog(@RequestBody Blog newBlog) {
        Blog blog = blogRepository.save(newBlog);
        return ResponseEntity.status(HttpStatus.CREATED).body(blog);
    }


    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        return ResponseEntity.ok(blogs);
    }


    @GetMapping("/blog/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return blogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/blog/{id}")
    public ResponseEntity<Blog> updateBlog(@RequestBody Blog newBlog, @PathVariable Long id) {
        return blogRepository.findById(id)
                .map(existingBlog -> {
                    existingBlog.setTitle(newBlog.getTitle());
                    existingBlog.setImage(newBlog.getImage());
                    existingBlog.setDate(newBlog.getDate());
                    existingBlog.setPost(newBlog.getPost());
                    Blog updatedBlog = blogRepository.save(existingBlog);
                    return ResponseEntity.ok(updatedBlog);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/blog/{id}")
    public ResponseEntity<String> deleteBlog(@PathVariable Long id) {
        if (!blogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }


        blogRepository.deleteById(id);
        return ResponseEntity.ok("Blog with ID " + id + " has been deleted successfully.");
    }

}
