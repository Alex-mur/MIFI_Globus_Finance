package it.globus.finance.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import it.globus.finance.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @JsonIgnore
    @Schema(hidden = true)
    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "reports_file_names", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag")
    private List<String> reportFileNames = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, Role role, String email, Boolean isEnabled, LocalDateTime createdAt) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.isEnabled = isEnabled;
        this.createdAt = createdAt;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotBlank(message = "Email обязателен") @Email(message = "Некорректный формат email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email обязателен") @Email(message = "Некорректный формат email") String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public void addReportFileName(String name) {
        List<String> names = getReportFileNames();
        names.add(name);
        setReportFileNames(names);
    }

    public void removeReportFileName(String name) {
        List<String> names = getReportFileNames();
        names.remove(name);
        setReportFileNames(names);
    }
}
