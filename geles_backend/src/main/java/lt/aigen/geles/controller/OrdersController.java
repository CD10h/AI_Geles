package lt.aigen.geles.controller;

import lt.aigen.geles.models.FlowerInOrder;
import lt.aigen.geles.models.Order;
import lt.aigen.geles.models.dto.FlowerInOrderDTO;
import lt.aigen.geles.models.dto.OrderDTO;
import lt.aigen.geles.repositories.FlowerInOrderRepository;
import lt.aigen.geles.repositories.OrderRepository;
import lt.aigen.geles.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrderRepository orderRepository;
    private final FlowerInOrderRepository flowerInOrderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public OrdersController(OrderRepository orderRepository, FlowerInOrderRepository flowerInOrderRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.flowerInOrderRepository = flowerInOrderRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        modelMapper.typeMap(Order.class, OrderDTO.class).addMapping((order -> 0.0), OrderDTO::setTotalOrderPrice); //HACK
    }

    @Transactional
    @PostMapping("/")
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO, @CookieValue("user") Optional<String> userName) {
        if (userName.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        var user = userRepository.findByUsername(userName.get());
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Order order = convertFromDTO(orderDTO);
        List<FlowerInOrder> flowersInOrder = new ArrayList<>();

        order.setOrderStatus(Order.OrderStatus.UNPAID);
        order.setOrderProducts(null);
        order.setUser(user.get());
        order = orderRepository.save(order);

        for (var f : orderDTO.getOrderFlowers()) {
            var flowerInOrder = convertFromDTO(f);
            flowerInOrder.setOrder(order);
            flowersInOrder.add(flowerInOrder);
        }

        flowersInOrder = flowerInOrderRepository.saveAll(flowersInOrder);
        order.setOrderProducts(flowersInOrder); //so it adds it to DTO

        return new ResponseEntity<>(convertToDTO(order), HttpStatus.CREATED);
    }


    @GetMapping("/")
    public ResponseEntity<List<OrderDTO>> getOrders(@CookieValue("user") Optional<String> userName) {
        if (userName.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (userName.get().equals("admin")) // ;)
            return new ResponseEntity<>(orderRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()), HttpStatus.OK);
        var user = userRepository.findByUsername(userName.get());
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        var orders = orderRepository.findAllByUser(user.get()).stream().map(this::convertToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDTO> payForOrder(@CookieValue("user") Optional<String> userName, @PathVariable Long id) {
        if (userName.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        var user = userRepository.findByUsername(userName.get());
        if (user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        var order = orderRepository.getOne(id);
        if (!order.getUser().getId().equals(user.get().getId()))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (order.getOrderStatus() != Order.OrderStatus.UNPAID)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        order.setOrderStatus(Order.OrderStatus.PAID);
        order = orderRepository.save(order);
        return new ResponseEntity<>(convertToDTO(order), HttpStatus.OK);
    }

    public OrderDTO convertToDTO(Order order) {
        var r = modelMapper.map(order, OrderDTO.class);
        r.setTotalOrderPrice(orderRepository.getOrderPrice(order));
        return r;
    }

    private Order convertFromDTO(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public FlowerInOrderDTO convertToDTO(FlowerInOrder flowerInOrder) {
        return modelMapper.map(flowerInOrder, FlowerInOrderDTO.class);
    }

    private FlowerInOrder convertFromDTO(FlowerInOrderDTO flowerInOrderDTO) {
        return modelMapper.map(flowerInOrderDTO, FlowerInOrder.class);
    }
}
