package uz.pdp.appspringsecurity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uz.pdp.appspringsecurity.entity.template.AbsIntegerEntity;
import uz.pdp.appspringsecurity.enums.PermissionEnum;

import java.util.Set;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Role extends AbsIntegerEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<PermissionEnum> permissions;

    private boolean isAdmin;//CAN'T MODIFY

    private boolean isUser;//DEFAULT ROLE
}
