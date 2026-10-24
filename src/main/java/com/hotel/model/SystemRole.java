package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "he_thong_vai_tro")
public class SystemRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private Integer id;
    @Column(name = "ma_vai_tro", nullable = false, unique = true, length = 30) private String code;
    @Column(name = "ten_hien_thi", nullable = false, length = 100) private String displayName;
    @Column(name = "mo_ta", length = 255) private String description;
    @Column(name = "trang_thai", nullable = false) private boolean active = true;
    public Integer getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
