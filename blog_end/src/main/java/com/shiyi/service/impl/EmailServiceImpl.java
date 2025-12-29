package com.shiyi.service.impl;

import com.shiyi.common.RedisConstants;
import com.shiyi.entity.FriendLink;
import com.shiyi.entity.SystemConfig;
import com.shiyi.service.EmailService;
import com.shiyi.service.RedisService;
import com.shiyi.service.SystemConfigService;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.shiyi.enums.FriendLinkEnum.DOWN;
import static com.shiyi.enums.FriendLinkEnum.UP;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RedisService redisService;

    private final SystemConfigService systemConfigService;

    private final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    private final Map<Integer, Consumer<FriendLink>> map = new HashMap<>();

    @PostConstruct
    public void init(){
        try {
            SystemConfig systemConfig = systemConfigService.getCustomizeOne();
            if (systemConfig == null) {
                log.warn("系统配置未找到，邮件服务可能无法正常工作");
                return;
            }
            
            // 验证并清理配置
            String emailHost = systemConfig.getEmailHost();
            String emailUsername = systemConfig.getEmailUsername();
            String emailPassword = systemConfig.getEmailPassword();
            int emailPort = systemConfig.getEmailPort();
            
            if (emailHost == null || emailHost.trim().isEmpty()) {
                log.error("邮件服务器地址未配置");
                return;
            }
            
            if (emailUsername == null || emailUsername.trim().isEmpty()) {
                log.error("邮件发送者地址未配置");
                return;
            }
            
            // 验证发送者邮箱格式
            String cleanUsername = emailUsername.trim();
            if (!cleanUsername.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                log.error("邮件发送者地址格式不正确: {}", cleanUsername);
                return;
            }
            
            if (emailPassword == null || emailPassword.trim().isEmpty()) {
                log.error("邮件授权码未配置");
                return;
            }
            
            javaMailSender.setHost(emailHost.trim());
            javaMailSender.setUsername(cleanUsername);
            javaMailSender.setPassword(emailPassword.trim());
            javaMailSender.setPort(emailPort);
            javaMailSender.setDefaultEncoding("UTF-8");
            
            Properties p = new Properties();
            p.setProperty("mail.smtp.auth", "true");
            p.setProperty("mail.debug", "true");
            
            // QQ邮箱SSL/TLS配置
            int port = systemConfig.getEmailPort();
            if (port == 465) {
                // 端口465使用SSL
                p.setProperty("mail.smtp.ssl.enable", "true");
                p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                p.setProperty("mail.smtp.socketFactory.port", "465");
                p.setProperty("mail.smtp.socketFactory.fallback", "false");
            } else if (port == 587) {
                // 端口587使用STARTTLS
                p.setProperty("mail.smtp.starttls.enable", "true");
                p.setProperty("mail.smtp.starttls.required", "true");
                p.setProperty("mail.smtp.ssl.trust", systemConfig.getEmailHost());
            }
            
            // 设置超时时间
            p.setProperty("mail.smtp.timeout", "10000");
            p.setProperty("mail.smtp.connectiontimeout", "10000");
            
            javaMailSender.setJavaMailProperties(p);
            log.info("邮件服务初始化成功，Host: {}, Port: {}, Username: {}", 
                    systemConfig.getEmailHost(), port, systemConfig.getEmailUsername());

            //初始化策略map
            map.put(UP.getCode(),friendLink ->  friendPassSendEmail(friendLink.getEmail()));
            map.put(DOWN.getCode(),friendLink ->  friendFailedSendEmail(friendLink.getEmail(),friendLink.getReason()));
        } catch (Exception e) {
            log.error("邮件服务初始化失败", e);
        }
    }


    /**
     * 通知我
     */
    @Override
    @Async("threadPoolTaskExecutor")
    public void emailNoticeMe(String subject,String content) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(subject);
        // 设置邮件发送者
        message.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开
        message.setTo("3085755420@qq.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText(content);
        // 发送邮件
        javaMailSender.send(message);
    }

    /**
     *  审核友链通不通过发送通知
     * @param friendLink 友链对象
     */
    @Override
    public void sendFriendEmail(FriendLink friendLink) {
        Consumer<FriendLink> consumer = map.get(friendLink.getStatus());
        if (consumer != null) {
            consumer.accept(friendLink);
        }
    }

    /**
     * 友链通过发送通知
     * @param email 邮箱账号
     */
    @Override
    public void friendPassSendEmail(String email){
        String content = "<html>\n" +
                "<body>\n" +
                "    <p>您在"+"<a href='http://www.shiyit.com'>拾壹博客</a>"+"站点申请友链加入审核通过!!</span>\n" +
                "<p style='padding: 20px;'>感谢您的选择，本站将会竭尽维护好站点稳定，分享高质量的文章，欢迎相互交流互访。</p>" +
                "<p>可前往<a href='http://www.shiyit.com/links'>本站友链</a>查阅您的站点。</p></body>\n" +
                "</html>";
        try {
            send(email,content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 友链未通过发送通知
     * @param email 邮箱账号
     * @param reason 原因
     */
    @Override
    public void friendFailedSendEmail(String email,String reason){
        String content = "<html>\n" +
                "<body>\n" +
                "    <p>您在"+"<a href='http://www.shiyit.com'>拾壹博客</a>"+"站点申请的友链加入审核未通过!具体原因为:"+ reason +"</span>\n" +
                "<p style='padding: 20px;'>感谢您的选择，本站将会竭尽维护好站点稳定，分享高质量的文章，欢迎相互交流互访。</p>" +
                "<p>可前往<a href='http://www.shiyit.com/links'>本站友链</a>查阅您的站点。</p></body>\n" +
                "</html>";
        try {
            send(email,content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送邮箱验证码
     */
    @Override
    public void sendCode(String email) throws MessagingException {
        // 检查邮件配置是否已初始化
        if (javaMailSender.getHost() == null || javaMailSender.getUsername() == null) {
            log.error("邮件服务未正确初始化，请检查系统配置");
            throw new MessagingException("邮件服务未配置，请联系管理员");
        }
        
        // 验证接收者邮箱地址
        if (email == null || email.trim().isEmpty()) {
            throw new MessagingException("接收者邮箱地址不能为空");
        }
        
        String cleanEmail = email.trim();
        if (!cleanEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new MessagingException("接收者邮箱地址格式不正确: " + cleanEmail);
        }
        
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String content = "<html>\n" +
                "\t<body><div id=\"contentDiv\" onmouseover=\"getTop().stopPropagation(event);\" onclick=\"getTop().preSwapLink(event, 'html', 'ZC0004_vDfNJayMtMUuKGIAzzsWvc8');\" style=\"position:relative;font-size:14px;height:auto;padding:15px 15px 10px 15px;z-index:1;zoom:1;line-height:1.7;\" class=\"body\">\n" +
                "  <div id=\"qm_con_body\">\n" +
                "    <div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\" style=\"opacity: 1;\">\n" +
                "      <style type=\"text/css\">\n" +
                "        .qmbox h1,.qmbox \t\t\th2,.qmbox \t\t\th3 {\t\t\t\tcolor: #00785a;\t\t\t}\t\t\t.qmbox p {\t\t\t\tpadding: 0;\t\t\t\tmargin: 0;\t\t\t\tcolor: #333;\t\t\t\tfont-size: 16px;\t\t\t}\t\t\t.qmbox hr {\t\t\t\tbackground-color: #d9d9d9;\t\t\t\tborder: none;\t\t\t\theight: 1px;\t\t\t}\t\t\t.qmbox .eo-link {\t\t\t\tcolor: #0576b9;\t\t\t\ttext-decoration: none;\t\t\t\tcursor: pointer;\t\t\t}\t\t\t.qmbox .eo-link:hover {\t\t\t\tcolor: #3498db;\t\t\t}\t\t\t.qmbox .eo-link:hover {\t\t\t\ttext-decoration: underline;\t\t\t}\t\t\t.qmbox .eo-p-link {\t\t\t\tdisplay: block;\t\t\t\tmargin-top: 20px;\t\t\t\tcolor: #009cff;\t\t\t\ttext-decoration: underline;\t\t\t}\t\t\t.qmbox .p-intro {\t\t\t\tpadding: 30px;\t\t\t}\t\t\t.qmbox .p-code {\t\t\t\tpadding: 0 30px 0 30px;\t\t\t}\t\t\t.qmbox .p-news {\t\t\t\tpadding: 0px 30px 30px 30px;\t\t\t}\n" +
                "      </style>\n" +
                "      <div style=\"max-width:800px;padding-bottom:10px;margin:20px auto 0 auto;\">\n" +
                "        <table cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #fff;border-collapse: collapse; border:1px solid #e5e5e5;box-shadow: 0 10px 15px rgba(0, 0, 0, 0.05);text-align: left;width: 100%;font-size: 14px;border-spacing: 0;\">\n" +
                "          <tbody>\n" +
                "            <tr style=\"background-color: #f8f8f8;\">\n" +
                "              <td>\n" +
                "                <img style=\"padding: 15px 0 15px 30px;width:50px\" src=\"http://img.shiyit.com/FjzfvfWYZVED7eXMS4EL8KNR949K\">" +
                "                <span>拾壹博客. </span>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td class=\"p-intro\">\n" +
                "                <h1 style=\"font-size: 26px; font-weight: bold;\">验证您的邮箱地址</h1>\n" +
                "                <p style=\"line-height:1.75em;\">感谢您使用 拾壹博客. </p>\n" +
                "                <p style=\"line-height:1.75em;\">以下是您的邮箱验证码，请将它输入到 拾壹博客 的邮箱验证码输入框中:</p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td class=\"p-code\">\n" +
                "                <p style=\"color: #253858;text-align:center;line-height:1.75em;background-color: #f2f2f2;min-width: 200px;margin: 0 auto;font-size: 28px;border-radius: 5px;border: 1px solid #d9d9d9;font-weight: bold;\">"+code+"</p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td class=\"p-intro\">\n" +
                "                <p style=\"line-height:1.75em;\">这一封邮件包括一些您的私密的 拾壹博客 账号信息，请不要回复或转发它，以免带来不必要的信息泄露风险。 </p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "              <td class=\"p-intro\">\n" +
                "                <hr>\n" +
                "                <p style=\"text-align: center;line-height:1.75em;\">shiyi - 拾壹博客</p>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </tbody>\n" +
                "        </table>\n" +
                "      </div>\n" +
                "      <style type=\"text/css\">\n" +
                "        .qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}\n" +
                "      </style>\n" +
                "    </div>\n" +
                "  </div><!-- -->\n" +
                "  <style>\n" +
                "    #mailContentContainer .txt {height:auto;}\n" +
                "  </style>\n" +
                "</div></body>\n" +
                "</html>\n";
       send(cleanEmail, content);
       log.info("邮箱验证码发送成功,邮箱:{},验证码:{}", cleanEmail, code);

       redisService.setCacheObject(RedisConstants.EMAIL_CODE + cleanEmail, code + "");
       redisService.expire(RedisConstants.EMAIL_CODE + cleanEmail, RedisConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

    }

    private void send(String email, String template) throws MessagingException {
        // 验证和清理邮箱地址
        if (email == null || email.trim().isEmpty()) {
            throw new MessagingException("接收者邮箱地址不能为空");
        }
        
        String recipientEmail = email.trim();
        // 验证邮箱格式
        if (!recipientEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new MessagingException("接收者邮箱地址格式不正确: " + recipientEmail);
        }
        
        // 获取发送者邮箱
        String senderEmail = javaMailSender.getUsername();
        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            throw new MessagingException("发送者邮箱地址未配置，请检查系统配置");
        }
        
        senderEmail = senderEmail.trim();
        // 验证发送者邮箱格式
        if (!senderEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new MessagingException("发送者邮箱地址格式不正确: " + senderEmail);
        }
        
        log.debug("准备发送邮件，发送者: {}, 接收者: {}", senderEmail, recipientEmail);

        //创建一个MIME消息
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
        // 设置邮件主题
        mineHelper.setSubject("拾壹博客");
        // 设置邮件发送者
        mineHelper.setFrom(senderEmail);
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开
        mineHelper.setTo(recipientEmail);
        // 设置邮件发送日期
        mineHelper.setSentDate(new Date());
        // 设置邮件的正文
        mineHelper.setText(template, true);
        // 发送邮件
        javaMailSender.send(mimeMessage);
        
        log.debug("邮件发送成功，接收者: {}", recipientEmail);
    }
}
