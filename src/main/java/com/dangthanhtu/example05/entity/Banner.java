package com.dangthanhtu.example05.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "banners")
@NoArgsConstructor
@AllArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image; 
    private String title; 
    private String description;
    private Boolean isActive;
}
