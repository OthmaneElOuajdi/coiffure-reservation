package be.salon.coiffurereservation.repository;

import be.salon.coiffurereservation.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour la gestion des membres du personnel {@link StaffMember}.
 * <p>
 * Permet de récupérer les coiffeurs/employés actifs ou filtrés par compétences.
 * Ces données sont essentielles pour l’affichage du personnel disponible,
 * la planification et la réservation des rendez-vous.
 */
@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {

    /**
     * Retourne la liste des membres du personnel actifs, triés par ordre d’affichage.
     * <p>
     * Utilisé pour afficher les employés disponibles côté client.
     *
     * @return liste des membres du personnel actifs
     */
    List<StaffMember> findByActiveTrueOrderByDisplayOrderAsc();

    /**
     * Retourne tous les membres du personnel, actifs ou non, triés par ordre d’affichage.
     * <p>
     * Utilisé dans les interfaces d’administration.
     *
     * @return liste complète des membres du personnel
     */
    List<StaffMember> findAllByOrderByDisplayOrderAsc();

    /**
     * Recherche les membres du personnel actifs possédant une compétence spécifique.
     * <p>
     * Exemple d’utilisation : trouver les coiffeurs spécialisés en « Coloration » ou « Coupe Homme ».
     *
     * @param skill nom de la compétence recherchée
     * @return liste des membres du personnel correspondants
     */
    @Query("""
           SELECT s FROM StaffMember s
           WHERE s.active = true
             AND :skill MEMBER OF s.skills
           ORDER BY s.displayOrder
           """)
    List<StaffMember> findActiveBySkill(@Param("skill") String skill);
}
