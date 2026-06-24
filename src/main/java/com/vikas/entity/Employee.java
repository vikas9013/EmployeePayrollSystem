package com.vikas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// CHANGED:
//  1. Added Lombok @Getter/@Setter/@NoArgsConstructor — removes all boilerplate getters/setters
//  2. Added @SQLDelete + @SQLRestriction for soft deletes — removeEmployee() now sets deleted_at
//     instead of permanently wiping the row
//  3. Added createdAt / updatedAt audit timestamps with @PrePersist / @PreUpdate
//  4. Added @CreatedBy and @LastModifiedBy with AuditingEntityListener

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "employees")
@SQLDelete(sql = "UPDATE employees SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Designation cannot be empty")
    private String designation;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "slack_invite_sent")
    private Boolean slackInviteSent = false;

    @Column(name = "training_assigned")
    private Boolean trainingAssigned = false;

    @Column(name = "payroll_configured")
    private Boolean payrollConfigured = false;

    @Column(name = "ai_onboarding_message", columnDefinition = "TEXT")
    private String aiOnboardingMessage;

    public Employee(String name, String designation) {
        this.name        = name;
        this.designation = designation;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public abstract double calculateSalary();

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[id=" + id + ", name=" + name
                + ", salary=" + calculateSalary() + "]";
    }
}
