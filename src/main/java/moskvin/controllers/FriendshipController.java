package moskvin.controllers;

import moskvin.dao.FriendshipDAO;
import moskvin.dao.PersonDAO;
import moskvin.models.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FriendshipController {
    private final FriendshipDAO friendshipDAO;
    private final PersonDAO personDAO;

    public FriendshipController(FriendshipDAO friendshipDAO, PersonDAO personDAO) {
        this.friendshipDAO = friendshipDAO;
        this.personDAO = personDAO;
    }

    @GetMapping("/friends")
    public String viewFriends(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            List<Person> friends = friendshipDAO.getFriendsByPersonId(userId);
            List<Person> sentFriendshipRequests = friendshipDAO.getSentFriendshipRequestsByPersonId(userId);
            List<Person> receivedFriendshipRequests = friendshipDAO.getReceivedFriendshipRequestsByPersonId(userId);
            model.addAttribute("friends", friends);
            model.addAttribute("sentFriendshipRequests", sentFriendshipRequests);
            model.addAttribute("receivedFriendshipRequests", receivedFriendshipRequests);
            return "friends/friends";
        }
        return "redirect:/authorization";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam("friendId") int friendId, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            if(friendId==userId){
                model.addAttribute("ownProfile", true);
                model.addAttribute("friendshipStatus", "Ви");
            }
            else {
                model.addAttribute("ownProfile", false);
                Person person = personDAO.findById(friendId);
                model.addAttribute("person", person);
                if (friendshipDAO.areFriends(userId, friendId)) {
                    model.addAttribute("friendshipStatus", "Друзі");
                } else if (friendshipDAO.hasPendingRequest(userId, friendId)) {
                    model.addAttribute("friendshipStatus", "Запит надіслано");
                } else {
                    friendshipDAO.sendFriendshipRequest(userId, friendId);
                    model.addAttribute("friendshipStatus", "Запит відправлено");
                }
            }
        }
        return "redirect:/info/" + friendId;
    }

    @PostMapping("/accept-request")
    public String acceptRequest(@RequestParam("friendId") int friendId, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId!=null){
            friendshipDAO.sendFriendshipRequest(userId, friendId);
        }
        return "redirect:/friends";
    }

    @PostMapping("/cancel-sent-request")
    public String cancelSentRequest(@RequestParam("friendId") int friendId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            friendshipDAO.cancelFriendshipRequest(userId, friendId);
        }
        return "redirect:/friends";
    }

    @PostMapping("/cancel-received-request")
    public String cancelReceivedRequest(@RequestParam("friendId") int friendId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            friendshipDAO.cancelFriendshipRequest(friendId, userId);
        }
        return "redirect:/friends";
    }

    @PostMapping("/search-friend")
    public String searchFriend(@RequestParam("username") String username, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId!=null) {
            Person person = personDAO.findByUsername(username);
            if (person != null) {
                if(friendshipDAO.areFriends(userId, person.getId())) {
                    redirectAttributes.addFlashAttribute("userNameError", "Ви вже друзі");
                    return "redirect:/friends";
                }
                else if(friendshipDAO.hasPendingRequest(userId, person.getId())){
                    redirectAttributes.addFlashAttribute("userNameError", "Ви вже надіслали запит в друзі цьому користувачу");
                    return "redirect:/friends";
                }
                else if(userId==person.getId()){
                    redirectAttributes.addFlashAttribute("userNameError", "Ви не можете надіслати запит у друзі самому собі");
                    return "redirect:/friends";
                }
                else {
                    friendshipDAO.sendFriendshipRequest(userId, person.getId());
                    return "redirect:/friends";
                }
            }
            else{
                redirectAttributes.addFlashAttribute("userNameError", "Користувача з таким юзернеймом не знайдено");
                return "redirect:/friends";
            }
        }
        return "redirect:/friends";
    }
}
