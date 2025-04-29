package dev.hamze.nanoexchange.presentation.api.data;

import dev.hamze.nanoexchange.common.core.data.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseEdgeRequestDTO extends BaseDTO {

}
