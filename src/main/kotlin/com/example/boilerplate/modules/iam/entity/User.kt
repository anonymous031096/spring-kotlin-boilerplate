package com.example.boilerplate.modules.iam.entity

import com.example.boilerplate.shared.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
        @Column(nullable = false, unique = true) var username: String,
        @Column(nullable = false) var password: String,
        @Column(nullable = false) var name: String,
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        var status: UserStatus = UserStatus.ACTIVE,
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        var roles: MutableSet<Role> = mutableSetOf(),
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "user_permissions",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "permission_id")]
        )
        var permissions: MutableSet<Permission> = mutableSetOf()
) : BaseEntity()
