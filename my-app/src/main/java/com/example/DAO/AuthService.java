package com.example.DAO;

import org.mindrot.jbcrypt.BCrypt;

import com.example.JwtUtil;
import com.example.Model.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();
    private static final String ADMIN_KEY = "admin-key-123";
    private JwtUtil jwtUtil = new JwtUtil();  

    public void register(User user) throws Exception {
        if (userDAO.getUserByEmail(user.getEmail()) != null) {
            throw new Exception("Користувач із таким email вже зареєстрований");
        }
    
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new Exception("Поле email обов'язкове для заповнення");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new Exception("Поле password обов'язкове для заповнення");
        }
        if (user.getSurname() == null || user.getSurname().isEmpty()) {
            throw new Exception("Поле surname обов'язкове для заповнення");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new Exception("Поле name обов'язкове для заповнення");
        }
    
        if (isCorporateEmail(user.getEmail())) {
            if (user.getAdminKey() == null || !isValidAdminKey(user.getAdminKey())) {
                throw new Exception("Для корпоративної пошти необхідно вказати валідний adminKey");
            }
            user.setRole("admin");
        } else {
            user.setRole("user");
        }
    
        user.setPassword(hashPassword(user.getPassword()));
    
        userDAO.createUser(user);
    }

    public String login(String email, String password, String adminKey) throws Exception {
        // Перевірка обов'язкових полів
        if (email == null || email.isEmpty()) {
            throw new Exception("Поле email обов'язкове для заповнення");
        }
        if (password == null || password.isEmpty()) {
            throw new Exception("Поле password обов'язкове для заповнення");
        }
    
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new Exception("Користувача не знайдено");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Невірний пароль");
        }
    
        if (isCorporateEmail(email)) {
            if (!isValidAdminKey(adminKey)) {
                throw new Exception("Невірний adminKey для корпоративної пошти");
            }
        } else {
            if (adminKey != null && !adminKey.isEmpty()) {
                throw new Exception("adminKey не дозволено для звичайних користувачів");
            }
        }

        String token = jwtUtil.generateToken(user);
        return token;
    }

    private boolean isCorporateEmail(String email) {
        return email != null && email.endsWith("@company.com");
    }

    private boolean isValidAdminKey(String adminKey) {
        return ADMIN_KEY.equals(adminKey);
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
