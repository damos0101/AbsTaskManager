package moskvin.controllers;

import moskvin.dao.OptimizationDAO;
import moskvin.dao.PersonDAO;
import moskvin.dao.PlanDAO;
import moskvin.dao.RoleDAO;
import moskvin.models.Person;
import moskvin.models.Plan;
import moskvin.util.PlanValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PlanController {
    private final PersonDAO personDAO;
    private final PlanDAO planDAO;
    private final RoleDAO roleDAO;
    private final PlanValidator planValidator;
    private final OptimizationDAO optimizationDAO;

    public PlanController(PersonDAO personDAO, PlanDAO planDAO, RoleDAO roleDAO, PlanValidator planValidator, OptimizationDAO optimizationDAO) {
        this.personDAO = personDAO;
        this.planDAO = planDAO;
        this.roleDAO = roleDAO;
        this.planValidator = planValidator;
        this.optimizationDAO = optimizationDAO;
    }

    @GetMapping("/plans")
    public String viewAllPlans(Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("plans", planDAO.getPlansByPersonId(userId));
            model.addAttribute("access_plans", planDAO.getPlansAccessByPersonId(userId));

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
            Person person = personDAO.findById(userId);
            plan.setPerson_id(person.getId());
            planDAO.save(plan);
            return "redirect:/plans";
        }
        return "redirect:/authorization";
    }

    @DeleteMapping("/plan/delete")
    public String deletePlan(@RequestParam("planId") int planId, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            planDAO.deletePlan(planId);
            planDAO.deleteAccess(planId);
            return "redirect:/plans";
        }
        return "redirect:/authorization";
    }

    @DeleteMapping("/take-away-access")
    public String takeawayAccess(@RequestParam("planId") int planId, @RequestParam("personId") int personId, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            planDAO.takeawayAccess(planId, personId);
            roleDAO.deleteRole(personId, planId);
            optimizationDAO.deleteFromTaskPersonByPersonAndPlanId(personId,planId);
            return "redirect:/plans/tasks?planId="+planId;
        }
        return "redirect:/authorization";
    }
}
