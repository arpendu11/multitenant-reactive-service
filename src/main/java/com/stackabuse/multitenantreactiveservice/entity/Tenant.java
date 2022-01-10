package com.stackabuse.multitenantreactiveservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ToString
@EqualsAndHashCode(of = {"tenantId","key"})
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "key")
@Document
public class Tenant implements TimeAudit {

    @Id
    private String id;
    private Long tenantId;

    @Size(min = 1,
            max = 8,
            message = "Tenant key must be between 1 and 8 characters")
    @Pattern(regexp = "^[a-z0-9]{1,8}$",
            message = "Tenant key must be lowercase alphanumeric ASCII"
                    + " value between 1 and 8 characters inclusive")
    private String key;
    private Boolean enabled;
    private String createdBy;
    private Long createdOn;
    private String lastUpdatedBy;
    private Long lastUpdatedOn;
}
