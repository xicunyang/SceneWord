package com.mutou.word.filter;


import com.mutou.word.inteceptor.BufferedServletRequestWrapper;
import com.mutou.word.util.CheckEmptyUtil;
import java.io.IOException;
import java.util.logging.LogRecord;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @author 杨喜存
 * @since 2018/12/29 2:31 PM
 */
@Component
public class MyFilter implements Filter {


  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(
      ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    ServletRequest requestWrapper = null;
    if(request instanceof HttpServletRequest){
      requestWrapper = new BufferedServletRequestWrapper((HttpServletRequest) request);
    }
    if(CheckEmptyUtil.isEmpty(requestWrapper)){
      chain.doFilter(request,response);
    }else{
      chain.doFilter(requestWrapper,response);
    }
  }

  @Override
  public void destroy() {

  }
}
