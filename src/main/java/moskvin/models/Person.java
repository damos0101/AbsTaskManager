package moskvin.models;


import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class Person {
    private int id;
    @Pattern(regexp = "[A-Z]\\w+ [A-Z]\\w+", message = "Ім'я повинно бути у форматі Name Surname")
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @Email(message = "Пошта має бути існуючою")
    private String email;
    private String username;
    private String password;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserIdByUsername(String username){
        return id;
    }
}
