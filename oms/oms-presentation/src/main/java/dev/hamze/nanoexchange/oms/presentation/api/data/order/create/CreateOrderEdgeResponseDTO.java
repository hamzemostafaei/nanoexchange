package dev.hamze.nanoexchange.oms.presentation.api.data.order.create;

import dev.hamze.nanoexchange.oms.presentation.api.data.order.OrderEdgeDTO;
import dev.hamze.nanoexchange.presentation.api.data.BaseEdgeResponseDataDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateOrderEdgeResponseDTO extends BaseEdgeResponseDataDTO<OrderEdgeDTO> {
}
