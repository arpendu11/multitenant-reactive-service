package com.stackabuse.multitenantreactiveservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@EqualsAndHashCode(of = {"username","firstName","lastName"})
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "username_firstName_lastName")
@Document
public class User implements TimeAudit {

    @Id
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String createdBy;
    private Long createdOn;
    private String lastUpdatedBy;
    private Long lastUpdatedOn;
}
