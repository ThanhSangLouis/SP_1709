package vn.hcmute.entity;
import jakarta.persistence.*;          // JPA annotations
import lombok.*;    
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name")
    private String roleName;

    // Manual getter để đảm bảo hoạt động
    public String getRoleName() {
        return roleName;
    }
}