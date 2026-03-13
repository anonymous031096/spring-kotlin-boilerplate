package com.example.boilerplate.modules.iam.entity

import com.example.boilerplate.shared.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "permissions")
class Permission(@Column(nullable = false, unique = true) var name: String) : BaseEntity()
