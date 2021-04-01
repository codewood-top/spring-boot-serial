package top.codewood.service.bean.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderParam implements Serializable {

    private String orderNumber;
    private String name;
    private String telephone;
    private String province;
    private String city;
    private String town;
    private String address;
    private Long userId;
    private List<ItemParam> items;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ItemParam> getItems() {
        return items;
    }

    public void setItems(List<ItemParam> items) {
        this.items = items;
    }

    public void addItem(Long goodsId, int quantity) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        ItemParam itemParam = null;
        for (ItemParam item : items) {
            if (item.getGoodsId().equals(goodsId)) itemParam = item;
        }
        if (itemParam != null) {
            itemParam.setQuantity(itemParam.getQuantity() + quantity);
        } else {
            itemParam = new ItemParam(goodsId, quantity);
        }
        this.items.add(itemParam);
    }

    public static class ItemParam implements Serializable {
        private Long goodsId;
        private int quantity;

        public ItemParam() {}

        public ItemParam(Long goodsId, int quantity) {
            this.goodsId = goodsId;
            this.quantity = quantity;
        }

        public Long getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Long goodsId) {
            this.goodsId = goodsId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

}
