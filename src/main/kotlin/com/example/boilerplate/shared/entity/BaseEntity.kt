package com.example.boilerplate.shared.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.Instant
import java.util.UUID
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(updatable = false, nullable = false)
        var id: UUID? = null,
        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        var createdAt: Instant? = null,
        @UpdateTimestamp @Column(nullable = false) var updatedAt: Instant? = null,
        @CreatedBy @Column(name = "created_by", updatable = false) var createdBy: UUID? = null,
        @LastModifiedBy @Column(name = "updated_by") var updatedBy: UUID? = null
)
