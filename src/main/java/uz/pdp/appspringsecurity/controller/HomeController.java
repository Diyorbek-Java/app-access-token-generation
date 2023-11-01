package uz.pdp.appspringsecurity.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.payload.ErrorData;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final MessageSource messageSource;

    public HomeController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("open")
    public ApiResult<?> helloPage() {
        return ApiResult.errorResponse(
                messageSource.getMessage(
                        "accessTokenExpired",
                        null,
                        "accessTokenExpired",
                        LocaleContextHolder.getLocale()),
                417);
    }

    @GetMapping("secure")
    public String getSecurePage() {
        return messageSource.getMessage(
                "accessTokenExpired",
                null,
                "accessTokenExpired",
                LocaleContextHolder.getLocale());
    }

    @GetMapping("ketmon")
    public String bla(){
        return "dsdasd";
    }


}
