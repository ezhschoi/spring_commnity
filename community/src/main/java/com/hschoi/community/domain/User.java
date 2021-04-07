package com.hschoi.community.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.hschoi.community.domain.enums.SocialType;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table
public class User implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;
    
    @Column
    private String prnicipal;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updateDate;

    @Builder
    public User(String name, String password, String email, String principal,
    			SocialType socialType, LocalDateTime createdDate, 
    			LocalDateTime updateDate)
    {
        this.name = name;
        this.password = password;
        this.email = email;
        this.prnicipal = principal;
        this.socialType = socialType;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }
}
