package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.models.Person;
import moskvin.models.Plan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MenuController {
    private final PersonDAO personDAO;

    public MenuController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping("/menu")
    public String menu(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Person person = personDAO.show(userId);
            if (person != null) {
                model.addAttribute("person", person);
                return "menu/main";
            }
        }
        return "redirect:/authorization";
    }
}
