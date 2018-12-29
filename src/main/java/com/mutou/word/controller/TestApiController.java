package com.mutou.word.controller;

import com.mutou.word.api.TestApi;
import com.mutou.word.dto.TestPost;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨喜存
 * @since 2018/12/26 6:16 PM
 */
@RestController
public class TestApiController implements TestApi {
  @Override
  public void testSwagger(@RequestParam  String yxc) {
    System.out.println(yxc);

  }

  @Override
  public void testPost(@RequestBody TestPost testPost) {
    System.out.println("----->"+testPost.getCcc());
  }
}
