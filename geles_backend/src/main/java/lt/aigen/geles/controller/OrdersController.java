package lt.aigen.geles.controller;

import lt.aigen.geles.models.FlowerInOrder;
import lt.aigen.geles.models.Order;
import lt.aigen.geles.models.dto.FlowerInOrderDTO;
import lt.aigen.geles.models.dto.OrderDTO;
import lt.aigen.geles.repositories.FlowerInOrderRepository;
import lt.aigen.geles.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrderRepository orderRepository;
    private final FlowerInOrderRepository flowerInOrderRepository;
    private final ModelMapper modelMapper;

    public OrdersController(OrderRepository orderRepository, FlowerInOrderRepository flowerInOrderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.flowerInOrderRepository = flowerInOrderRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @PostMapping("/")
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO) {
        //List<FlowerInOrderDTO> formDtos = orderDTO.getOrderFlowers();
        //validateProductsExistence(formDtos);
        Order order = convertFromDTO(orderDTO);
        List<FlowerInOrder> flowersInOrder = new ArrayList<>();
        //orderDTO.getOrderFlowers().forEach(flowerInOrderDTO -> flowersInOrder.add(convertFromDTO(flowerInOrderDTO)));

        order.setOrderStatus(Order.OrderStatus.UNPAID);
        order.setOrderProducts(null);
        order = orderRepository.save(order);

        for (var f : orderDTO.getOrderFlowers()) {
            var flowerInOrder = convertFromDTO(f);
            flowerInOrder.setOrder(order);
            flowerInOrder = flowerInOrderRepository.save(flowerInOrder);
            flowersInOrder.add(flowerInOrder);
        }
        order.setOrderProducts(flowersInOrder);
        return new ResponseEntity<>(convertToDTO(order), HttpStatus.CREATED);
    }


    @GetMapping("/")
    public List<OrderDTO> getOrders() {
        return orderRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO convertToDTO(Order order) { return modelMapper.map(order, OrderDTO.class); }
    private Order convertFromDTO(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public FlowerInOrderDTO convertToDTO(FlowerInOrder flowerInOrder) { return modelMapper.map(flowerInOrder, FlowerInOrderDTO.class); }
    private FlowerInOrder convertFromDTO(FlowerInOrderDTO flowerInOrderDTO) {
        return modelMapper.map(flowerInOrderDTO, FlowerInOrder.class);
    }
}
