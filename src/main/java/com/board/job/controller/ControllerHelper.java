package com.board.job.controller;

import com.board.job.model.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ControllerHelper {
    public static Set<String> getAuthorities(Authentication authentication) {
       return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public static boolean checkSearchText(String searchText) {
        return searchText != null && !searchText.trim().isEmpty();
    }

    public static String getSortByValues(String sortBy) {
        return sortBy.substring(1, sortBy.length() - 1);
    }


    public static void sendRedirectAndCheckForError(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    private static ModelAndView redirectionError() {
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
