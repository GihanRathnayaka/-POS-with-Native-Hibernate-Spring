package lk.ijse.dep.app.business;

import lk.ijse.dep.app.dto.*;
import lk.ijse.dep.app.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    public static <T extends SuperDTO> T getDTO(SuperEntity entity) {
        if (entity instanceof Customer) {
            Customer c = (Customer) entity;
            return (T) new CustomerDTO(c.getId(), c.getName(), c.getAddress());
        } else if (entity instanceof Item) {
            Item i = (Item) entity;
            return (T) new ItemDTO(i.getCode(), i.getDescription(), i.getUnitPrice(), i.getQtyOnHand());
        }
        else if (entity instanceof Order) {
            Order o = (Order) entity;
            return (T) new OrderDTO(o.getId(),o.getDate(),o.getCustomerId().getId(),new ArrayList<OrderDetailDTO>());
        }else if (entity instanceof OrderDetail) {
            OrderDetail o = (OrderDetail) entity;
            return (T) new OrderDetailDTO(o.getOrderDetailPK().getItemCode(),o.getOrderDetailPK().getOrderId(),o.getQty(),o.getUnitPrice());
        }
        else {
            throw new RuntimeException("This entity can't be converted to a DTO");
        }
    }

    public static <T extends SuperEntity> T getEntity(SuperDTO dto) {
        if (dto instanceof CustomerDTO) {
            CustomerDTO c = (CustomerDTO) dto;
            return (T) new Customer(c.getId(), c.getName(), c.getAddress());
        } else if (dto instanceof ItemDTO) {
            ItemDTO i = (ItemDTO) dto;
            return (T) new Item(i.getCode(), i.getDescription(), i.getUnitPrice(), i.getQtyOnHand());
        } else if (dto instanceof OrderDTO) {
            OrderDTO o = (OrderDTO) dto;
            return (T) new Order(o.getId(),o.getDate(),new Customer());
        } else if (dto instanceof OrderDetailDTO) {
            OrderDetailDTO o = (OrderDetailDTO) dto;
            return (T) new OrderDetail(new OrderDetailPK(),o.getQty(),o.getUnitPrice());
        }
        else {
            throw new RuntimeException("This DTO can't be converted to an entity");
        }
    }

    public static <T extends SuperDTO> List<T> getDTOList(List<? extends SuperEntity> entities) {
        return entities.stream().map(Converter::<T>getDTO).collect(Collectors.toList());
    }

    public static <T extends SuperEntity> List<T> getEntityList(List<? extends SuperDTO> dtos) {
        return dtos.stream().map(Converter::<T>getEntity).collect(Collectors.toList());

    }

    // =========================================================================

    public static <T extends SuperDTO> T getDTO(CustomEntity entity, Class<T> dtoClass) {
        if (dtoClass == OrderDTO2.class) {
            return (T) new OrderDTO2(entity.getOrderId(), entity.getOrderDate().toLocalDate(),
                    entity.getCustomerId(), entity.getCustomerName(), entity.getTotal());
        } else if (dtoClass == OrderDetailDTO.class) {
            return (T) new OrderDetailDTO(entity.getItemCode(), entity.getDescription(), entity.getQty(), entity.getUnitPrice());
        } else {
            throw new RuntimeException("Not Supported DTO");
        }
    }

    public static <T extends SuperDTO> List<T> getDTOList(List<CustomEntity> list, Class<T> dtoClass) {
        return list.stream().map(c -> getDTO(c, dtoClass)).collect(Collectors.toList());
    }

}
