package top.codewood.wx.service;

import top.codewood.wx.pay.v2.bean.profitsharing.*;

public interface ProfitSharingService {

    ProfitSharingReceiverV2Result addReceiver(ProfitSharingReceiverV2Request receiverV2Request);

    ProfitSharingReceiverV2Result removeReceiver(ProfitSharingReceiverV2Request receiverV2Request);

    /**
     *
     * @param profitSharingV2Request
     * @param multi true 为多次分账, false 为单次分账
     * @return
     */
    ProfitSharingV2Result profitSharing(ProfitSharingV2Request profitSharingV2Request, boolean multi);

    ProfitSharingQueryV2Result query(ProfitSharingQueryV2Request queryV2Request);

    ProfitSharingV2Result finish(ProfitSharingFinishV2Request finishV2Request);

    ProfitSharingAmountQueryV2Result amountQuery(ProfitSharingAmountQueryV2Request amountQueryV2Request);
}
