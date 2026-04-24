package com.nfy.cancerapp.web;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 避免未处理异常落到 Whitelabel；数据库类错误给出迁移提示。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NoResourceFoundException.class)
	public ModelAndView notFound(NoResourceFoundException ex, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("status", 404);
		mv.addObject("error", "Not Found");
		mv.addObject("message", "请求的页面或资源不存在。");
		return mv;
	}

	@ExceptionHandler(DataAccessException.class)
	public ModelAndView dataAccess(DataAccessException ex, HttpServletResponse response) {
		log.error("Data access error", ex);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("status", 500);
		mv.addObject("error", "数据库访问异常");
		String root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
		mv.addObject("message", "请检查数据库是否已执行最新建表/迁移脚本（例如缺少 template_code 等字段）。\n\n详情：" + root);
		return mv;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView fallback(Exception ex, HttpServletResponse response) {
		log.error("Unhandled exception", ex);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("status", 500);
		mv.addObject("error", "服务器异常");
		mv.addObject("message", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName());
		return mv;
	}
}
