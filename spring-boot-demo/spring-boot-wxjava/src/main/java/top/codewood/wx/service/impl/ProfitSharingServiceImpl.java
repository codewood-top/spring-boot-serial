package top.codewood.wx.service.impl;

import org.springframework.stereotype.Service;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.pay.v2.api.ProfitSharingV2Service;
import top.codewood.wx.pay.v2.bean.profitsharing.*;
import top.codewood.wx.service.ProfitSharingService;

import javax.annotation.PostConstruct;

@Service("profitSharingService")
public class ProfitSharingServiceImpl implements ProfitSharingService {

    private ProfitSharingV2Service profitSharingV2Service;

    private WxAppProperties wxAppProperties;
    public ProfitSharingServiceImpl(WxAppProperties wxAppProperties) {
        this.wxAppProperties = wxAppProperties;
    }

    public ProfitSharingReceiverV2Result addReceiver(ProfitSharingReceiverV2Request receiverV2Request) {
        return profitSharingV2Service.addReceiver(receiverV2Request);
    }

    @Override
    public ProfitSharingReceiverV2Result removeReceiver(ProfitSharingReceiverV2Request receiverV2Request) {
        return profitSharingV2Service.removeReceiver(receiverV2Request);
    }

    @Override
    public ProfitSharingV2Result profitSharing(ProfitSharingV2Request profitSharingV2Request, boolean multi) {
        assert profitSharingV2Request != null;
        if (multi) {
            return profitSharingV2Service.multiRequest(profitSharingV2Request);
        } else {
            return profitSharingV2Service.request(profitSharingV2Request);
        }
    }

    @Override
    public ProfitSharingQueryV2Result query(ProfitSharingQueryV2Request queryV2Request) {
        assert queryV2Request != null;
        return profitSharingV2Service.query(queryV2Request);
    }

    @Override
    public ProfitSharingV2Result finish(ProfitSharingFinishV2Request finishV2Request) {
        assert finishV2Request != null;
        return profitSharingV2Service.finish(finishV2Request);
    }

    @Override
    public ProfitSharingAmountQueryV2Result amountQuery(ProfitSharingAmountQueryV2Request amountQueryV2Request) {
        assert amountQueryV2Request != null;
        return profitSharingV2Service.orderAmountQuery(amountQueryV2Request);
    }

    @PostConstruct
    public void postConstruct() {
        profitSharingV2Service = new ProfitSharingV2Service(this.wxAppProperties.getPay());
    }

}
