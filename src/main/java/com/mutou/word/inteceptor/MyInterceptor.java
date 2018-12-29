package com.mutou.word.inteceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mutou.word.util.CheckEmptyUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 杨喜存
 * @since 2018/12/26 7:59 PM
 */
@Component
public class MyInterceptor implements HandlerInterceptor {

  // 该api最大超时时间
  private static final Integer MAX_TIME_OUT_SEC = 100;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    //    System.err.println("请求地址: " + request.getRequestURI());

    // 1. 为Ajax设置跨域请求头
    response.setHeader("Access-Control-Allow-Origin", "*");

    // 2. 根据request的请求类型进行分类验证身份
    Boolean checkResult = true;
    switch (request.getMethod()) {
      case "POST":
        checkResult = checkVerify_Post(request, response);
        break;
      case "GET":
        checkResult = checkVerify_Get(request, response);
        break;
      default:
        break;
    }
    System.out.println(checkResult);
    return checkResult;
  }

  /**
   * 校验GET请求的加密方式.
   *
   * @param request request
   * @param response response
   * @return
   * @throws UnsupportedEncodingException
   */
  private Boolean checkVerify_Get(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    // 从request请求中获取到参数列表
    Map<String, String[]> paramMap = request.getParameterMap();
    // 由于获取到的参数列表是只读的，所以只能用自定义一个map
    // 使用TreeMap的时候，已经将key进行自然排序了(默认升序)
    Map<String, String[]> myMap = new TreeMap<>();
    // 将自定义的map被赋值
    for (String key : paramMap.keySet()) {
      myMap.put(key, paramMap.get(key));
    }

    // 不存在时间戳
    if (!myMap.containsKey("_")) {
      return false;
    }

    // 1. 删除掉时间戳
    String[] timestampArray = myMap.remove("_");
    // 校验时间戳是否合法
    if (!checkTimeStamp(timestampArray[0])) {
      return false;
    }

    // 2. 不存在加密串
    if (!myMap.containsKey("vc")) {
      return false;
    }
    // 删除掉加密串
    String[] vc = myMap.remove("vc");
    // 将map输出成指定格式的字符串
    String mapString = "";
    for (String key : myMap.keySet()) {
      String[] values = myMap.get(key);
      for (int i = 0; i < values.length; i++) {
        String value = values[i];
        mapString += key + "=" + value + "&";
      }
    }
    mapString += "a,.@@'p;0-+";
    // 最后一个&先不删除了，前端也留着
    // 生成加密串
    String verifyCode = DigestUtils.md5DigestAsHex(mapString.getBytes("UTF-8"));
    // 比较我生成的加密串和传来的是否相等
    if (CheckEmptyUtil.isNotEmpty(vc) && CheckEmptyUtil.isNotEmpty(vc[0])) {
      if (verifyCode.equals(vc[0])) {
        return true;
      }
    }
    return false;
  }

  // 校验POST请求的加密方式
  private Boolean checkVerify_Post(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    // 从request中获取到body的内容
    String requestJson = getPostBody(request);
    // 解析该json串到json对象
    JSONObject jsonObject = JSON.parseObject(requestJson);
    // 先获取到时间戳
    if (!jsonObject.containsKey("_")) {
      return false;
    }
    // 校验时间戳是否合法
    if (!checkTimeStamp(jsonObject.getString("_"))) {
      return false;
    }
    // 去掉VerifyCode这一项
    String vc = (String) jsonObject.remove("vc");
    // 再转换为字符串
    requestJson = jsonObject.toJSONString();
    // 此时的salt是写死的，应该从数据库获取到才对
    String salt = "a,.@@'p;0-+";
    // 请求体加上salt
    requestJson = requestJson + salt;
    // 将拼接好的字符串进行加密
    String verifyCode = DigestUtils.md5DigestAsHex(requestJson.getBytes("UTF-8"));
    // 比较两个加密串  相等即通过审核
    if (verifyCode.equals(vc)) {
      return true;
    }
    return false;
  }

  /**
   * 检查时间戳是否合格(在当前时间内).
   *
   * @param clientTimeStamp
   * @return
   */
  private Boolean checkTimeStamp(String clientTimeStamp) {
    Long timestamp = 1L;
    try {
      timestamp = Long.parseLong(clientTimeStamp);
    } catch (Exception e) {
      return false;
    }
    Timestamp d = new Timestamp(new Date().getTime());
    Long sec = (d.getTime() - timestamp) / 1000;
    if (sec > MAX_TIME_OUT_SEC) {
      return false;
    }
    return true;
  }

  /**
   * 获取POST请求中Body参数
   *
   * @param request
   * @return 字符串
   */
  public String getPostBody(HttpServletRequest request) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    String line = null;
    StringBuilder sb = new StringBuilder();
    try {
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {}
}
