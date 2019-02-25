package lk.ijse.dep.app.dao.custom.impl;

import lk.ijse.dep.app.dao.custom.QueryDAO;
import lk.ijse.dep.app.entity.CustomEntity;
import lk.ijse.dep.app.entity.Order;
import lk.ijse.dep.app.entity.OrderDetail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuaryDaoImpl implements QueryDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Optional<List<CustomEntity>> findOrderDetailsWithItemDescriptions(String orderId) throws Exception {
        List<OrderDetail> list = getSession().createQuery("select o FROM OrderDetail o WHERE o.orderId='"+orderId+"'", OrderDetail.class).list();
        List <CustomEntity> ce = new ArrayList<>();
        list.forEach(od -> {
            ce.add(new CustomEntity(od.getItemId().getCode(), od.getQty(), od.getUnitPrice(), od.getItemId().getDescription()));
        });

        return Optional.of(ce);
    }

    @Override
    public Optional<List<CustomEntity>> findAllOrdersWithCustomerNameAndTotal() throws Exception {

        List<Order> list = getSession().createQuery("select o FROM  Order o", Order.class).list();

        List<CustomEntity> entities = new ArrayList<>();

        list.forEach(order -> {

            Object singleResult = getSession().createQuery("SELECT sum( o.unitPrice*o .qty) as totle FROM OrderDetail as o WHERE o.orderId='" + order.getId() + "' group by o.orderId ").getSingleResult();
            System.out.println(singleResult);
            double total = Double.parseDouble(singleResult.toString());
            entities.add(new CustomEntity(order.getId(), (Date) order.getDate(), order.getCustomerId().getId(), order.getCustomerId().getName(), total));
        });

        return Optional.of(entities);
    }


    @Override
    public int count() throws Exception {

        List list = getSession().createQuery("select count (o.id) FROM  Order o").list();
        System.out.println(list.get(0));

        return Integer.parseInt(list.get(0).toString());
    }

//    public void setSession(Session session){
//        this.session=session;
//    }

    @Override
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
