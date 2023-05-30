package moskvin.controllers;

import moskvin.dao.FriendshipDAO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class TaskController {
    private final TaskDAO taskDAO;
    private final TaskValidator taskValidator;
    private final PlanDAO planDAO;
    private final PersonDAO personDAO;
    private final FriendshipDAO friendshipDAO;

    public TaskController(TaskDAO taskDAO, TaskValidator taskValidator, PlanDAO planDAO, PersonDAO personDAO, FriendshipDAO friendshipDAO) {
        this.taskDAO = taskDAO;
        this.taskValidator = taskValidator;
        this.planDAO = planDAO;
        this.personDAO = personDAO;
        this.friendshipDAO = friendshipDAO;
    }

    @GetMapping("/plans/tasks")
    public String viewAllTasks(@RequestParam("planId") int planId, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Plan plan = planDAO.findById(planId);
            if ((plan != null && plan.getPerson_id() == userId)|| planDAO.isHaveAccess(userId, planId)) {
                model.addAttribute("tasks", taskDAO.getTaskByPlanId(planId));
                model.addAttribute("planId", planId);
                model.addAttribute("users", planDAO.getUsersByPlanAccess(planId));
                model.addAttribute("admin", planDAO.getPersonByPlanId(planId));
                if(plan.getPerson_id()==userId) {
                    model.addAttribute("isCreator", true);
                }
                else{
                    model.addAttribute("isCreator", false);
                }
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

    @PostMapping("plan/addPerson")
    public String addAccess(@RequestParam("username") String username, @RequestParam("planId") int planId, HttpSession session, RedirectAttributes redirectAttributes, Model model){
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId!=null){
            Person person = personDAO.findByUsername(username);
            if(person==null){
                redirectAttributes.addFlashAttribute("userNameError", "Користувача з таким юзернеймом не знайдено");
                return "redirect:/plans/tasks?planId="+planId;
            }
            if(friendshipDAO.areFriends(userId, person.getId())) {
                model.addAttribute("planId", planId);
                planDAO.addAccess(person.getId(), planId);
                return "redirect:/plans/tasks?planId="+planId;
            }
            else if(userId==person.getId()){
                redirectAttributes.addFlashAttribute("userNameError", "Ви не можете дати доступ самому собі");
                return "redirect:/plans/tasks?planId="+planId;
            }
            else {
                redirectAttributes.addFlashAttribute("userNameError", "Ви можете давати доступ тільки друзям");
                return "redirect:/plans/tasks?planId="+planId;
            }
        }
        return "redirect:/authorization";
    }

    @GetMapping("/plan/tasks/task")
    public String showTask(@RequestParam("taskId") int taskId, @RequestParam("planId") int planId, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId!=null){
            model.addAttribute("task", taskDAO.show(taskId));
            model.addAttribute("planId", planId);
            return "tasks/task";
        }
        return "redirect:/authorization";
    }

    @PostMapping("/task/update")
    public String updateCompleted(@RequestParam("taskId") int taskId, Model model, @RequestParam("planId") int planId, HttpSession session, @RequestParam("completed") boolean completed){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId!=null){
            Task task = taskDAO.show(taskId);
            task.setCompleted(completed);
            taskDAO.setCompleted(completed, taskId);
            model.addAttribute("planId", planId);
            return "redirect:/plan/tasks/task?taskId="+taskId;
        }
        return "redirect:/authorization";
    }

    @DeleteMapping("/task/delete")
    public String deleteTask(@RequestParam("taskId") int taskId, @RequestParam("planId") int planId, Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId!=null){
            taskDAO.deleteTask(taskId);
            model.addAttribute("planId", planId);
            return "redirect:/plans/tasks?planId="+planId;
        }
        return "redirect:/authorization";
    }
}
