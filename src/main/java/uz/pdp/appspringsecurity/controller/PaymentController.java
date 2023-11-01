package uz.pdp.appspringsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appspringsecurity.security.CurrentUserDetails;

import java.util.List;

@RestController
@RequestMapping(PaymentController.BASE_PATH)
public class PaymentController {
    public static final String BASE_PATH = "/payment";

    @PreAuthorize(value = "hasAnyAuthority('PAYMENT_VIEW')")
    //CASHIER, ADMIN
    @GetMapping
    public List<String> getPayments() {
        return List.of("Samandar", "Yashnar");
    }

    //CASHIER, ADMIN
    @PreAuthorize(value = "hasAnyAuthority('PAYMENT_VIEW')")
    @GetMapping("/{id}")
    public String getPayment(@PathVariable String id) {
        return id;
    }

    //CASHIER, ADMIN
    @PreAuthorize(value = "hasAnyAuthority('PAYMENT_ADD')")
    @PostMapping
    public String addPayments(@AuthenticationPrincipal CurrentUserDetails currentUser) {
        return "Added";
    }

    //ADMIN
    @PreAuthorize(value = "hasAnyAuthority('PAYMENT_EDIT')")
    @PutMapping
    public String editPayments() {
        return "Edited";
    }

    //ADMIN
    @PreAuthorize(value = "hasAnyAuthority('PAYMENT_DELETE')")
    @DeleteMapping
    public String deletePayments(@AuthenticationPrincipal CurrentUserDetails currentUser) {
        System.out.println(currentUser);
        return "Deleted";
    }
}
