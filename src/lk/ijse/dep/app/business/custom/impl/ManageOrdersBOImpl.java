package lk.ijse.dep.app.business.custom.impl;

import lk.ijse.dep.app.business.Converter;
import lk.ijse.dep.app.business.custom.ManageOrdersBO;
import lk.ijse.dep.app.dao.custom.*;
import lk.ijse.dep.app.dto.OrderDTO;
import lk.ijse.dep.app.dto.OrderDTO2;
import lk.ijse.dep.app.dto.OrderDetailDTO;
import lk.ijse.dep.app.entity.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class ManageOrdersBOImpl implements ManageOrdersBO {

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private ItemDAO itemDAO;
    private QueryDAO queryDAO;
    private CustomerDAO customerDAO;

    @Autowired
    public ManageOrdersBOImpl(OrderDAO orderDAO, OrderDetailDAO orderDetailDAO, ItemDAO itemDAO, QueryDAO queryDAO, CustomerDAO customerDAO) {
        this.orderDAO = orderDAO;
        this.orderDetailDAO = orderDetailDAO;
        this.itemDAO = itemDAO;
        this.queryDAO = queryDAO;
        this.customerDAO = customerDAO;
    }

    @Override
    public List<OrderDTO2> getOrdersWithCustomerNamesAndTotals() throws Exception {

        return queryDAO.findAllOrdersWithCustomerNameAndTotal().map(ce -> {
            return Converter.getDTOList(ce, OrderDTO2.class);
        }).get();

    }

    @Override

    public List<OrderDTO> getOrders() throws Exception {

           return orderDAO.findAll().map(Converter::<OrderDTO>getDTOList).get();


    }

    @Override
    public String generateOrderId() throws Exception {

        int count = queryDAO.count();

        return "O00" + (++count);
    }

    @Override
    public void createOrder(OrderDTO dto) throws Exception {


            Order order = Converter.getEntity(dto);

            Customer customer = customerDAO.find(dto.getCustomerId()).get();
            order.setCustomerId(customer);
            orderDAO.save(order);
            for (OrderDetailDTO dao : dto.getOrderDetailDTOS()) {
                OrderDetail od = Converter.getEntity(dao);
                od.setOrderDetailPK(new OrderDetailPK(dto.getId(), dao.getCode()));
                orderDetailDAO.save(od);
                Item item = itemDAO.find(dao.getCode()).get();
                item.setQtyOnHand(item.getQtyOnHand() - dao.getQty());
            }
    }

    @Override
    public OrderDTO findOrder(String orderId) throws Exception {

//        orderDAO.setSession(mySession);
        OrderDTO orderDTO = orderDAO.find(orderId).map(Converter::<OrderDTO>getDTO).orElse(null);
//        orderDetailDAO.setSession(mySession);
        List<OrderDetail> entities = orderDetailDAO.findAll().get();
        List<OrderDetailDTO> list = new ArrayList<>();
        entities.forEach(c -> {
            if (orderId.equals(c.getOrderDetailPK().getOrderId())) {
                list.add(new OrderDetailDTO(c.getOrderDetailPK().getItemCode(), c.getItemId().getDescription(), c.getQty(), c.getUnitPrice()));
            }
        });

        orderDTO.setOrderDetailDTOS(list);
        return orderDTO;

    }

//    private OrderDAO orderDAO;
//    private OrderDetailDAO orderDetailDAO;
//    private ItemDAO itemDAO;
//    private QueryDAO queryDAO;
//
//    public ManageOrdersBOImpl() {
//        orderDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER);
//        orderDetailDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ORDER_DETAIL);
//        itemDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
//        queryDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.QUERY);
//    }
//
//    public List<OrderDTO2> getOrdersWithCustomerNamesAndTotals() throws Exception {
//
//        return queryDAO.findAllOrdersWithCustomerNameAndTotal().map(ce -> {
//            return Converter.getDTOList(ce, OrderDTO2.class);
//        }).get();
//
//    }
//
//    public List<OrderDTO> getOrders() throws Exception {
//
//        List<Order> orders = orderDAO.findAll().get();
//        ArrayList<OrderDTO> tmpDTOs = new ArrayList<>();
//
//        for (Order order : orders) {
//            List<OrderDetailDTO> tmpOrderDetailsDtos = queryDAO.findOrderDetailsWithItemDescriptions(order.getId()).map(ce -> {
//                return Converter.getDTOList(ce, OrderDetailDTO.class);
//            }).get();
//
//            OrderDTO dto = new OrderDTO(order.getId(),
//                    order.getDate().toLocalDate(),
//                    order.getCustomerId(), tmpOrderDetailsDtos);
//            tmpDTOs.add(dto);
//        }
//
//        return tmpDTOs;
//    }
//
//    public String generateOrderId() throws Exception {
//        return orderDAO.count() + 1 + "";
//    }
//
//    public void createOrder(OrderDTO dto) throws Exception {
//
//        DBConnection.getConnection().setAutoCommit(false);
//
//        try {
//
//            boolean result = orderDAO.save(new Order(dto.getId(), Date.valueOf(dto.getDate()), dto.getCustomerId()));
//
//            if (!result) {
//                return;
//            }
//
//            for (OrderDetailDTO detailDTO : dto.getOrderDetailDTOS()) {
//                result = orderDetailDAO.save(new OrderDetail(dto.getId(),
//                        detailDTO.getCode(), detailDTO.getQty(), detailDTO.getUnitPrice()));
//
//                if (!result) {
//                    DBConnection.getConnection().rollback();
//                    return;
//                }
//
//                Item item = itemDAO.find(detailDTO.getCode()).get();
//                int qty = item.getQtyOnHand() - detailDTO.getQty();
//                item.setQtyOnHand(qty);
//                itemDAO.update(item);
//
//            }
//
//            DBConnection.getConnection().commit();
//
//        } catch (Exception ex) {
//            DBConnection.getConnection().rollback();
//            ex.printStackTrace();
//        } finally {
//            DBConnection.getConnection().setAutoCommit(true);
//        }
//
//    }
//
//    public OrderDTO findOrder(String orderId) throws Exception {
//        Order order = orderDAO.find(orderId).get();
//
//        List<OrderDetailDTO> tmpOrderDetailsDtos = queryDAO.findOrderDetailsWithItemDescriptions(order.getId()).map(ce -> {
//            return Converter.getDTOList(ce, OrderDetailDTO.class);
//        }).get();
//
//        return new OrderDTO(order.getId(), order.getDate().toLocalDate(), order.getCustomerId(), tmpOrderDetailsDtos);
//    }
}
