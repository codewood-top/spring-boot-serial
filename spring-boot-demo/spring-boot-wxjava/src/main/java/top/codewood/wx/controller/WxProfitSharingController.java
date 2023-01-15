package top.codewood.wx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.codewood.wx.config.property.WxAppProperties;
import top.codewood.wx.pay.v2.common.WxPayConstants;
import top.codewood.wx.service.ProfitSharingService;

@Controller
@RequestMapping("/wx/profitsharing")
public class WxProfitSharingController {

    private WxAppProperties wxAppProperties;
    private ProfitSharingService profitSharingService;

    public WxProfitSharingController(WxAppProperties wxAppProperties, ProfitSharingService profitSharingService) {
        this.wxAppProperties = wxAppProperties;
        this.profitSharingService = profitSharingService;
    }

    @GetMapping("/receiver")
    public String receiver(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        model.addAttribute("receiverTypes", WxPayConstants.ProfitSharingReceiverType.values());
        model.addAttribute("receiverRelationTypes", WxPayConstants.ProfitShargingReceiverRelationType.values());
        return "profitSharing/receiver";
    }

    @GetMapping("/profitsharing")
    public String profitSharing(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        model.addAttribute("receiverTypes", WxPayConstants.ProfitSharingReceiverType.values());
        return "profitSharing/profitSharing";
    }

    @GetMapping("/finish")
    public String finish(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        return "profitSharing/finish";
    }

    @GetMapping("/query")
    public String query(Model model) {
        model.addAttribute("apps", wxAppProperties.getApps());
        return "profitSharing/query";
    }


}
