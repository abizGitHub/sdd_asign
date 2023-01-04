package com.abiz.security;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_DETAIL")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String password;

    @Column(unique = true)
    private String username;

    @Column
    private String roles;

}
