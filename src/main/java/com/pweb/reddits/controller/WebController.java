package com.pweb.reddits.controller;

import com.pweb.reddits.entity.Comment;
import com.pweb.reddits.entity.Post;
import com.pweb.reddits.entity.User;
import com.pweb.reddits.service.CommentService;
import com.pweb.reddits.service.PostService;
import com.pweb.reddits.service.UserService;
import com.pweb.reddits.util.Slugify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("")
public class WebController {

  @Autowired
  private PostService postService;

  @Autowired
  private UserService userService;

  @Autowired
  private CommentService commentService;

  //
  // GET Route
  //

  @GetMapping("/")
  public String homePage(Model model) {
    model.addAttribute("title", "Home");
    model.addAttribute("posts", postService.findByNewest());
    model.addAttribute("commentService", commentService);

    return "index";
  }

  @GetMapping("/newpost")
  public String newPostPage(Model model) {
    model.addAttribute("title", "New Post");
    model.addAttribute("post", new Post());

    return "newpost";
  }

  @GetMapping("/post/{slug}")
  public String displayPostPage(@PathVariable("slug") String slug, Model model) {
    Post post = new Post();
    for (Post p : postService.findAll()) {
      if (p.getSlug().equals(slug)) {
        post = p;
      }
    }

    List<Comment> comments = commentService.findNewestByPostId(post.getId());

    model.addAttribute("post", post);
    model.addAttribute("title", post.getText());
    model.addAttribute("comment", new Comment());
    model.addAttribute("comments", comments);
    model.addAttribute("userService", userService);
    model.addAttribute("commentService", commentService);

    return "post";
  }

  @RequestMapping(value = "/edit_post", method = RequestMethod.POST)
  public String postEditPage(@ModelAttribute("post") Post post, Model model) {
    model.addAttribute("title", "Edit Post");
    model.addAttribute("post", post);

    return "editpost";
  }

  @RequestMapping(value = "/edit_comment", method = RequestMethod.POST)
  public String commentEditPage(@ModelAttribute("comment") Comment comment, Model model) {
    model.addAttribute("title", "Edit Comment");
    model.addAttribute("comment", comment);

    return "editcomment";
  }

  @GetMapping("/signup")
  public String signUpPage(Model model) {
    model.addAttribute("title", "Sign Up");
    model.addAttribute("user", new User());

    return "signup";
  }

  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("title", "Sign Up");

    return "login";
  }

  @GetMapping("/profile/{username}")
  public String profilePage(@PathVariable("username") String username, Model model) {
    User u = userService.findByUsername(username);
    if (u == null) {
      u = new User();
      u.setUsername("User not found");
      u.setDepartment("");
      model.addAttribute("user", u);
      model.addAttribute("title", "User not found");
    } else {
      model.addAttribute("title", u.getUsername() + "'s Profile");
      model.addAttribute("user", u);
    }

    return "profile";
  }

  @GetMapping("/changeprofile")
  public String changeProfilePage(Model model) {
    model.addAttribute("title", "Change Profile");

    return "changeprofile";
  }

  //
  // POST Route
  //

  @PostMapping("/api/v1/user_signup")
  public String userSignUp(User user) {
    userService.addUser(user);
    return "redirect:/";
  }


  @PostMapping("/api/v1/user_login")
  public String handleLogin(String username, String password, Model model) {
    if (userService.authenticate(username, password)) {
      return "redirect:/";
    }
    return "redirect:/login";
  }

  @PostMapping("/api/v1/post_update")
  public String postSave(Post post) {
    postService.update(post);

    return "redirect:/";
  }

  @PostMapping("/api/v1/post_new")
  public String postNew(Post post) {
    post.setSlug(Slugify.slugify(post.getText()));
    System.out.println(post.getSlug());

    SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
    post.setTimestamp(sdf.format(new Timestamp(System.currentTimeMillis())));

    postService.add(post);

    return "redirect:/";
  }

  @PostMapping("/api/v1/post_delete/{id}")
  public String postDelete(@PathVariable("id") Long id) {
    postService.removeById(id);

    return "redirect:/";
  }

  @PostMapping("/api/v1/add_comment")
  public String addComment(Comment comment) {
    comment.setUserId(1L);

    SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
    comment.setTimestamp(sdf.format(new Timestamp(System.currentTimeMillis())));

    commentService.add(comment);

    return "redirect:/post/" + postService.findById(comment.getPostId()).get().getSlug();
  }

  @PostMapping("/api/v1/delete_comment")
  public String deleteComment(Comment comment) {
    commentService.removeById(comment.getId());

    return "redirect:/post/" + postService.findById(comment.getPostId()).get().getSlug();
  }

  @PostMapping("/api/v1/update_comment")
  public String updateComment(Comment comment) {
    commentService.update(comment);

    return "redirect:/post/" + postService.findById(comment.getPostId()).get().getSlug();
  }
}