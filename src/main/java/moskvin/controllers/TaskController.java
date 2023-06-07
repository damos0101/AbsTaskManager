package moskvin.controllers;

import moskvin.dao.*;
import moskvin.models.Person;
import moskvin.models.PersonTasks;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {
    private final TaskDAO taskDAO;
    private final TaskValidator taskValidator;
    private final PlanDAO planDAO;
    private final PersonDAO personDAO;
    private final FriendshipDAO friendshipDAO;
    private final RoleDAO roleDAO;
    private final OptimizationDAO optimizationDAO;

    public TaskController(TaskDAO taskDAO, TaskValidator taskValidator, PlanDAO planDAO, PersonDAO personDAO, FriendshipDAO friendshipDAO, RoleDAO roleDAO, OptimizationDAO optimizationDAO) {
        this.taskDAO = taskDAO;
        this.taskValidator = taskValidator;
        this.planDAO = planDAO;
        this.personDAO = personDAO;
        this.friendshipDAO = friendshipDAO;
        this.roleDAO = roleDAO;
        this.optimizationDAO = optimizationDAO;
    }

    @GetMapping("/plans/tasks")
    public String viewAllTasks(@RequestParam("planId") int planId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Plan plan = planDAO.findById(planId);
            if ((plan != null && plan.getPerson_id() == userId) || planDAO.isHaveAccess(userId, planId)) {
                model.addAttribute("planId", planId);
                model.addAttribute("users", planDAO.getUsersByPlanAccess(planId));
                model.addAttribute("admin", planDAO.getPersonByPlanId(planId));
                model.addAttribute("userId", userId);
                model.addAttribute("optimizedPeople", optimizationDAO.getPeopleForOptimization(planId));
                List<Task> tasks = taskDAO.getTaskByPlanId(planId);
                for(int i = 0;i<tasks.size();++i){
                    tasks.get(i).setPerson(optimizationDAO.getPersonByTaskId(tasks.get(i).getId()));
                }
                model.addAttribute("tasks", tasks);
                //model.addAttribute("isPersonAlreadyInPlan", false);
                if (plan.getPerson_id() == userId) {
                    model.addAttribute("isCreator", true);
                } else {
                    model.addAttribute("isCreator", false);
                }
                List<Person> roles = planDAO.getUsersByPlanAccess(planId);
                List<Integer> numberRoles = new ArrayList<>();
                for (Person person : roles) {
                    int roleId = roleDAO.getRoleByPersonIdAndPlanId(person.getId(), planId);
                    numberRoles.add(roleId);
                }
                model.addAttribute("numberRoles", numberRoles);
                return "tasks/tasks";
            } else {
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
                return "redirect:/plans/tasks?planId=" + planId;
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/plan/addPerson")
    public String addAccess(@RequestParam("username") String username, @RequestParam("planId") int planId, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Person person = personDAO.findByUsername(username);
            if (person == null) {
                redirectAttributes.addFlashAttribute("userNameError", "Користувача з таким юзернеймом не знайдено");
                return "redirect:/plans/tasks?planId=" + planId;
            }
            if (friendshipDAO.areFriends(userId, person.getId())) {
                model.addAttribute("planId", planId);
                model.addAttribute("role", 1);
                planDAO.addAccess(person.getId(), planId);
                roleDAO.addRoleToUser(person.getId(), planId, 1);
                return "redirect:/plans/tasks?planId=" + planId;
            } else if (userId == person.getId()) {
                redirectAttributes.addFlashAttribute("userNameError", "Ви не можете дати доступ самому собі");
                return "redirect:/plans/tasks?planId=" + planId;
            } else {
                redirectAttributes.addFlashAttribute("userNameError", "Ви можете давати доступ тільки друзям");
                return "redirect:/plans/tasks?planId=" + planId;
            }
        }
        return "redirect:/authorization";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam("planId") int planId,
                             @RequestParam("personId") int personId,
                             @RequestParam("roleId") int roleId,
                             HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            roleDAO.updateRole(personId, planId, roleId);
            model.addAttribute("planId", planId);
            return "redirect:/plans/tasks?planId=" + planId;
        }
        return "redirect:/authorization";
    }

    @PostMapping("/add-to-plan")
    public String addToPlanPerson(@RequestParam("planId") int planId,
                                  @RequestParam("personId") int personId,
                                  HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            optimizationDAO.addPersonToPlanOptimization(planId, personId);
            model.addAttribute("planId", planId);
            //model.addAttribute("isPersonAlreadyInPlan", optimizationDAO.isPersonAlreadyInPlan(planId, personId));
            return "redirect:/plans/tasks?planId=" + planId;
        }
        return "redirect:/authorization";
    }

    @PostMapping("/optimize-plan")
    public String optimizePlan(@RequestParam("planId") int planId,
                               HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            optimizationDAO.deleteOptimization(planId);
            List<Person> optimizedPeople = optimizationDAO.getPeopleForOptimization(planId);
            List<Task> tasks = taskDAO.getTaskByPlanId(planId);
            List<PersonTasks> personTasks = new ArrayList<>();
            for (int i = 0; i < optimizedPeople.size(); ++i) {
                personTasks.add(new PersonTasks(optimizedPeople.get(i).getName(), optimizedPeople.get(i).getId()));
            }
            Optimization.optimizeTaskDistribution(personTasks, tasks);
            for (int i = 0; i < optimizedPeople.size(); ++i) {
                List<Task> pTasks = personTasks.get(i).getTasks();
                for (int j = 0; j < pTasks.size(); ++j) {
                    Task task = pTasks.get(j);
                    task.setPerson(optimizedPeople.get(i));
                    optimizationDAO.addPersonToTask(task.getId(), personTasks.get(i).getId());
                }
            }
            model.addAttribute("planId", planId);
            return "redirect:/plans/tasks?planId=" + planId;
        }
        return "redirect:/authorization";
    }

    @GetMapping("/plan/tasks/task")
    public String showTask(@RequestParam("taskId") int taskId, @RequestParam("planId") int planId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Plan plan = planDAO.findById(planId);
            if (plan.getPerson_id() == userId) {
                model.addAttribute("isCreator", true);
                model.addAttribute("roleId", 0);
            } else {
                model.addAttribute("roleId", roleDAO.getRoleByPersonIdAndPlanId(userId, planId));
                model.addAttribute("isCreator", false);
            }
            model.addAttribute("task", taskDAO.show(taskId));
            model.addAttribute("person", optimizationDAO.getPersonByTaskId(taskId));
            model.addAttribute("planId", planId);
            return "tasks/task";
        }
        return "redirect:/authorization";
    }

    @PostMapping("/task/update")
    public String updateCompleted(@RequestParam("taskId") int taskId, Model model, @RequestParam("planId") int planId, HttpSession session, @RequestParam("completed") boolean completed) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Task task = taskDAO.show(taskId);
            task.setCompleted(completed);
            taskDAO.setCompleted(completed, taskId);
            model.addAttribute("planId", planId);
            return "redirect:/plan/tasks/task?taskId=" + taskId;
        }
        return "redirect:/authorization";
    }

    @DeleteMapping("/task/delete")
    public String deleteTask(@RequestParam("taskId") int taskId, @RequestParam("planId") int planId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            taskDAO.deleteTask(taskId);
            model.addAttribute("planId", planId);
            return "redirect:/plans/tasks?planId=" + planId;
        }
        return "redirect:/authorization";
    }

    @DeleteMapping("/remove-person-optimization")
    public String removePersonOptimization(@RequestParam("planId") int planId,
                                           @RequestParam("personId") int personId,
                                           Model model, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            optimizationDAO.deleteFromTaskPersonByPersonAndPlanId(personId, planId);
            return "redirect:/plans/tasks?planId=" + planId;
        }
        return "redirect:/authorization";
    }
}
