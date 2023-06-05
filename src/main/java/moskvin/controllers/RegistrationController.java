package moskvin.controllers;

import moskvin.dao.PersonDAO;
import moskvin.models.Person;
import moskvin.util.PersonValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final PersonDAO personDAO;
    private final PersonValidator personValidator;

    public RegistrationController(PersonDAO personDAO, PersonValidator personValidator) {
        this.personDAO = personDAO;
        this.personValidator = personValidator;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("person", new Person());
        return "registration/registration";
    }

    @PostMapping
    public String register(@ModelAttribute("person") @Valid Person person,
                           BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "registration/registration";
        }
        personDAO.save(person);
        return "redirect:/authorization";
    }
}
