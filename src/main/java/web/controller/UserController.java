package web.controller;

import model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    protected static Logger log = Logger.getLogger(UserController.class.getName());

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        // добавление пользователей при первом запуске, если еще не созданы
        List<User> users = userService.listAll();
        if (users.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                User user = userService.create(new User("firstName" + i, "secondName" + i, i));
                log.debug("list:" + user);
            }
            users = userService.listAll();
        }
    }

    /*
    GET /users/ - получение списка всех пользователей
     */
    @GetMapping()
    public String list(ModelMap model) {
        log.debug("list: <- ");

        List<User> users = userService.listAll();

        model.addAttribute("users", users);
        log.debug("list: -> " + users);
        return "users/index";
    }

    /*
    GET /users/:id - заполнение данных о конкретном пользователе для просмотра
     */
    @GetMapping("/{id}")
    public String show(@PathVariable("id") long id, ModelMap model) {
        log.debug("show: <- id=" + id);

        // получение одного пользователя по id и передача на отображение
        User user = userService.find(id);
        if (user == null) {
            model.addAttribute("message", "User with id=" + id + " not found");
        }
        model.addAttribute("user", user);
        log.debug("show: -> " + user);
        return "users/show";
    }

    /*
    GET /users/new - создание пустого объекта (для заполнения данными формы), добавление объекта в модель
    и ссылка на форму создания нового пользователя
     */
    @GetMapping(value = "/new")
    public String newUser(@ModelAttribute("user") User user) {
        log.debug("newUser: <- ");
        return "users/new";
    }

    /*
     POST /users/ - обработка данных с формы:
      - создание пользователя по объекту, зачения которого заполнены в форме
      - перенаправление на начальную страницу вывода списка
     */
    @PostMapping()
    public String create(@ModelAttribute("user") User user) {
        log.debug("create: <- " + user);
        User u = userService.create(user);
        log.debug("create: -> " + u);
        return "redirect:/users";
    }

    /*
     GET /users/:id/edit - заполнение объекта данными
     и отправка на отправка на форму редактирование данных пользователя
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        log.debug("edit: <- id=" + id);
        User user = userService.find(id);
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("message", "Warning: User with id=" + id + " not found");
        }
        return "users/edit";
    }

    /*
     PATCH /users/:id - обновление данных пользователя c конкретным id
     */
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") long id) {
        log.debug("update: <- user=" + user + ", id=" + id);
        User usr = userService.update(id, user);
        log.debug("update: -> " + usr);
        return "redirect:/users";
    }

    /*
     DELETE /users/:id - удаление пользователя по id
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, Model model) {
        log.debug("delete: <- id=" + id);
        User user = userService.find(id);
        if (user != null) {
            userService.delete(user);
        } else {
            model.addAttribute("message", "Warning: User with id=" + id + " not found");
        }
        return "redirect:/users";
    }
}
