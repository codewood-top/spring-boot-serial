package top.codewood.config.rabbitmq.bean;

public interface UserAccountTopic {

    String NAME = "user.account";

    interface Keys {
        /**
         * 账户创建
         */
        String CREATE = "create";

        /**
         * 账户变动
         */
        String UPDATE = "update";

        /**
         * 账户提现
         */
        String WITHDRAW = "withdraw";

    }

}
