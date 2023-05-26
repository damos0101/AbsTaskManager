package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.dao.PlanDAO;
import moskvin.models.Person;
import moskvin.models.Plan;
import moskvin.util.PlanValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PlanController {
    private final PersonDAO personDAO;
    private final PlanDAO planDAO;
    private final PlanValidator planValidator;

    public PlanController(PersonDAO personDAO, PlanDAO planDAO, PlanValidator planValidator) {
        this.personDAO = personDAO;
        this.planDAO = planDAO;
        this.planValidator = planValidator;
    }

    @GetMapping("/plans")
    public String viewAllPlans(Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            // Встановлення класу Person, який створив план
            model.addAttribute("plans", planDAO.getPlansByPersonId(userId));

            return "menu/plans";
        }
        return "redirect:/authorization";
    }

    @GetMapping("/plan/new")
    public String showCreatePlanForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("plan", new Plan());
            return "menu/newPlan";
        }
        return "redirect:/authorization";
    }

    @PostMapping("/plan/new")
    public String createPlan(@ModelAttribute("plan") @Valid Plan plan, BindingResult bindingResult, HttpSession session) {
        planValidator.validate(plan, bindingResult);
        if (bindingResult.hasErrors()) {
            return "menu/newPlan";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {

            // Отримання ідентифікатора користувача з принципала
            Person person = personDAO.findById(userId);

            // Встановлення класу Person, який створив план
            plan.setPerson_id(person.getId());

            planDAO.save(plan);
            return "redirect:/menu";
        }
        return "redirect:/authorization";
    }
}
