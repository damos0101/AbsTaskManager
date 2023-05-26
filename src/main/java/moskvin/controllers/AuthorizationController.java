package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.models.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/authorization")
public class AuthorizationController {
    private final PersonDAO personDAO;

    public AuthorizationController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping
    public String show() {
        return "authorization/authorization";
    }

    @PostMapping
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        Person person = personDAO.findByUsername(username);
        if (person != null && person.getPassword().equals(password)) {
            session.setAttribute("userId", person.getId());
            return "redirect:/info";
        } else {
            redirectAttributes.addFlashAttribute("error", "Невірний логін або пароль");
            return "redirect:/authorization";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/authorization";
    }
}
