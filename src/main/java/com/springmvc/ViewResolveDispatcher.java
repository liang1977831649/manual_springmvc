package com.springmvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewResolveDispatcher {
    public void redirectTo(HttpServletRequest req, HttpServletResponse resp, String result) throws IOException, ServletException {
        String[] split = result.split(":");
        if ("redirect".equals(split[0])) {
            try {
                resp.sendRedirect(req.getServletContext().getContextPath() + split[1]);
            } catch (IOException e) {
                //如果发现没有这个页面，就返回404
                resp.getWriter().write("<h1>404 Not Found</h1>");
            }
        } else if ("forward".equals(split[0])) {
            forwardTo(req, resp, result);
        } else {
            //如果是不是redirect，也不是forward，那就返回一个404
            resp.getWriter().write("<h1>404 Not Found</h1>");
        }
    }

    public void forwardTo(HttpServletRequest req, HttpServletResponse resp, String result) throws ServletException, IOException {
        req.getRequestDispatcher(result).forward(req, resp);
    }
}
