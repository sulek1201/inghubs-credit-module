package com.inghubs.credit.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "numeric(19)")
    private Long id;

    @Column(name = "pwd", length = 200)
    private String password;

    @Column(name = "user_name", length = 100, unique = true)
    private String username;

    @Column(name = "user_role", length = 100, unique = true)
    private String userRole;

}
