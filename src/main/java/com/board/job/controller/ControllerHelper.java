package com.board.job.controller;

import com.board.job.model.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

public class ControllerHelper {
    public static Set<String> getAuthorities(Authentication authentication) {
       return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public static ModelAndView redirectionError() {
        ModelMap map = new ModelMap();
        map.addAttribute("errorResponse", new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Sorry, our problem with page, come back in 5 minutes😐🙏",
                "/error"
        ));
        map.addAttribute("formatter", DateTimeFormatter.ofPattern("h:mm a"));


        return new ModelAndView("errors/error", map);
    }
}
