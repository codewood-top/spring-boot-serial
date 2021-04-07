package top.codewood.wx.service.impl;

import org.springframework.stereotype.Service;
import top.codewood.wx.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Override
    public String generateOrderNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(DateTimeFormatter.ofPattern("yyyyMMddHHMM").format(LocalDateTime.now()));
        for (int i = 0; i < 6; i++) {
            sb.append(Double.valueOf(Math.random() * 10).intValue());
        }
        return sb.toString();
    }

}
