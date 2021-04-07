package top.codewood.wx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.codewood.wx.common.api.WxConstants;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.service.OrderService;

@Controller
@RequestMapping("/wx/pay")
public class WxPayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WxAppProperties wxAppProperties;

    @GetMapping("/unifiedorder")
    public String unifiedOrder(
            @RequestParam(value = "openid", required = false, defaultValue = "") String openid,
            Model model) {
        model.addAttribute("orderNumber", orderService.generateOrderNumber());
        model.addAttribute("description", "代码坞-素材小店-大好河山");
        model.addAttribute("amount", 0.01);
        model.addAttribute("appid", wxAppProperties.getAppPropertyByType(WxConstants.AppType.MP).getAppid());
        model.addAttribute("mchid", wxAppProperties.getPay().getMchid());
        model.addAttribute("openid", openid);
        return "pay/unifiedOrder";
    }


    @GetMapping("/operate")
    public String operate(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        return "pay/operate";
    }


    @GetMapping("/query")
    public String query(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        return "pay/query";
    }

    @GetMapping("/close")
    public String close() {
        return "pay/close";
    }

    @GetMapping("/refund")
    public String refund() {
        return "pay/refund";
    }

    @GetMapping("/refundQuery")
    public String refundQuery() {
        return "pay/refundQuery";
    }

}