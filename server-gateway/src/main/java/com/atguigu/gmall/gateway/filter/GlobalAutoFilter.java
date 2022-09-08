package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalAutoFilter implements GlobalFilter {
    AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    AuthUrlProperties urlProperties;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        String uri = exchange.getRequest().getURI().toString();
        log.info("{} 请求开始", path);
        //无需登录就能够访问的资源，也就是前端的静态资源，直接放行

        for (String url : urlProperties.getNoAuthUrl()) {

            boolean match = matcher.match(url, path);
            //直接放行
            if (match){
                return chain.filter(exchange);

            }

        }

       //只要是api/inner的全部拒绝，因为是内部调用远程访问,所以全部拒绝
        for (String url : urlProperties.getDenyUrl()) {
            boolean match = matcher.match(url, path);
            if(match){
               // 直接响应json数据即可
                Result<String> result = Result.build("",
                        ResultCodeEnum.PERMISSION);
                return responseResult(result,exchange);

            }
        }

        //需要登录的请求，我们进行权限验证
        for (String url : urlProperties.getLoginAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //说明路径正确，获取token信息
                String tokenValue = getTokenValue(exchange);
                // 校验 token
                UserInfo info = getTokenUserInfo(tokenValue);
                //3.3、 判断用户信息是否正确
                if(info != null){
                    //Redis中有此用户。exchange里面的request的头会新增一个userid
                    ServerWebExchange webExchange = userIdTransport(info, exchange);
                    return chain.filter(webExchange);
                }else {
                    //redis中无此用户【假令牌、token没有，没登录】
                    //重定向到登录页

                    return redirectToCustomPage(urlProperties.getLoginPage()+"?originUrl="+uri,exchange);
                }
            }
        }
        /*
        * 走到这里，不是静态资源能够直接放行，也不是必须登录才能访问，是其他普通请求，类似于购物车，不用登录也能够访问
        * 这种普通请求只要是带了token就说明可能登陆了，那么久透传用户id
        * */
        String tokenValue = getTokenValue(exchange);
        UserInfo userInfo = getTokenUserInfo(tokenValue);
        if (userInfo!=null){
            exchange= userIdTransport(userInfo, exchange);
        }else{
            //但是前段带了token，还是没有解析出用户消息，说明是假令牌，
            if(!StringUtils.isEmpty(tokenValue)){
                //所以需要重定向到登录页面，重新登录，用来获得token，游览器所携带的token必须正确
                return  redirectToCustomPage(urlProperties.getLoginPage()+"?originUrl="+uri,exchange);

            }
        }

        return chain.filter(exchange);
    }
     /*
     *
     * */
    private Mono<Void> redirectToCustomPage(String location, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        //1、重定向【302状态码 + 响应头中 Location: 新位置】
        response.setStatusCode(HttpStatus.FOUND);
        // http://passport.gmall.com/login.html?originUrl=http://gmall.com/
        response.getHeaders().add(HttpHeaders.LOCATION,location);
        //2、清除旧的错误的Cookie[token]，解决无限重定向问题
        ResponseCookie tokenCookie = ResponseCookie
                .from("token", "777")        //设置新的cookie同样叫做token，这样就能够覆盖之前的错误token了
                .maxAge(0)                            //maAge 表示立即过期
                .path("/")                              //原来cookie的path
                .domain(".gmall.com")                   //在gmall。com的领域下
                .build();                               //最终构造一个新的cookie
        response.getCookies().set("token",tokenCookie);    //将新的cookie设置到响应结果中

        //3、响应结束
        return response.setComplete();

    }

    private ServerWebExchange userIdTransport(UserInfo info, ServerWebExchange exchange) {
        if(info != null){
            //请求一旦发来，所有的请求数据是固定的，不能进行任何修改，只能读取
            ServerHttpRequest request = exchange.getRequest();

            //根据原来的请求，封装一个新情求
            ServerHttpRequest newReq = exchange.getRequest()
                    .mutate() //变一个新的
                    .header(SysRedisConst.USERID_HEADER, info.getId().toString())
                    .build();//添加自己的头


            //放行的时候传改掉的exchange
            ServerWebExchange webExchange = exchange
                    .mutate()
                    .request(newReq)
                    .response(exchange.getResponse())
                    .build();
//            request.getHeaders().add();
            return webExchange;
        }
        return exchange;
    }

    /*
* 根据token的值去redis里面查询用户讯息*/
    private UserInfo getTokenUserInfo(String tokenValue) {
        String json = redisTemplate.opsForValue().get(SysRedisConst.LOGIN_USER + tokenValue);
        if(!StringUtils.isEmpty(json)){
            return Jsons.toObj(json,UserInfo.class);
        }
        return null;

    }

    /*
   *
   *
   * 从cookie或请求头中取到  token 对应的值*/
    private String getTokenValue(ServerWebExchange exchange) {

        //由于前端乱写，到处可能都有【Cookie[token=xxx]】【Header[token=xxx]】
        //1、先检查Cookie中有没有这个 token
        String tokenValue = "";
        HttpCookie token = exchange.getRequest()
                .getCookies()
                .getFirst("token");
        if(token != null){
            tokenValue = token.getValue();
            return tokenValue;
        }

        //2、说明cookie中没有
        tokenValue = exchange.getRequest()
                .getHeaders()
                .getFirst("token");

        return tokenValue;
    }

    private Mono<Void> responseResult(Result<String> result, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        String jsonStr = Jsons.toStr(result);

        //DataBuffer
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(jsonStr.getBytes());
        //Publisher<? extends DataBuffer> body

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(dataBuffer));
    }
}
