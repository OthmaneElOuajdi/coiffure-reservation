package be.salon.coiffurereservation.mapper;

import be.salon.coiffurereservation.entity.StaffMember;
import be.salon.coiffurereservation.dto.StaffMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct responsable de la conversion entre les entités {@link StaffMember}
 * et leurs représentations de transfert de données {@link StaffMemberDto}.
 *
 * <p>Ce mapper est automatiquement implémenté par MapStruct au moment de la compilation.
 * Il est utilisé pour convertir les objets du domaine vers des DTO (et inversement),
 * facilitant ainsi la communication entre la couche métier et les couches
 * d’exposition (API REST, interface admin, etc.).</p>
 *
 * <p>L’annotation {@code @Mapper(componentModel = "spring")} permet à Spring
 * de détecter et d’injecter automatiquement le mapper en tant que bean.</p>
 *
 * <h3>Règles de conversion spécifiques :</h3>
 * <ul>
 *   <li>Le champ {@code fullName} du DTO est calculé à partir de la méthode
 *       {@link StaffMember#getFullName()}.</li>
 *   <li>Les champs {@code createdAt} et {@code updatedAt} sont ignorés lors
 *       de la conversion du DTO vers l’entité, car ils sont gérés automatiquement
 *       par le système de persistance.</li>
 * </ul>
 *
 * <p>Exemples d’utilisation :</p>
 * <pre>
 * {@code
 * // Conversion d'une entité vers un DTO
 * StaffMemberDto dto = staffMemberMapper.toDto(staffEntity);
 *
 * // Conversion d'une liste d'entités
 * List<StaffMemberDto> dtos = staffMemberMapper.toDtoList(staffList);
 *
 * // Conversion inverse (DTO → Entité)
 * StaffMember entity = staffMemberMapper.toEntity(dto);
 * }
 * </pre>
 *
 * @see be.salon.coiffurereservation.dto.StaffMemberDto
 * @see be.salon.coiffurereservation.entity.StaffMember
 */
@Mapper(componentModel = "spring")
public interface StaffMemberMapper {

    /**
     * Convertit une entité {@link StaffMember} en {@link StaffMemberDto}.
     *
     * @param staffMember l’entité à convertir
     * @return un DTO contenant les données du membre du staff
     */
    @Mapping(target = "fullName", expression = "java(staffMember.getFullName())")
    StaffMemberDto toDto(StaffMember staffMember);

    /**
     * Convertit une liste d’entités {@link StaffMember} en liste de {@link StaffMemberDto}.
     *
     * @param staffMembers la liste d’entités à convertir
     * @return la liste des DTO correspondants
     */
    List<StaffMemberDto> toDtoList(List<StaffMember> staffMembers);

    /**
     * Convertit un {@link StaffMemberDto} en entité {@link StaffMember}.
     *
     * <p>Les champs {@code createdAt} et {@code updatedAt} sont ignorés,
     * car ils sont automatiquement gérés par la base de données.</p>
     *
     * @param dto le DTO à convertir
     * @return une entité prête à être persistée
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StaffMember toEntity(StaffMemberDto dto);
}
