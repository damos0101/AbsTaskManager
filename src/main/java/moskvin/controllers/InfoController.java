package moskvin.controllers;

import moskvin.dao.FriendshipDAO;
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
    private final FriendshipDAO friendshipDAO;

    public InfoController(PersonDAO personDAO, FriendshipDAO friendshipDAO) {
        this.personDAO = personDAO;
        this.friendshipDAO = friendshipDAO;
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

    @GetMapping("/{id}")
    public String showOtherPerson(@PathVariable("id") int id, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Person person = personDAO.show(id);

            if (person.getId() == userId) {
                model.addAttribute("ownProfile", true);
                model.addAttribute("friendshipStatus", "Ви");
            } else {
                model.addAttribute("ownProfile", false);
                if (friendshipDAO.areFriends(userId, id)) {
                    model.addAttribute("friendshipStatus", "Друзі");
                } else if (friendshipDAO.hasPendingRequest(userId, id)) {
                    model.addAttribute("friendshipStatus", "Запит надіслано");
                } else {
                    model.addAttribute("friendshipStatus", "Не друзі");
                }
            }
            model.addAttribute("person", person);
            return "info/otherUserProfile";
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
