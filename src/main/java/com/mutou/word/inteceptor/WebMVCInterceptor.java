package com.mutou.word.inteceptor;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author 杨喜存
 * @since 2018/12/26 8:24 PM
 */
@Configuration
public class WebMVCInterceptor extends WebMvcConfigurerAdapter {
  // 注入拦截器类
  @Autowired private MyInterceptor myInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // addPathPatterns 用于添加拦截规则
    // excludePathPatterns 用户排除拦截
    // 使用拦截器的时候  addPathPatterns  内的参数要写成  /**
    // 否则不拦截
    // 很好奇  为什么/**会拦截两次
    // 第一次是/test/test-mapping  第二次是/error
    // 这样讲/error开启掉就好了
    registry.addInterceptor(myInterceptor).addPathPatterns("/**").excludePathPatterns("/error");
    super.addInterceptors(registry);
  }
}
