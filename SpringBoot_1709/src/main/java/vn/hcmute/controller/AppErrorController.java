package vn.hcmute.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // For 404 errors, redirect to login if not authenticated, otherwise to dashboard
                return "redirect:/login";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // For 500 errors, redirect to login
                return "redirect:/login";
            }
        }
        
        // Default redirect to login
        return "redirect:/login";
    }
}
