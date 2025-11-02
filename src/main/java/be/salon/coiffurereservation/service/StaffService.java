package be.salon.coiffurereservation.service;

import be.salon.coiffurereservation.entity.StaffMember;
import be.salon.coiffurereservation.dto.StaffMemberDto;
import be.salon.coiffurereservation.mapper.StaffMemberMapper;
import be.salon.coiffurereservation.repository.StaffMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service applicatif pour la gestion des membres du staff.
 * <p>
 * Fournit des opérations de lecture (liste, filtrage par compétence, détail)
 * et des opérations d'administration (création, mise à jour, suppression).
 * <br>
 * Les listes de staff actifs sont mises en cache pour améliorer les performances.
 */
@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffMemberRepository staffMemberRepository;
    private final StaffMemberMapper staffMemberMapper;

    /**
     * Retourne la liste des membres du staff actifs, triés par ordre d'affichage.
     * <p>
     * Résultat mis en cache sous la clé {@code "staffMembers"}.
     *
     * @return liste de {@link StaffMemberDto} actifs
     */
    @Cacheable("staffMembers")
    public List<StaffMemberDto> getAllActiveStaff() {
        List<StaffMember> staff = staffMemberRepository.findByActiveTrueOrderByDisplayOrderAsc();
        return staffMemberMapper.toDtoList(staff);
    }

    /**
     * Retourne l'ensemble des membres du staff (actifs ou non), triés par ordre d'affichage.
     *
     * @return liste complète de {@link StaffMemberDto}
     */
    public List<StaffMemberDto> getAllStaff() {
        List<StaffMember> staff = staffMemberRepository.findAllByOrderByDisplayOrderAsc();
        return staffMemberMapper.toDtoList(staff);
    }

    /**
     * Récupère un membre du staff par son identifiant.
     *
     * @param id identifiant du staff
     * @return {@link StaffMemberDto} correspondant
     * @throws IllegalArgumentException si aucun membre n'existe avec cet identifiant
     */
    public StaffMemberDto getStaffById(UUID id) {
        StaffMember staff = staffMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));
        return staffMemberMapper.toDto(staff);
    }

    /**
     * Retourne les membres du staff actifs possédant une compétence donnée.
     *
     * @param skill nom/clé de compétence (ex: "coloration", "barber", ...)
     * @return liste de {@link StaffMemberDto} correspondant au filtre
     */
    public List<StaffMemberDto> getStaffBySkill(String skill) {
        List<StaffMember> staff = staffMemberRepository.findActiveBySkill(skill);
        return staffMemberMapper.toDtoList(staff);
    }

    /**
     * Crée un nouveau membre du staff et invalide le cache des listes.
     *
     * @param dto données du membre à créer
     * @return {@link StaffMemberDto} créé
     */
    @Transactional
    @CacheEvict(value = "staffMembers", allEntries = true)
    public StaffMemberDto createStaff(StaffMemberDto dto) {
        StaffMember staff = staffMemberMapper.toEntity(dto);
        staff = staffMemberRepository.save(staff);
        return staffMemberMapper.toDto(staff);
    }

    /**
     * Met à jour un membre du staff existant et invalide le cache des listes.
     *
     * @param id  identifiant du membre à mettre à jour
     * @param dto nouvelles données
     * @return {@link StaffMemberDto} mis à jour
     * @throws IllegalArgumentException si le membre n'existe pas
     */
    @Transactional
    @CacheEvict(value = "staffMembers", allEntries = true)
    public StaffMemberDto updateStaff(UUID id, StaffMemberDto dto) {
        StaffMember staff = staffMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setEmail(dto.getEmail());
        staff.setPhone(dto.getPhone());
        staff.setBio(dto.getBio());
        staff.setPhotoUrl(dto.getPhotoUrl());
        staff.setSkills(dto.getSkills());
        staff.setActive(dto.getActive());
        staff.setDisplayOrder(dto.getDisplayOrder());

        staff = staffMemberRepository.save(staff);
        return staffMemberMapper.toDto(staff);
    }

    /**
     * Supprime un membre du staff et invalide le cache des listes.
     *
     * @param id identifiant du membre à supprimer
     */
    @Transactional
    @CacheEvict(value = "staffMembers", allEntries = true)
    public void deleteStaff(UUID id) {
        staffMemberRepository.deleteById(id);
    }
}
