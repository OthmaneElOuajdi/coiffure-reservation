package be.salon.coiffurereservation.security;

import be.salon.coiffurereservation.entity.User;
import be.salon.coiffurereservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implémentation personnalisée de {@link UserDetailsService} pour l’authentification
 * basée sur l’entité {@link User}.
 *
 * <p>Cette classe est utilisée par Spring Security pour charger les informations
 * d’un utilisateur au moment de la connexion (authentification par e-mail).</p>
 *
 * <p>Elle s’appuie sur le {@link UserRepository} pour interroger la base
 * et retourne une instance de {@link CustomUserDetails} utilisée par le moteur
 * de sécurité pour vérifier le mot de passe et les rôles.</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository d’accès aux utilisateurs persistés. */
    private final UserRepository userRepository;

    /**
     * Charge un utilisateur à partir de son e-mail (identifiant de connexion).
     *
     * @param email adresse e-mail saisie par l’utilisateur
     * @return les informations d’authentification de l’utilisateur
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet e-mail
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));
    }
}
