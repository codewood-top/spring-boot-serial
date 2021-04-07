package top.codewood.wx.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.pay.v2.api.WxPayV2Service;
import top.codewood.wx.pay.v2.bean.request.*;
import top.codewood.wx.pay.v2.bean.result.*;
import top.codewood.wx.pay.v2.util.crypt.WxPayV2CryptUtils;
import top.codewood.wx.service.WxPayService;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("wxPayService")
public class WxPayServiceImpl implements WxPayService {

    static Logger LOGGER = LoggerFactory.getLogger(WxPayServiceImpl.class);

    private WxPayV2Service wxPayV2Service;

    @Autowired
    private WxAppProperties wxAppProperties;

    @Override
    public String sign(Object object) {
        if (object instanceof Map) {
            return wxPayV2Service.sign((Map) object);
        }
        return wxPayV2Service.sign(object);
    }

    @Override
    public void checkResultSign(String xml) {
        wxPayV2Service.verifyResultSign(xml);
    }

    @Override
    public WxPayUnifiedOrderV2Result unifiedOrder(WxPayUnifiedOrderV2Request unifiedOrderV2Request) {
        assert unifiedOrderV2Request != null;
        return wxPayV2Service.unifiedOrder(unifiedOrderV2Request);
    }

    @Override
    public WxPayOrderQueryV2Result orderQuery(WxPayOrderQueryV2Request orderQueryV2Request) {
        assert orderQueryV2Request != null;
        return wxPayV2Service.orderQuery(orderQueryV2Request);
    }

    @Override
    public WxPayOrderCloseV2Result closeOrder(WxPayOrderCloseV2Request orderCloseV2Request) {
        assert orderCloseV2Request != null;
        return wxPayV2Service.closeOrder(orderCloseV2Request);
    }

    @Override
    public WxPayRefundV2Result refund(WxPayRefundV2Request refundV2Request) {
        assert refundV2Request != null;
        return wxPayV2Service.refund(refundV2Request);
    }

    @Override
    public WxPayRefundQueryV2Result refundQuery(WxPayRefundQueryV2Request refundQueryV2Request) {
        assert refundQueryV2Request != null;
        return wxPayV2Service.refundQuery(refundQueryV2Request);
    }

    @Override
    public String decrypt(String encryptedStr) {
        return WxPayV2CryptUtils.decrypt(encryptedStr, wxAppProperties.getPay().getKey());
    }

    @PostConstruct
    public void postConstruct() {
        if (wxAppProperties.getPay() == null) {
            throw new RuntimeException("WxPayConfig 未配置");
        }
        wxPayV2Service = new WxPayV2Service(wxAppProperties.getPay());
    }


}
