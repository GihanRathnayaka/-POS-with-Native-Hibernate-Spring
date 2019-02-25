package lk.ijse.dep.app.business.custom.impl;

import lk.ijse.dep.app.business.Converter;
import lk.ijse.dep.app.business.custom.ManageItemsBO;
import lk.ijse.dep.app.dao.custom.ItemDAO;
import lk.ijse.dep.app.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class ManageItemsBOImpl implements ManageItemsBO {

    private ItemDAO itemDAO;
    @Autowired
    public ManageItemsBOImpl(ItemDAO itemDAO) {

       this. itemDAO =itemDAO;
    }



    public List<ItemDTO> getItems() throws Exception {
            return itemDAO.findAll().map(Converter::<ItemDTO>getDTOList).get();
    }

    @Override
    public void createItem(ItemDTO dto) throws Exception {

            itemDAO.save(Converter.getEntity(dto));



    }

    @Override
    public void updateItem(ItemDTO dto) throws Exception {

            itemDAO.update(Converter.getEntity(dto));

    }
    @Override
    public void deleteItem(String code) throws Exception {

            itemDAO.delete(code);

    }

    @Override
    public ItemDTO findItem(String itemCode) throws Exception {

            return itemDAO.find(itemCode).map(Converter::<ItemDTO>getDTO).orElse(null);


    }

//    private ItemDAO itemDAO;
//
//    public ManageItemsBOImpl() {
//        itemDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.ITEM);
//    }
//
//    public List<ItemDTO> getItems() throws Exception {
//        return itemDAO.findAll().map(Converter::<ItemDTO>getDTOList).get();
//    }
//
//    public boolean createItem(ItemDTO dto) throws Exception {
//        return itemDAO.save(Converter.getEntity(dto));
//    }
//
//    public boolean updateItem(ItemDTO dto) throws Exception {
//        return itemDAO.update(Converter.getEntity(dto));
//    }
//
//    public boolean deleteItem(String code) throws Exception {
//        return itemDAO.delete(code);
//
//    }
//
//    public ItemDTO findItem(String itemCode) throws Exception {
//        return itemDAO.find(itemCode).map(Converter::<ItemDTO>getDTO).orElse(null);
//    }
}
