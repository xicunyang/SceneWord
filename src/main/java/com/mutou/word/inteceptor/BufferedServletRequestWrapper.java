package com.mutou.word.inteceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author 杨喜存
 * @since 2018/12/29 2:20 PM
 */
public class BufferedServletRequestWrapper extends HttpServletRequestWrapper {
  private byte[] buffer;

  public BufferedServletRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    //封装request
    //场景：在拦截器进行requestBody流的读取后  会造成requestBody数据在Controller为空
    //所以在拦截器的上一层  就是Filter层  先将request进行包装
    //这样  就可以多次读取request中的Body内容了。
    InputStream is = request.getInputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte buff[] = new byte[1024];
    int read;
    while ((read = is.read(buff)) > 0) {
      baos.write(buff,0,read);
    }
    this.buffer = baos.toByteArray();
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.buffer);
    ServletInputStream servletInputStream = new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener listener) {

      }

      @Override
      public int read() throws IOException {
        return byteArrayInputStream.read();
      }
    };
    return servletInputStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }
}
