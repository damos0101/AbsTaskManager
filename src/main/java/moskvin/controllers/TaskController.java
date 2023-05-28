package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.dao.PlanDAO;
import moskvin.dao.TaskDAO;
import moskvin.models.Person;
import moskvin.models.Plan;
import moskvin.models.Task;
import moskvin.util.TaskValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class TaskController {
    private final TaskDAO taskDAO;
    private final TaskValidator taskValidator;
    private final PlanDAO planDAO;
    private final PersonDAO personDAO;

    public TaskController(TaskDAO taskDAO, TaskValidator taskValidator, PlanDAO planDAO, PersonDAO personDAO) {
        this.taskDAO = taskDAO;
        this.taskValidator = taskValidator;
        this.planDAO = planDAO;
        this.personDAO = personDAO;
    }

    @GetMapping("/plans/tasks")
    public String viewAllTasks(@RequestParam("planId") int planId, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Plan plan = planDAO.findById(planId);
            if (plan != null && plan.getPerson_id() == userId) {
                model.addAttribute("tasks", taskDAO.getTaskByPlanId(planId));
                model.addAttribute("planId", planId);
                return "tasks/tasks";
            } else {
                // Відобразити повідомлення про помилку доступу
                return "error/access-denied";
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/plan/tasks/new")
    public String showCreateTaskForm(@RequestParam("planId") int planId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("task", new Task());
            model.addAttribute("planId", planId); // Додати planId до моделі
            return "tasks/newTask";
        }
        return "redirect:/authorization";
    }

    @PostMapping("/plan/tasks/create")
    public String createTask(@ModelAttribute("task") @Valid Task task, BindingResult bindingResult, HttpSession session, @RequestParam("planId") Integer planId) {
        taskValidator.validate(task, bindingResult);
        if (bindingResult.hasErrors()) {
            return "tasks/newTask";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Plan plan = planDAO.findById(planId);
            if (plan != null) {

                task.setPlan_id(planId);
                taskDAO.save(task);
                return "redirect:/plans/tasks?planId="+planId;
            }
        }
        return "redirect:/authorization";
    }
}
