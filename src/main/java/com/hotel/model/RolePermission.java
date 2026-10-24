package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "vai_tro_quyen", uniqueConstraints = @UniqueConstraint(columnNames = {"vai_tro_id", "quyen_id"}))
public class RolePermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "vai_tro_id") private SystemRole role;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "quyen_id") private SystemPermission permission;
    public Integer getId() { return id; }
    public SystemRole getRole() { return role; }
    public void setRole(SystemRole role) { this.role = role; }
    public SystemPermission getPermission() { return permission; }
    public void setPermission(SystemPermission permission) { this.permission = permission; }
}
