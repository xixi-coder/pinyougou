package cn.itcast.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
      try {
          String sign_name = map.get("sign_name");
          System.out.println(sign_name);
          SendSmsResponse response = smsUtil.sendSms(map.get("mobile"), map.get("template_code"),
                  map.get("品优购"),
                  map.get("param"));
          System.out.println("code"+response.getCode());
          System.out.println("bizId"+response.getBizId());
          System.out.println("message"+response.getMessage());
          System.out.println("requestId"+response.getRequestId());
      } catch (ClientException e) {
          e.printStackTrace();
      }


  }
}
