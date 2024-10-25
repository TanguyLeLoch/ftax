package com.natu.ftax.common.infrastructure;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = "/{path:(?!img|api|assets).*$}")
    // Matches all paths except those with a period (.)
    public String forwardToIndex() {
        // Forward to `index.html` so Angular can handle the routing
        return "forward:/index.html";
    }
}