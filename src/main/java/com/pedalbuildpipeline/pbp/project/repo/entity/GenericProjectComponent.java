package com.pedalbuildpipeline.pbp.project.repo.entity;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DiscriminatorValue("GENERIC")
public class GenericProjectComponent extends BaseProjectComponent {
}
