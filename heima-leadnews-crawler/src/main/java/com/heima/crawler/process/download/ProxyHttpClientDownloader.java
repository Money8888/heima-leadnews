package com.heima.crawler.process.download;

import com.heima.crawler.factory.CrawlerProxyFactory;
import com.heima.crawler.helper.CookieHelper;
import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.ProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.utils.SeleniumClient;
import com.heima.model.crawler.core.cookie.CrawlerCookie;
import com.heima.model.crawler.core.cookie.CrawlerHtml;
import com.heima.model.crawler.core.proxy.CrawlerProxy;
import com.heima.model.crawler.core.proxy.CrawlerProxyProvider;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class ProxyHttpClientDownloader extends AbstractDownloader implements ProcessFlow {

    @Autowired
    private CookieHelper cookieHelper;

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;

    @Autowired
    private SeleniumClient seleniumClient;


    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private ProxyProvider proxyProvider;

    private boolean responseHeader = true;

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }

        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }


    /**
     * webmagic 下载页面调用的方法入口
     *
     * @param request 请求的request
     * @param task    任务
     * @return
     */
    @Override
    public Page download(Request request, Task task) {
        String handelType = crawlerHelper.getHandelType(request);
        long currentTime = System.currentTimeMillis();
        log.info("开始下载页面数据，url:{},handelType:{}", request.getUrl(),handelType);
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        Site site = task.getSite();

        //设置代理对象
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(task) : null;
        //将 Proxy 转换为我们自己的 CrawlerProxy
        CrawlerProxy crawlerProxy = proxy == null ? null : new CrawlerProxy(proxy.getHost(), proxy.getPort());
        //添加Cookie
        addCookie(site, request.getUrl(), crawlerProxy);

        CloseableHttpClient httpClient = getHttpClient(site);
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());

            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);
            //验证httpClient返回的数据是否是正常格式

            boolean downloadStatus = checkDownloadStatus(page, crawlerProxy);
            //下载失败
            if (!downloadStatus) {
                page = seleniumDownload(page);
                downloadStatus = crawlerHelper.requestValidation(page);
            }

            if (downloadStatus) {
                page.setStatusCode(200);
                onSuccess(request);
                log.info("下载数据成功，url:{}，handelType:{},耗时：{}", request.getUrl(),handelType, System.currentTimeMillis() - currentTime);
            } else {
                onError(request);
                log.error("下载文档失败，url:{},handelType:{},proxy:{},状态码：{}", page.getUrl().toString(),handelType, proxy, page.getStatusCode());
            }


            return page;
        } catch (IOException e) {
            logger.warn("download page {} error", request.getUrl(), e);
            onError(request);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task);
            }
        }
    }

    /**
     * 校验下载状态
     *
     * @param page
     * @return
     */
    private boolean checkDownloadStatus(Page page, CrawlerProxy proxy) {
        boolean downloadStatus = false;
        if (page.getStatusCode() == 200) {
            downloadStatus = crawlerHelper.requestValidation(page);
        } else {
            crawlerProxyProvider.unavailable(proxy);
        }
        return downloadStatus;
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }


    //**********************************************以下代码是自定义的代码************************

    /**
     * 初始化webmagic的代理IP
     *
     * @param processFlowData
     */
    @Override
    public void handel(ProcessFlowData processFlowData) {
        Proxy[] proxies = getProxyArray(crawlerProxyProvider.getCrawlerProxyList());
        if (null != proxies && proxies.length > 0) {
            setProxyProvider(SimpleProxyProvider.from(proxies));
        }
    }


    /**
     * selenium+chrome headless 方式下载
     *
     * @param page
     */
    public Page seleniumDownload(Page page) {
        CrawlerHtml crawlerHtml = proxySeleniumDownloadRetry(page);
        boolean requestValidation = crawlerHelper.requestValidation(crawlerHtml);
        //校验失败
        if (!requestValidation) {
            //不使用代理尝试本地下载
            crawlerHtml = seleniumClient.getCrawlerHtml(page.getUrl().toString(), null, cookieHelper.getCookieName());
            requestValidation = crawlerHelper.requestValidation(crawlerHtml);
        }
        //如果校验成功成功
        if (requestValidation) {
            cookieHelper.updateCookie(crawlerHtml.getCrawlerCookieList(), crawlerHtml.getProxy());
            Html html = new Html(crawlerHtml.getHtml());
            page.setHtml(html);
        }
        return page;
    }

    /**
     * 使用代理方式进行下载重试
     *
     * @param page
     * @return
     */
    public CrawlerHtml proxySeleniumDownloadRetry(Page page) {
        CrawlerHtml crawlerHtml = null;
        for (int i = 0; i < 3; i++) {
            long currentTime = System.currentTimeMillis();
            CrawlerProxy proxy = crawlerProxyProvider.getRandomProxy();
            log.info("尝试使用selenium下载数据第{}次，url:{}，代理：{}", i + 1, page.getUrl(), proxy);
            crawlerHtml = seleniumClient.getCrawlerHtml(page.getUrl().toString(), proxy, cookieHelper.getCookieName());
            log.info("尝试使用selenium下载数据第{}次完成，代理：{}，url:{}，耗时：{}", i, proxy, page.getUrl(), System.currentTimeMillis() - currentTime);
            if (StringUtils.isNotEmpty(crawlerHtml.getHtml())) {
                break;
            }
            //该代理不可用禁用
            crawlerProxyProvider.unavailable(proxy);
        }
        return crawlerHtml;
    }


    /**
     * 根据代理Ip 添加Cookie
     *
     * @param site
     * @param url
     * @param proxy
     */
    private void addCookie(Site site, String url, CrawlerProxy proxy) {
        List<CrawlerCookie> crawlerCookieList = cookieHelper.getCacheCookieList(url, proxy);
        if (null != site && null != crawlerCookieList && !crawlerCookieList.isEmpty()) {
            for (CrawlerCookie crawlerCookie : crawlerCookieList) {
                if (null != crawlerCookie) {
                    site.addCookie(crawlerCookie.getName(), crawlerCookie.getValue());
                }
            }
        }
    }


    /**
     * 获取代理数组
     *
     * @param crawlerProxyList
     * @return
     */
    private Proxy[] getProxyArray(List<CrawlerProxy> crawlerProxyList) {
        Proxy[] proxyArray = null;
        if (null != crawlerProxyList && !crawlerProxyList.isEmpty()) {
            proxyArray = new Proxy[crawlerProxyList.size()];
            for (int i = 0; i < crawlerProxyList.size(); i++) {
                proxyArray[i] = CrawlerProxyFactory.getWebmagicProxy(crawlerProxyList.get(i));
            }
        }
        return proxyArray;
    }


    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.DOWNLOAD;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}