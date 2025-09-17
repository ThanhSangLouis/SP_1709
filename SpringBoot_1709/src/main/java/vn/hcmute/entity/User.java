package vn.hcmute.entity;

import jakarta.persistence.*;          // JPA annotations
import lombok.*;                      // Lombok annotations

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")        // map đúng với cột trong SQL Server
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Manual getters để đảm bảo hoạt động
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
