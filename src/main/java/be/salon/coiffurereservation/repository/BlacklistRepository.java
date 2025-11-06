package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion de la blacklist {@link Blacklist}.
 * <p>
 * Fournit des méthodes pour vérifier si un email ou un téléphone est banni.
 */
@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, UUID> {

    /**
     * Vérifie si un email est dans la blacklist.
     *
     * @param email adresse email à vérifier
     * @return true si l'email est banni
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un numéro de téléphone est dans la blacklist.
     *
     * @param phone numéro de téléphone à vérifier
     * @return true si le téléphone est banni
     */
    boolean existsByPhone(String phone);
}
