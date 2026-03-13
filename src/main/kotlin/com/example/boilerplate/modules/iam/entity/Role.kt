package com.example.boilerplate.modules.iam.entity

import com.example.boilerplate.shared.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "roles")
class Role(
        @Column(nullable = false, unique = true) var name: String,
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "role_permissions",
                joinColumns = [JoinColumn(name = "role_id")],
                inverseJoinColumns = [JoinColumn(name = "permission_id")]
        )
        var permissions: MutableSet<Permission> = mutableSetOf()
) : BaseEntity()
