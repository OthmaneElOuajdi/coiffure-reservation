package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des prestations et services du salon {@link Service}.
 * <p>
 * Fournit des méthodes de recherche pour récupérer :
 * <ul>
 *     <li>Toutes les prestations actives (visibles côté client)</li>
 *     <li>Ou l’ensemble des prestations, triées par ordre d’affichage</li>
 * </ul>
 * <p>
 * Ces données sont utilisées notamment pour l’affichage du catalogue
 * des services disponibles et pour la gestion interne du salon.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    /**
     * Retourne la liste des services actifs, triés par ordre d’affichage croissant.
     * <p>
     * Utilisée pour afficher uniquement les prestations disponibles
     * sur le site ou dans l’application.
     *
     * @return liste des services actifs
     */
    List<Service> findByActiveTrueOrderByDisplayOrderAsc();

    /**
     * Retourne la liste de toutes les prestations, actives ou non,
     * triées par ordre d’affichage croissant.
     * <p>
     * Utilisée principalement par l’administration.
     *
     * @return liste complète des prestations
     */
    List<Service> findAllByOrderByDisplayOrderAsc();
}
