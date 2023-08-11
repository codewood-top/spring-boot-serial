package top.codewood.rabbitmq.config.bean;

public interface OrderTopic {

    String NAME = "order";

    interface Keys {

        /**
         * 订单创建
         */
        String GENERATED = "generated";

        /**
         * 订单付款
         */
        String PAID = "paid";

        /**
         * 配送通知
         */
        String DELIVERY = "delivery";

        /**
         * 已送达通知
         */
        String ARRIVED = "arrived";

        /**
         * 订单确认
         */
        String CONFIRM = "confirm";

        /**
         * 订单取消
         */
        String CANCEL = "cancel";

    }

}
