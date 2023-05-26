package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.models.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/info")
public class InfoController {
    private final PersonDAO personDAO;

    public InfoController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping
    public String showInfo(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Person person = personDAO.findById(userId);
            if (person != null) {
                model.addAttribute("person", person);
                return "info/userProfile";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Person person = personDAO.show(userId);
            if (person != null) {
                model.addAttribute("person", person);
                return "info/editProfile";
            }
        }
        return "redirect:/authorization";
    }

    @PatchMapping
    public String updateProfile(@ModelAttribute("person") @Valid Person person,
                             BindingResult bindingResult,
                             HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            if (bindingResult.hasErrors()) {
                return "info/editProfile";
            }
            personDAO.updateData(userId, person);
            return "redirect:/info";
        }
        return "redirect:/authorization";
    }


}
