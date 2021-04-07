package top.codewood.wx.service;

import top.codewood.wx.pay.v2.bean.request.*;
import top.codewood.wx.pay.v2.bean.result.*;

public interface WxPayService {

    String sign(Object object);

    void checkResultSign(String xml);

    WxPayUnifiedOrderV2Result unifiedOrder(WxPayUnifiedOrderV2Request unifiedOrderV2Request);

    WxPayOrderQueryV2Result orderQuery(WxPayOrderQueryV2Request orderQueryV2Request);

    WxPayOrderCloseV2Result closeOrder(WxPayOrderCloseV2Request orderCloseV2Request);

    WxPayRefundV2Result refund(WxPayRefundV2Request refundV2Request);

    WxPayRefundQueryV2Result refundQuery(WxPayRefundQueryV2Request refundQueryV2Request);

    String decrypt(String encryptedStr);


}
