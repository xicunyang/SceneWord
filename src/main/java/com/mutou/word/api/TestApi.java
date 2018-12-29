package com.mutou.word.api;

import com.mutou.word.dto.TestPost;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨喜存
 * @since 2018/12/26 6:15 PM
 */
@RequestMapping("/test")
@RestController
public interface TestApi {
  @RequestMapping(value = "/test-mapping", method = RequestMethod.GET)
  void testSwagger(@RequestParam("yxc") String yxc);

  @RequestMapping(value = "/test-post", method = RequestMethod.POST)
  void testPost(@RequestBody TestPost testPost);
}
