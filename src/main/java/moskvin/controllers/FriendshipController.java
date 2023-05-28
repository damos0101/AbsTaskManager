package moskvin.controllers;

import moskvin.dao.FriendshipDAO;
import moskvin.models.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FriendshipController {
    private final FriendshipDAO friendshipDAO;

    public FriendshipController(FriendshipDAO friendshipDAO) {
        this.friendshipDAO = friendshipDAO;
    }

    @GetMapping("/friends")
    public String viewFriends(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            List<Person> friends = friendshipDAO.getFriendsByPersonId(userId);
            model.addAttribute("friends", friends);
            return "friends/friends";
        }
        return "redirect:/authorization";
    }
}
