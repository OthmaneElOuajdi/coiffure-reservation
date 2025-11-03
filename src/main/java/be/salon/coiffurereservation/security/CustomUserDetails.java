package be.salon.coiffurereservation.security;

import be.salon.coiffurereservation.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implémentation personnalisée de {@link UserDetails} pour l’intégration
 * de l’entité {@link User} avec Spring Security.
 *
 * <p>Cette implémentation est désormais un {@code record}, ce qui la rend
 * immuable, concise et adaptée aux besoins d’un simple conteneur de données
 * pour l’utilisateur connecté.</p>
 *
 * <p>Elle adapte le modèle métier {@code User} au format attendu
 * par le framework d’authentification, en exposant notamment :
 * <ul>
 *   <li>L'adresse e-mail comme nom d’utilisateur ;</li>
 *   <li>le mot de passe hashé ;</li>
 *   <li>Les rôles convertis en {@link SimpleGrantedAuthority} ;</li>
 *   <li>L'état actif/désactivé du compte.</li>
 * </ul>
 *
 * <p>Elle est utilisée par {@link CustomUserDetailsService} lors du chargement
 * de l’utilisateur dans la méthode {@code loadUserByUsername(...)}.</p>
 */
public record CustomUserDetails(User user) implements UserDetails {

    /**
     * Retourne les autorités (rôles) de l’utilisateur sous forme de {@link GrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Retourne le mot de passe hashé stocké en base.
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Retourne l’e-mail de l’utilisateur (identifiant de connexion).
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Le compte n’expire jamais (par défaut).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Retourne {@code true} si le compte n’est pas verrouillé.
     * <p>Un compte inactif est considéré comme "verrouillé".</p>
     */
    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(user.getActive());
    }

    /**
     * Les identifiants ne sont jamais expirés (par défaut).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Retourne {@code true} si l’utilisateur est actif.
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getActive());
    }
}
