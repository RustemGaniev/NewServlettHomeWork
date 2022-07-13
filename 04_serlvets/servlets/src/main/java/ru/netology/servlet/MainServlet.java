package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
  private PostController postController;
  private final String PATH_POSTS = "/api/posts";
  private final String PATH_WITH_NUMBER_POST = PATH_POSTS + "/\\d+";

  @Override
  public void init() {
    final var applicationContext = new AnnotationConfigApplicationContext("ru.netology");
    postController = applicationContext.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      if (method.equals("GET") && path.equals(PATH_POSTS)) {
        postController.all(resp);
        return;
      }
      if (method.equals("GET") && path.matches(PATH_WITH_NUMBER_POST)) {
        // easy way
        final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        postController.getById(id, resp);
        return;
      }
      if (method.equals("POST") && path.equals(PATH_POSTS)) {
        postController.save(req.getReader(), resp);
        return;
      }
      if (method.equals("DELETE") && path.matches(PATH_WITH_NUMBER_POST)) {
        // easy way
        final long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        postController.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (IOException ex) {
      ex.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    catch (NotFoundException ex) {
      ex.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
