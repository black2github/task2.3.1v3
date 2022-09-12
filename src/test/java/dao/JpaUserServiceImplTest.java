package dao;

import model.User;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import service.UserService;
import web.config.PersistenceJPAConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JpaUserServiceImplTest {
    private static Logger log = Logger.getLogger(JpaUserServiceImplTest.class);

    private GenericApplicationContext ctx;
    private UserService userService;
    private static final int LIST_SIZE = 2;

    @Before
    public void setUp() throws Exception {
        log.debug("setUp: <-");
        ctx = new AnnotationConfigApplicationContext(PersistenceJPAConfig.class);
        //ctx = new AnnotationConfigApplicationContext(AppInit.class);
        userService = ctx.getBean(UserService.class);
        assertNotNull(userService);
        log.debug("setUp: ->");
    }

    @After
    public void tearDown() throws Exception {
        log.debug("tearDown: <-");
        if (ctx != null) {
            ctx.close();
        }
        log.debug("tearDown: ->");
    }

    @Test
    public void listAll() {
        log.info("listAll: <-");

        // очистка базы
        List<User> list = userService.listAll();
        for (User user : list) {
            log.debug("listAll: remove entity: " + user);
            userService.delete(user);
        }

        // создание записей
        for (int i = 0; i < LIST_SIZE; i++) {
            User user = userService.create(new User("listAll" + i, "listAll" + i, i + 20));
            assertNotNull("User not created (null)", user);
        }
        list = userService.listAll();
        log.debug("listAll: entities:" + Arrays.toString(list.toArray()));
        assertEquals("Number of inserted and listed records didn't match.", LIST_SIZE, list.size());

        for (User user : list) {
            log.debug("listAll: try remove entity: " + user);
            userService.delete(user);
        }
        log.info("listAll: ->");
    }

    @Test
    public void list() {
        log.info("list: <-");
        for (int i = 0; i < LIST_SIZE; i++) {
            User user = userService.create(new User("list" + i, "ist" + i, i + 20));
            assertNotNull("User not created (null)", user);
        }
        List<User> list = userService.list(LIST_SIZE - 1);
        log.debug("list: entities:" + Arrays.toString(list.toArray()));

        assertEquals("Number of records more then requested", LIST_SIZE - 1, list.size());

        list = userService.list(LIST_SIZE);
        for (User user : list) {
            log.debug("list: try clean entity: " + user);
            userService.delete(user);
        }
        log.info("list: ->");
    }

    @Test
    public void create() {
        log.info("create: <-");
        User u1 = userService.create(new User("f1", "f2", 23));
        assertNotNull(u1);
        User u2 = userService.find(u1.getId());
        assertEquals("Inserted end found entities didn't match", u1, u2);
        log.info("create: OK. Try clean: " + u1);
        userService.delete(u1);
        log.info("create: ->");
    }

    @Test
    public void update() {
        log.info("update: <-");
        User u1 = userService.create(new User("u1", "u2", 23));
        log.info("update: created " + u1);
        assertNotNull("Create User failed (null)",u1);
        u1.setFirstName("f11");
        userService.update(u1.getId(), u1);
        User u2 = userService.find(u1.getId());
        assertEquals(u1, u2);
        log.info("update: try clean entity: " + u1);
        userService.delete(u1);
    }

    @Test
    public void delete() {
        log.info("delete: <-");
        User u1 = userService.create(new User("d1", "d2", 23));
        assertNotNull(u1);
        log.info("delete: " + u1);
        userService.delete(u1);
        u1 = userService.find(u1.getId());
        assertNull("Delete failed, found entity must be empty.", u1);
    }

    @Test
    public void find() {
        log.info("find: <-");
        User u1 = userService.create(new User("f1", "f2", 23));
        User u2 = userService.find(u1.getId());
        assertEquals(u1, u2);
        log.info("find: OK. Try clean: " + u1);
        userService.delete(u1);
    }
}