package be.salon.coiffurereservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO pour la création d'un utilisateur par un administrateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 80, message = "First name must be between 2 and 80 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 80, message = "Last name must be between 2 and 80 characters")
    private String lastName;

    private String phone;

    /** Rôles à attribuer (par défaut ROLE_CLIENT) */
    private Set<String> roles;

    /** Indique si le compte est actif */
    @Builder.Default
    private Boolean active = true;

    /** Indique si l'email est vérifié (true par défaut pour les comptes créés par admin) */
    @Builder.Default
    private Boolean emailVerified = true;
}
