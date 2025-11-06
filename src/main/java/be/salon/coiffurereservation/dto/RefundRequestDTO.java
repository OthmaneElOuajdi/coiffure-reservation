package be.salon.coiffurereservation.dto;

import be.salon.coiffurereservation.entity.RefundRequest.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDTO {
    private String id;
    private String appointmentId;
    private String userId;
    private String userName;
    private String userEmail;
    private String appointmentDate;
    private String serviceName;
    private Double amount;
    private String reason;
    private String justificationFileName;
    private String justificationFileUrl;
    private RefundStatus status;
    private String adminComment;
    private String processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
