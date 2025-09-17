package vn.hcmute.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "1";
        String hashedPassword = encoder.encode(password);
        System.out.println("Original password: " + password);
        System.out.println("BCrypt hash: " + hashedPassword);
    }
}
